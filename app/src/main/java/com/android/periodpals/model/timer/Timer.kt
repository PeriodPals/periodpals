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
