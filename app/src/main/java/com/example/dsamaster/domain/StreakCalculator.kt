package com.example.dsamaster.domain

data class StreakInfo(val current: Int, val longest: Int)

/** Computes streaks from a sorted-ascending list of active epoch days. */
object StreakCalculator {
    fun fromActiveDays(activeDays: List<Long>, today: Long): StreakInfo {
        if (activeDays.isEmpty()) return StreakInfo(0, 0)
        val days = activeDays.distinct().sorted()

        var longest = 1
        var run = 1
        for (i in 1 until days.size) {
            run = if (days[i] == days[i - 1] + 1) run + 1 else 1
            if (run > longest) longest = run
        }

        // Current streak: consecutive days ending today or yesterday.
        val set = days.toHashSet()
        var anchor = when {
            today in set -> today
            (today - 1) in set -> today - 1
            else -> return StreakInfo(0, longest)
        }
        var current = 0
        while (anchor in set) { current++; anchor-- }
        return StreakInfo(current, maxOf(longest, current))
    }
}
