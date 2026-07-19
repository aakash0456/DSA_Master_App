package com.example.dsamaster.core.seed

import com.example.dsamaster.core.database.DsaDatabase
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Seeds built-in content on first launch. A Kotlin seed provider was chosen over
 * a pre-populated .db asset because it survives schema refactors at compile time
 * and keeps content reviewable in code review / version control.
 */
@Singleton
class DatabaseSeeder @Inject constructor(private val db: DsaDatabase) {

    suspend fun seedIfEmpty() {
        if (db.topicDao().count() > 0) return
        db.topicDao().insertAll(SeedData.topics)
        db.lessonDao().insertAll(SeedData.lessons)
        db.quizDao().insertQuizzes(SeedData.quizzes)
        db.quizDao().insertQuestions(SeedData.questions)
        db.problemDao().insertAll(SeedData.problems)
        db.patternDao().insertAll(SeedData.patterns)
        val deckId = db.flashcardDao().insertDeck(SeedData.builtInDeck)
        db.flashcardDao().insertCards(SeedData.cards.map { it.copy(deckId = deckId) })
    }
}
