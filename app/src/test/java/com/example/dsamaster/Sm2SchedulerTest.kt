package com.example.dsamaster

import com.example.dsamaster.domain.Sm2Scheduler
import com.example.dsamaster.domain.Sm2Scheduler.Quality
import com.example.dsamaster.domain.Sm2Scheduler.State
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class Sm2SchedulerTest {

    @Test
    fun `first successful review schedules one day`() {
        val result = Sm2Scheduler.review(State(), Quality.GOOD)
        assertEquals(1, result.state.repetition)
        assertEquals(1, result.nextDueInDays)
    }

    @Test
    fun `second successful review schedules six days`() {
        val first = Sm2Scheduler.review(State(), Quality.GOOD)
        val second = Sm2Scheduler.review(first.state, Quality.GOOD)
        assertEquals(2, second.state.repetition)
        assertEquals(6, second.nextDueInDays)
    }

    @Test
    fun `third review multiplies interval by ease factor`() {
        var state = State()
        repeat(2) { state = Sm2Scheduler.review(state, Quality.GOOD).state }
        val third = Sm2Scheduler.review(state, Quality.GOOD)
        // interval = round(6 * ease). Ease after two GOOD reviews stays at 2.5.
        assertEquals(15, third.nextDueInDays)
    }

    @Test
    fun `again resets repetition and schedules tomorrow`() {
        var state = State()
        repeat(3) { state = Sm2Scheduler.review(state, Quality.GOOD).state }
        val lapsed = Sm2Scheduler.review(state, Quality.AGAIN)
        assertEquals(0, lapsed.state.repetition)
        assertEquals(1, lapsed.nextDueInDays)
        assertEquals(state.lapses + 1, lapsed.state.lapses)
    }

    @Test
    fun `again lowers ease factor`() {
        val before = State(easeFactor = 2.5)
        val after = Sm2Scheduler.review(before, Quality.AGAIN).state
        assertTrue(after.easeFactor < before.easeFactor)
    }

    @Test
    fun `ease factor never drops below 1_3`() {
        var state = State(easeFactor = 1.3)
        repeat(10) { state = Sm2Scheduler.review(state, Quality.AGAIN).state }
        assertTrue(state.easeFactor >= 1.3)
    }

    @Test
    fun `easy grows ease factor`() {
        val before = State(easeFactor = 2.5)
        val after = Sm2Scheduler.review(before, Quality.EASY).state
        assertTrue(after.easeFactor > before.easeFactor)
    }

    @Test
    fun `hard passes but slows growth relative to good`() {
        var hard = State(); var good = State()
        repeat(3) {
            hard = Sm2Scheduler.review(hard, Quality.HARD).state
            good = Sm2Scheduler.review(good, Quality.GOOD).state
        }
        assertTrue(hard.intervalDays <= good.intervalDays)
        assertEquals(3, hard.repetition)   // HARD still counts as a pass
    }

    @Test
    fun `mastery at 21 day interval`() {
        assertTrue(Sm2Scheduler.isMastered(State(intervalDays = 21)))
        assertTrue(!Sm2Scheduler.isMastered(State(intervalDays = 20)))
    }
}
