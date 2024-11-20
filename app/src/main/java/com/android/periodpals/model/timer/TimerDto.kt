package com.android.periodpals.model.timer

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) for timer data.
 *
 * @property uid The user ID associated with the timer.
 * @property startTime The time the timer was started.
 * @property elapsedTime The total time the timer has been running.
 * @property averageTime The average time the timer has been running.
 * @property timerCount The number of times the timer has been started.
 * @property status The current status of the timer.
 */
@Serializable
data class TimerDto(
    @SerialName("uid") val uid: String,
    @SerialName("startTime") val startTime: String?,
    @SerialName("elapsedTime") val elapsedTime: Int,
    @SerialName("averageTime") val averageTime: Int,
    @SerialName("timerCount") val timerCount: Int,
    @SerialName("status") val status: TimerStatus
) {
  constructor(
      timer: Timer
  ) : this(
      uid = timer.uid,
      startTime = timer.startTime,
      elapsedTime = timer.elapsedTime,
      averageTime = timer.averageTime,
      timerCount = timer.timerCount,
      status = timer.status,
  )

  fun toTimer(): Timer {
    return Timer(
        uid = uid,
        startTime = startTime,
        elapsedTime = elapsedTime,
        averageTime = averageTime,
        timerCount = timerCount,
        status = status,
    )
  }
}
