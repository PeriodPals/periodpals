package com.android.periodpals.model.timer

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Data class representing a timer.
 *
 * @property lastTimers The last timers that have been run.
 */
data class Timer(val lastTimers: List<Long>) {
  /** Converts the timer to a timer data transfer object. */
  inline fun asTimerDto(): TimerDto {
    return TimerDto(lastTimers = Json.encodeToString(this.lastTimers))
  }
}

// TODO: update timer status for the reminders
/**
 * Enum class representing the current status of the timer.
 *
 * @property RUNNING The timer is currently running.
 * @property STOPPED The timer has been stopped, not yet started.
 */
enum class TimerStatus {
  RUNNING,
  STOPPED
}

val DEFAULT_TIMER = Timer(lastTimers = emptyList())
