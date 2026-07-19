package com.example.dsamaster

import com.example.dsamaster.domain.StreakCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

class StreakCalculatorTest {

    @Test
    fun `empty history gives zero streaks`() {
        val info = StreakCalculator.fromActiveDays(emptyList(), today = 100)
        assertEquals(0, info.current)
        assertEquals(0, info.longest)
    }

    @Test
    fun `streak ending today counts`() {
        val info = StreakCalculator.fromActiveDays(listOf(98, 99, 100), today = 100)
        assertEquals(3, info.current)
        assertEquals(3, info.longest)
    }

    @Test
    fun `streak ending yesterday still counts as current`() {
        val info = StreakCalculator.fromActiveDays(listOf(98, 99), today = 100)
        assertEquals(2, info.current)
    }

    @Test
    fun `gap before yesterday breaks current streak but keeps longest`() {
        val info = StreakCalculator.fromActiveDays(listOf(90, 91, 92, 93, 98), today = 100)
        assertEquals(0, info.current)
        assertEquals(4, info.longest)
    }

    @Test
    fun `duplicate days are ignored`() {
        val info = StreakCalculator.fromActiveDays(listOf(100, 100, 99), today = 100)
        assertEquals(2, info.current)
    }
}
