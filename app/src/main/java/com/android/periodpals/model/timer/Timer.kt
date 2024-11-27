package com.android.periodpals.model.timer

/**
 * Data class representing a timer.
 *
 * @property timerID The unique identifier of the timer, given when created in Supabase.
 * @property userID The user ID associated with the timer.
 * @property time The time of the timer.
 */
data class Timer(
    val timerID: String?, // given when created in supabase
    val userID: String,
    val time: Int
)
