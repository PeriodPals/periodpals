package com.android.periodpals.model.timer

import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) for timer data.
 *
 * @property startTime The time the timer was started.
 * @property stopTime The time the timer was ended.
 * @property remainingTime The remaining time on the timer.
 * @property status The current status of the timer.
 * @property lastTimers The last timers that have been run.
 */
@Serializable
data class TimerDto(
    val startTime: String?,
    val stopTime: String?,
    val remainingTime: Long,
    val status: TimerStatus,
    val lastTimers: List<Int>
) {
  /** Converts the timer data transfer object to a timer. */
  fun asTimer(): Timer {
    return Timer(
        startTime = startTime,
        stopTime = stopTime,
        remainingTime = remainingTime,
        status = status,
        lastTimers = lastTimers,
    )
  }
}
