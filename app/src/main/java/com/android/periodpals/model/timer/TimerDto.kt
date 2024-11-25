package com.android.periodpals.model.timer

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Data Transfer Object (DTO) for timer data.
 *
 * @property lastTimers The last timers that have been run.
 */
@Serializable
data class TimerDto(val lastTimers: String) {
  /** Converts the timer data transfer object to a timer. */
  fun asTimer(): Timer {
    return Timer(
        lastTimers = Json.decodeFromString(lastTimers),
    )
  }
}
