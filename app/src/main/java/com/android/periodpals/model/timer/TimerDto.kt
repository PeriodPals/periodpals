package com.android.periodpals.model.timer

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) for timer data.
 *
 * @property id The unique identifier of the timer, generated when timer is created in [Timer].
 * @property time The time of the timer.
 * @property instructionText The instruction text associated with the timer.
 */
@Serializable
data class TimerDto(
    @SerialName("id") val id: String,
    @SerialName("time") val time: Long,
    @SerialName("instructionText") val instructionText: String?
) {
  /**
   * Constructs a `TimerDto` from a `Timer` object.
   *
   * @param timer The `Timer` object to be converted into a `TimerDto`.
   */
  constructor(
      timer: Timer
  ) : this(id = timer.id, time = timer.time, instructionText = timer.instructionText)

  /**
   * Converts this `TimerDto` to a `Timer` object.
   *
   * @return The `Timer` object created from this `TimerDto`.
   */
  fun toTimer(): Timer {
    return Timer(id = id, time = time, instructionText = instructionText)
  }
}
