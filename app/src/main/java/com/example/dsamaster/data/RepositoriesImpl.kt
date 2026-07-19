package com.example.dsamaster.data

import com.example.dsamaster.core.database.CodingProblemEntity
import com.example.dsamaster.core.database.DeckEntity
import com.example.dsamaster.core.database.FlashcardDao
import com.example.dsamaster.core.database.FlashcardEntity
import com.example.dsamaster.core.database.LessonDao
import com.example.dsamaster.core.database.PatternDao
import com.example.dsamaster.core.database.ProblemDao
import com.example.dsamaster.core.database.QuizAttemptEntity
import com.example.dsamaster.core.database.QuizDao
import com.example.dsamaster.core.database.ReviewStateEntity
import com.example.dsamaster.core.database.StudyDayDao
import com.example.dsamaster.core.database.StudyDayEntity
import com.example.dsamaster.core.database.TopicDao
import com.example.dsamaster.domain.ActivityRepository
import com.example.dsamaster.domain.ContentRepository
import com.example.dsamaster.domain.FlashcardRepository
import com.example.dsamaster.domain.SearchResults
import com.example.dsamaster.domain.Sm2Scheduler
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContentRepositoryImpl @Inject constructor(
    private val topicDao: TopicDao,
    private val lessonDao: LessonDao,
    private val quizDao: QuizDao,
    private val problemDao: ProblemDao,
    private val patternDao: PatternDao,
    private val flashcardDao: FlashcardDao,
) : ContentRepository {
    override fun patterns() = patternDao.patterns()
    override fun pattern(id: Long) = patternDao.pattern(id)
    override fun problemsByPattern(pattern: String) = problemDao.problemsByPattern(pattern)
    override fun topics() = topicDao.topics()
    override fun topic(id: Long) = topicDao.topic(id)
    override fun lessonsForTopic(topicId: Long) = lessonDao.lessonsForTopic(topicId)
    override fun lesson(id: Long) = lessonDao.lesson(id)
    override fun allLessons() = lessonDao.allLessons()
    override fun completedLessonCount() = lessonDao.completedCount()
    override fun totalLessonCount() = lessonDao.totalCount()
    override suspend fun setLessonCompleted(id: Long, done: Boolean) = lessonDao.setCompleted(id, done)
    override suspend fun setLessonBookmarked(id: Long, bookmarked: Boolean) = lessonDao.setBookmarked(id, bookmarked)

    override fun quizzesForTopic(topicId: Long) = quizDao.quizzesForTopic(topicId)
    override suspend fun quiz(id: Long) = quizDao.quiz(id)
    override suspend fun questions(quizId: Long) = quizDao.questions(quizId)
    override suspend fun recordAttempt(attempt: QuizAttemptEntity) = quizDao.insertAttempt(attempt)
    override fun attempts() = quizDao.attempts()

    override fun problems() = problemDao.problems()
    override fun problemsForTopic(topicId: Long) = problemDao.problemsForTopic(topicId)
    override fun problem(id: Long) = problemDao.problem(id)
    override fun solvedProblemCount() = problemDao.solvedCount()
    override suspend fun setProblemSolved(id: Long, solved: Boolean) = problemDao.setSolved(id, solved)
    override suspend fun setProblemBookmarked(id: Long, bookmarked: Boolean) = problemDao.setBookmarked(id, bookmarked)
    override suspend fun setProblemNotes(id: Long, notes: String) = problemDao.setNotes(id, notes)

    override suspend fun search(query: String): SearchResults {
        if (query.isBlank()) return SearchResults()
        return SearchResults(
            topics = topicDao.search(query),
            lessons = lessonDao.search(query),
            cards = flashcardDao.search(query),
            problems = problemDao.search(query),
        )
    }
}

@Singleton
class FlashcardRepositoryImpl @Inject constructor(
    private val dao: FlashcardDao,
) : FlashcardRepository {
    private fun today(): Long = LocalDate.now().toEpochDay()

    override fun decks() = dao.decks()
    override fun cards(deckId: Long) = dao.cards(deckId)
    override fun dueCount(): Flow<Int> = dao.dueCount(today())
    override fun masteredCount() = dao.masteredCount()
    override fun cardCount() = dao.cardCount()
    override suspend fun createDeck(name: String, description: String) =
        dao.insertDeck(DeckEntity(name = name, description = description, isBuiltIn = false))
    override suspend fun deleteDeck(id: Long) = dao.deleteCustomDeck(id)
    override suspend fun card(id: Long) = dao.card(id)
    override suspend fun saveCard(card: FlashcardEntity) {
        if (card.id == 0L) dao.insertCard(card) else dao.updateCard(card)
    }
    override suspend fun deleteCard(card: FlashcardEntity) = dao.deleteCard(card)
    override suspend fun dueCards() = dao.dueCards(today())

    override suspend fun rate(cardId: Long, quality: Sm2Scheduler.Quality): Int {
        val existing = dao.state(cardId)
        val current = if (existing == null) Sm2Scheduler.State() else Sm2Scheduler.State(
            repetition = existing.repetition,
            intervalDays = existing.intervalDays,
            easeFactor = existing.easeFactor,
            lapses = existing.lapses,
        )
        val result = Sm2Scheduler.review(current, quality)
        val today = today()
        dao.upsertState(
            ReviewStateEntity(
                cardId = cardId,
                repetition = result.state.repetition,
                intervalDays = result.state.intervalDays,
                easeFactor = result.state.easeFactor,
                lastReviewedDay = today,
                dueDay = today + result.nextDueInDays,
                lapses = result.state.lapses,
            )
        )
        return result.nextDueInDays
    }
}

@Singleton
class ActivityRepositoryImpl @Inject constructor(
    private val studyDayDao: StudyDayDao,
    private val lessonDao: LessonDao,
    private val problemDao: ProblemDao,
    private val quizDao: QuizDao,
    private val flashcardDao: FlashcardDao,
) : ActivityRepository {
    private fun today(): Long = LocalDate.now().toEpochDay()

    override fun studyDays() = studyDayDao.all()

    private suspend fun update(transform: (StudyDayEntity) -> StudyDayEntity) {
        val today = today()
        val row = studyDayDao.day(today) ?: StudyDayEntity(epochDay = today)
        studyDayDao.upsert(transform(row))
    }

    override suspend fun recordLessonCompleted() = update { it.copy(lessonsCompleted = it.lessonsCompleted + 1) }
    override suspend fun recordCardsReviewed(count: Int) = update { it.copy(cardsReviewed = it.cardsReviewed + count) }
    override suspend fun recordQuestionsAnswered(count: Int) = update { it.copy(questionsAnswered = it.questionsAnswered + count) }

    override suspend fun resetAllProgress() {
        studyDayDao.reset()
        lessonDao.resetAll()
        problemDao.resetAll()
        quizDao.resetAttempts()
        flashcardDao.resetReviewStates()
    }
}
