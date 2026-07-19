package com.example.dsamaster.domain

import kotlin.math.roundToInt

/**
 * SM-2 spaced repetition (SuperMemo 2, Wozniak 1990).
 *
 * Each card stores (repetition, intervalDays, easeFactor). After a review the
 * user grades recall quality q in 0..5. We map the four buttons to:
 *   Again = 0 (failed), Hard = 3, Good = 4, Easy = 5.
 *
 * Rules:
 *  - q < 3  → lapse: repetition resets to 0 and the card comes back tomorrow.
 *             The ease factor is still updated (it drops), so lapsed cards
 *             grow their intervals more slowly in future.
 *  - q >= 3 → success:
 *        repetition 1 → interval 1 day
 *        repetition 2 → interval 6 days
 *        repetition n → previous interval × easeFactor (rounded)
 *  - easeFactor' = ef + (0.1 − (5−q)·(0.08 + (5−q)·0.02)), clamped to ≥ 1.3.
 */
object Sm2Scheduler {

    enum class Quality(val score: Int, val label: String) {
        AGAIN(0, "Again"), HARD(3, "Hard"), GOOD(4, "Good"), EASY(5, "Easy")
    }

    data class State(
        val repetition: Int = 0,
        val intervalDays: Int = 0,
        val easeFactor: Double = 2.5,
        val lapses: Int = 0,
    )

    data class Result(val state: State, val nextDueInDays: Int)

    fun review(current: State, quality: Quality): Result {
        val q = quality.score
        val newEase = (current.easeFactor + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02)))
            .coerceAtLeast(1.3)

        return if (q < 3) {
            val state = State(
                repetition = 0,
                intervalDays = 1,
                easeFactor = newEase,
                lapses = current.lapses + 1,
            )
            Result(state, nextDueInDays = 1)
        } else {
            val repetition = current.repetition + 1
            val interval = when (repetition) {
                1 -> 1
                2 -> 6
                else -> (current.intervalDays * newEase).roundToInt().coerceAtLeast(1)
            }
            Result(State(repetition, interval, newEase, current.lapses), nextDueInDays = interval)
        }
    }

    /** A card is "mastered" once its interval reaches three weeks. */
    fun isMastered(state: State): Boolean = state.intervalDays >= 21
}
