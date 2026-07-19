package com.example.dsamaster.core.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TopicDao {
    @Query("SELECT * FROM topics ORDER BY orderIndex") fun topics(): Flow<List<TopicEntity>>
    @Query("SELECT * FROM topics WHERE id = :id") fun topic(id: Long): Flow<TopicEntity?>
    @Query("SELECT COUNT(*) FROM topics") suspend fun count(): Int
    @Query("SELECT * FROM topics WHERE title LIKE '%' || :q || '%' OR description LIKE '%' || :q || '%' ORDER BY orderIndex")
    suspend fun search(q: String): List<TopicEntity>
    @Insert suspend fun insertAll(items: List<TopicEntity>)
}

@Dao
interface LessonDao {
    @Query("SELECT * FROM lessons WHERE topicId = :topicId ORDER BY orderIndex")
    fun lessonsForTopic(topicId: Long): Flow<List<LessonEntity>>
    @Query("SELECT * FROM lessons WHERE id = :id") fun lesson(id: Long): Flow<LessonEntity?>
    @Query("SELECT * FROM lessons ORDER BY topicId, orderIndex") fun allLessons(): Flow<List<LessonEntity>>
    @Query("UPDATE lessons SET isCompleted = :done WHERE id = :id") suspend fun setCompleted(id: Long, done: Boolean)
    @Query("UPDATE lessons SET isBookmarked = :b WHERE id = :id") suspend fun setBookmarked(id: Long, b: Boolean)
    @Query("SELECT COUNT(*) FROM lessons WHERE isCompleted = 1") fun completedCount(): Flow<Int>
    @Query("SELECT COUNT(*) FROM lessons") fun totalCount(): Flow<Int>
    @Query("UPDATE lessons SET isCompleted = 0, isBookmarked = 0") suspend fun resetAll()
    @Query("SELECT * FROM lessons WHERE title LIKE '%' || :q || '%' LIMIT 20") suspend fun search(q: String): List<LessonEntity>
    @Insert suspend fun insertAll(items: List<LessonEntity>)
}

@Dao
interface FlashcardDao {
    @Query("SELECT * FROM decks ORDER BY isBuiltIn DESC, name") fun decks(): Flow<List<DeckEntity>>
    @Insert suspend fun insertDeck(deck: DeckEntity): Long
    @Query("DELETE FROM decks WHERE id = :id AND isBuiltIn = 0") suspend fun deleteCustomDeck(id: Long)

    @Query("SELECT * FROM flashcards WHERE deckId = :deckId ORDER BY id") fun cards(deckId: Long): Flow<List<FlashcardEntity>>
    @Query("SELECT * FROM flashcards WHERE id = :id") suspend fun card(id: Long): FlashcardEntity?
    @Insert suspend fun insertCard(card: FlashcardEntity): Long
    @Insert suspend fun insertCards(cards: List<FlashcardEntity>)
    @Update suspend fun updateCard(card: FlashcardEntity)
    @Delete suspend fun deleteCard(card: FlashcardEntity)
    @Query("SELECT * FROM flashcards WHERE question LIKE '%' || :q || '%' OR answer LIKE '%' || :q || '%' LIMIT 20")
    suspend fun search(q: String): List<FlashcardEntity>

    /** Cards never reviewed, or due today or earlier. */
    @Query(
        "SELECT f.* FROM flashcards f LEFT JOIN review_states r ON r.cardId = f.id " +
            "WHERE r.cardId IS NULL OR r.dueDay <= :today ORDER BY r.dueDay"
    )
    suspend fun dueCards(today: Long): List<FlashcardEntity>

    @Query(
        "SELECT COUNT(*) FROM flashcards f LEFT JOIN review_states r ON r.cardId = f.id " +
            "WHERE r.cardId IS NULL OR r.dueDay <= :today"
    )
    fun dueCount(today: Long): Flow<Int>

