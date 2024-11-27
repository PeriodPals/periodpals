package com.android.periodpals.model.timer

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) for timer data.
 *
 * @property timerID The unique identifier of the timer, given when created in Supabase.
 * @property userID The user ID associated with the timer.
 * @property time The time of the timer.
 */
@Serializable
data class TimerDto(
    @SerialName("timerID") val timerID: String?,
    @SerialName("userID") val userID: String,
    @SerialName("time") val time: Int
) {
  /** Converts the timer data transfer object to a timer. */
  fun toTimer(): Timer {
    return Timer(timerID = timerID, userID = userID, time = time)
  }
}
