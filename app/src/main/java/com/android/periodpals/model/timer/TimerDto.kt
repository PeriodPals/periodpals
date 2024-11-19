package com.android.periodpals.model.timer

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) for timer data.
 *
 * @property uid The user ID associated with the timer.
 * @property status The current status of the timer.
 * @property averageTime The average time for the timer.
 * @property startedAt The date and time when the timer was started.
 * @property updatedAt The date and time when the timer was last updated from/to the server.
 */
@Serializable
data class TimerDto(
    @SerialName("uid") val uid: String,
    @SerialName("status") val status: TimerStatus,
    @SerialName("averageTime") val averageTime: Long,
    @SerialName("timerCount") val timerCount: Int,
    @SerialName("startedAt") val startedAt: String,
    @SerialName("updatedAt") val updatedAt: String
) {
  constructor(
      timer: Timer
  ) : this(
      uid = timer.uid,
      status = timer.status,
      averageTime = timer.averageTime,
      timerCount = timer.timerCount,
      startedAt = timer.startedAt,
      updatedAt = timer.updatedAt,
  )

  fun toTimer(): Timer {
    return Timer(
        uid = uid,
        status = status,
        averageTime = averageTime,
        timerCount = timerCount,
        startedAt = startedAt,
        updatedAt = updatedAt,
    )
  }
}
