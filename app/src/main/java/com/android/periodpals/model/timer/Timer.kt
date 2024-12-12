package com.android.periodpals.model.timer

import java.util.UUID

/**
 * Data class representing a timer.
 *
 * @property id The unique identifier of the timer, generated when timer is created.
 * @property time The time of the timer.
 * @property instructionText The instruction text associated with the timer.
 */
data class Timer(
    val id: String = UUID.randomUUID().toString(),
    val time: Long?,
    val instructionText: String?
)
