package com.example.dsamaster.core.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Schema overview
 *  topics 1—N lessons, quizzes, problems
 *  decks  1—N flashcards 1—1 review_states (SM-2 scheduling per card)
 *  quizzes 1—N quiz_questions, 1—N quiz_attempts
 *  study_days: one row per calendar day; drives streaks + heatmap.
 */

@Entity(tableName = "topics")
data class TopicEntity(
    @PrimaryKey val id: Long,
    val slug: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val analogy: String,
    val orderIndex: Int,
)

@Entity(
    tableName = "lessons",
    foreignKeys = [ForeignKey(entity = TopicEntity::class, parentColumns = ["id"], childColumns = ["topicId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("topicId")]
)
data class LessonEntity(
    @PrimaryKey val id: Long,
    val topicId: Long,
    val title: String,
    val orderIndex: Int,
    /** Lightweight markup: "# " heading, "- " bullet, "> " callout, "| " table row, ``` code fence. */
    val body: String,
    val isCompleted: Boolean = false,
    val isBookmarked: Boolean = false,
)

@Entity(tableName = "decks")
data class DeckEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val isBuiltIn: Boolean,
)

@Entity(
    tableName = "flashcards",
    foreignKeys = [ForeignKey(entity = DeckEntity::class, parentColumns = ["id"], childColumns = ["deckId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("deckId")]
)
data class FlashcardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deckId: Long,
    val topicSlug: String?,
    val question: String,
    val answer: String,
    val explanation: String = "",
    /** 1 easy, 2 medium, 3 hard */
    val difficulty: Int = 2,
    val codeSnippet: String? = null,
    val isBookmarked: Boolean = false,
)

/** One row per card once it has been reviewed at least once. SM-2 state. */
@Entity(
    tableName = "review_states",
    foreignKeys = [ForeignKey(entity = FlashcardEntity::class, parentColumns = ["id"], childColumns = ["cardId"], onDelete = ForeignKey.CASCADE)]
)
data class ReviewStateEntity(
    @PrimaryKey val cardId: Long,
    val repetition: Int,
    val intervalDays: Int,
    val easeFactor: Double,
    val lastReviewedDay: Long,
    val dueDay: Long,
    val lapses: Int,
)

@Entity(
    tableName = "quizzes",
    foreignKeys = [ForeignKey(entity = TopicEntity::class, parentColumns = ["id"], childColumns = ["topicId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("topicId")]
)
data class QuizEntity(
    @PrimaryKey val id: Long,
    val topicId: Long,
    val title: String,
    val difficulty: Int,
)

@Entity(
    tableName = "quiz_questions",
    foreignKeys = [ForeignKey(entity = QuizEntity::class, parentColumns = ["id"], childColumns = ["quizId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("quizId")]
)
data class QuizQuestionEntity(
    @PrimaryKey val id: Long,
    val quizId: Long,
    /** "mcq", "tf" or "code" (code-output question). */
    val type: String,
    val prompt: String,
    val codeSnippet: String? = null,
    /** Options separated by '|' — "True|False" for tf questions. */
    val options: String,
    val correctIndex: Int,
    val explanation: String,
)

@Entity(tableName = "quiz_attempts", indices = [Index("quizId")])
data class QuizAttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val quizId: Long,
    val score: Int,
    val total: Int,
    val timestampMillis: Long,
)

@Entity(
    tableName = "problems",
    foreignKeys = [ForeignKey(entity = TopicEntity::class, parentColumns = ["id"], childColumns = ["topicId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("topicId")]
)
data class CodingProblemEntity(
    @PrimaryKey val id: Long,
    val topicId: Long,
    val title: String,
    val statement: String,
    val examples: String,
    val constraints: String,
    /** Hints separated by '|', revealed one at a time. */
    val hints: String,
    val solutionKotlin: String,
    val explanation: String,
    val timeComplexity: String,
    val spaceComplexity: String,
    val difficulty: Int,
    /** Algorithmic pattern this problem trains, e.g. "Hashing", "Stack". */
    val pattern: String = "General",
    /** Signals in the statement that reveal the pattern, '|'-separated. */
    val patternClues: String = "",
    /** Decision walkthrough from observation to chosen algorithm, '|'-separated steps. */
    val approach: String = "",
    val isSolved: Boolean = false,
    val isBookmarked: Boolean = false,
    val notes: String = "",
)

/** A reusable algorithmic pattern taught in the Pattern Playbook. */
@Entity(tableName = "patterns")
data class PatternEntity(
    @PrimaryKey val id: Long,
    val name: String,
    /** One-line summary of the idea. */
    val tagline: String,
    /** How the technique works, in plain language. */
    val description: String,
    /** Statement signals that point to this pattern, '|'-separated. */
    val signals: String,
    /** Signs it is probably the WRONG pattern, '|'-separated. */
    val antiSignals: String,
    /** Kotlin skeleton showing the shape of the technique. */
    val template: String,
)

/** One row per active day — powers streaks, heatmap and weekly chart. */
@Entity(tableName = "study_days")
data class StudyDayEntity(
    @PrimaryKey val epochDay: Long,
    val lessonsCompleted: Int = 0,
    val cardsReviewed: Int = 0,
    val questionsAnswered: Int = 0,
)
