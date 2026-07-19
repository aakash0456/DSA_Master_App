package com.example.dsamaster

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.dsamaster.core.database.DeckEntity
import com.example.dsamaster.core.database.DsaDatabase
import com.example.dsamaster.core.database.FlashcardEntity
import com.example.dsamaster.core.database.ReviewStateEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FlashcardDaoTest {

    private lateinit var db: DsaDatabase

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), DsaDatabase::class.java
        ).build()
    }

    @After
    fun teardown() = db.close()

    @Test
    fun newCardsAreDue_andScheduledCardsRespectDueDay() = runBlocking {
        val dao = db.flashcardDao()
        val deckId = dao.insertDeck(DeckEntity(name = "Test", description = "", isBuiltIn = false))
        val cardId = dao.insertCard(
            FlashcardEntity(deckId = deckId, topicSlug = null, question = "Q", answer = "A")
        )

        // A card without review state is due immediately.
        assertEquals(1, dao.dueCards(today = 100).size)

        // Schedule it three days into the future — no longer due today.
        dao.upsertState(
            ReviewStateEntity(cardId = cardId, repetition = 1, intervalDays = 3,
                easeFactor = 2.5, lastReviewedDay = 100, dueDay = 103, lapses = 0)
        )
        assertEquals(0, dao.dueCards(today = 100).size)
        assertEquals(1, dao.dueCards(today = 103).size)
    }
}