    @Query("SELECT * FROM review_states WHERE cardId = :cardId") suspend fun state(cardId: Long): ReviewStateEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertState(state: ReviewStateEntity)
    @Query("SELECT COUNT(*) FROM review_states WHERE intervalDays >= 21") fun masteredCount(): Flow<Int>
    @Query("SELECT COUNT(*) FROM flashcards") fun cardCount(): Flow<Int>
    @Query("DELETE FROM review_states") suspend fun resetReviewStates()
}

@Dao
interface QuizDao {
    @Query("SELECT * FROM quizzes WHERE topicId = :topicId") fun quizzesForTopic(topicId: Long): Flow<List<QuizEntity>>
    @Query("SELECT * FROM quizzes WHERE id = :id") suspend fun quiz(id: Long): QuizEntity?
    @Query("SELECT * FROM quiz_questions WHERE quizId = :quizId") suspend fun questions(quizId: Long): List<QuizQuestionEntity>
    @Insert suspend fun insertAttempt(attempt: QuizAttemptEntity)
    @Query("SELECT * FROM quiz_attempts ORDER BY timestampMillis DESC") fun attempts(): Flow<List<QuizAttemptEntity>>
    @Query("DELETE FROM quiz_attempts") suspend fun resetAttempts()
    @Insert suspend fun insertQuizzes(items: List<QuizEntity>)
    @Insert suspend fun insertQuestions(items: List<QuizQuestionEntity>)
}

@Dao
interface ProblemDao {
    @Query("SELECT * FROM problems ORDER BY topicId, difficulty") fun problems(): Flow<List<CodingProblemEntity>>
    @Query("SELECT * FROM problems WHERE topicId = :topicId ORDER BY difficulty")
    fun problemsForTopic(topicId: Long): Flow<List<CodingProblemEntity>>
    @Query("SELECT * FROM problems WHERE id = :id") fun problem(id: Long): Flow<CodingProblemEntity?>
    @Query("UPDATE problems SET isSolved = :v WHERE id = :id") suspend fun setSolved(id: Long, v: Boolean)
    @Query("UPDATE problems SET isBookmarked = :v WHERE id = :id") suspend fun setBookmarked(id: Long, v: Boolean)
    @Query("UPDATE problems SET notes = :notes WHERE id = :id") suspend fun setNotes(id: Long, notes: String)
    @Query("SELECT COUNT(*) FROM problems WHERE isSolved = 1") fun solvedCount(): Flow<Int>
    @Query("UPDATE problems SET isSolved = 0, isBookmarked = 0, notes = ''") suspend fun resetAll()
    @Query("SELECT * FROM problems WHERE title LIKE '%' || :q || '%' OR statement LIKE '%' || :q || '%' LIMIT 20")
    suspend fun search(q: String): List<CodingProblemEntity>
    @Query("SELECT * FROM problems WHERE pattern = :pattern ORDER BY difficulty")
    fun problemsByPattern(pattern: String): Flow<List<CodingProblemEntity>>
    @Insert suspend fun insertAll(items: List<CodingProblemEntity>)
}

@Dao
interface PatternDao {
    @Query("SELECT * FROM patterns ORDER BY id") fun patterns(): Flow<List<PatternEntity>>
    @Query("SELECT * FROM patterns WHERE id = :id") fun pattern(id: Long): Flow<PatternEntity?>
    @Query("SELECT COUNT(*) FROM patterns") suspend fun count(): Int
    @Insert suspend fun insertAll(items: List<PatternEntity>)
}

@Dao
interface StudyDayDao {
    @Query("SELECT * FROM study_days ORDER BY epochDay") fun all(): Flow<List<StudyDayEntity>>
    @Query("SELECT * FROM study_days WHERE epochDay = :day") suspend fun day(day: Long): StudyDayEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(day: StudyDayEntity)
    @Query("DELETE FROM study_days") suspend fun reset()
}
