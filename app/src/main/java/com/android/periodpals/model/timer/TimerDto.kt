package com.android.periodpals.model.timer

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) for timer data.
 *
 * @property time The time of the timer.
 */
@Serializable
data class TimerDto(@SerialName("time") val time: Long) {
  /**
   * Constructs a `TimerDto` from a `Timer` object.
   *
   * @param timer The `Timer` object to be converted into a `TimerDto`.
   */
  constructor(timer: Timer) : this(time = timer.time)

  /**
   * Converts this `TimerDto` to a `Timer` object.
   *
   * @return The `Timer` object created from this `TimerDto`.
   */
  fun toTimer(): Timer {
    return Timer(time = time)
  }
}
