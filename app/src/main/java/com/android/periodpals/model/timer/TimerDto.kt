package com.android.periodpals.model.timer

import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) for timer data.
 *
 * @property startTime The time the timer was started.
 * @property elapsedTime The total time the timer has been running.
 * @property status The current status of the timer.
 * @property lastTimers The last timers that have been run.
 */
@Serializable
data class TimerDto(
    val startTime: String?,
    val elapsedTime: Int,
    val status: TimerStatus,
    val lastTimers: List<Int>
) {
  /** Converts the timer data transfer object to a timer. */
  inline fun asTimer(): Timer {
    return Timer(
        startTime = startTime, elapsedTime = elapsedTime, status = status, lastTimers = lastTimers)
  }
}
