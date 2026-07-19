package com.example.dsamaster.domain

import com.example.dsamaster.core.database.CodingProblemEntity
import com.example.dsamaster.core.database.PatternEntity
import com.example.dsamaster.core.database.DeckEntity
import com.example.dsamaster.core.database.FlashcardEntity
import com.example.dsamaster.core.database.LessonEntity
import com.example.dsamaster.core.database.QuizAttemptEntity
import com.example.dsamaster.core.database.QuizEntity
import com.example.dsamaster.core.database.QuizQuestionEntity
import com.example.dsamaster.core.database.StudyDayEntity
import com.example.dsamaster.core.database.TopicEntity
import kotlinx.coroutines.flow.Flow

interface ContentRepository {
    fun topics(): Flow<List<TopicEntity>>
    fun topic(id: Long): Flow<TopicEntity?>
    fun lessonsForTopic(topicId: Long): Flow<List<LessonEntity>>
    fun lesson(id: Long): Flow<LessonEntity?>
    fun allLessons(): Flow<List<LessonEntity>>
    fun completedLessonCount(): Flow<Int>
    fun totalLessonCount(): Flow<Int>
    suspend fun setLessonCompleted(id: Long, done: Boolean)
    suspend fun setLessonBookmarked(id: Long, bookmarked: Boolean)

    fun quizzesForTopic(topicId: Long): Flow<List<QuizEntity>>
    suspend fun quiz(id: Long): QuizEntity?
    suspend fun questions(quizId: Long): List<QuizQuestionEntity>
    suspend fun recordAttempt(attempt: QuizAttemptEntity)
    fun attempts(): Flow<List<QuizAttemptEntity>>

    fun problems(): Flow<List<CodingProblemEntity>>
    fun problemsForTopic(topicId: Long): Flow<List<CodingProblemEntity>>
    fun problem(id: Long): Flow<CodingProblemEntity?>
    fun solvedProblemCount(): Flow<Int>
    suspend fun setProblemSolved(id: Long, solved: Boolean)
    suspend fun setProblemBookmarked(id: Long, bookmarked: Boolean)
    suspend fun setProblemNotes(id: Long, notes: String)

    fun patterns(): Flow<List<PatternEntity>>
    fun pattern(id: Long): Flow<PatternEntity?>
    fun problemsByPattern(pattern: String): Flow<List<CodingProblemEntity>>

    suspend fun search(query: String): SearchResults
}

data class SearchResults(
    val topics: List<TopicEntity> = emptyList(),
    val lessons: List<LessonEntity> = emptyList(),
    val cards: List<FlashcardEntity> = emptyList(),
    val problems: List<CodingProblemEntity> = emptyList(),
)

interface FlashcardRepository {
    fun decks(): Flow<List<DeckEntity>>
    fun cards(deckId: Long): Flow<List<FlashcardEntity>>
    fun dueCount(): Flow<Int>
    fun masteredCount(): Flow<Int>
    fun cardCount(): Flow<Int>
    suspend fun createDeck(name: String, description: String): Long
    suspend fun deleteDeck(id: Long)
    suspend fun card(id: Long): FlashcardEntity?
    suspend fun saveCard(card: FlashcardEntity)
    suspend fun deleteCard(card: FlashcardEntity)
    suspend fun dueCards(): List<FlashcardEntity>
    /** Applies SM-2 and persists the new schedule. Returns days until next review. */
    suspend fun rate(cardId: Long, quality: Sm2Scheduler.Quality): Int
}

interface ActivityRepository {
    fun studyDays(): Flow<List<StudyDayEntity>>
    suspend fun recordLessonCompleted()
    suspend fun recordCardsReviewed(count: Int)
    suspend fun recordQuestionsAnswered(count: Int)
    suspend fun resetAllProgress()
}
