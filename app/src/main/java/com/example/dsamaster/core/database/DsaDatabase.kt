package com.example.dsamaster.core.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        TopicEntity::class, LessonEntity::class, DeckEntity::class, FlashcardEntity::class,
        ReviewStateEntity::class, QuizEntity::class, QuizQuestionEntity::class,
        QuizAttemptEntity::class, CodingProblemEntity::class, StudyDayEntity::class,
        PatternEntity::class,
    ],
    version = 3,
    exportSchema = false
)
abstract class DsaDatabase : RoomDatabase() {
    abstract fun topicDao(): TopicDao
    abstract fun lessonDao(): LessonDao
    abstract fun flashcardDao(): FlashcardDao
    abstract fun quizDao(): QuizDao
    abstract fun problemDao(): ProblemDao
    abstract fun patternDao(): PatternDao
    abstract fun studyDayDao(): StudyDayDao
}
