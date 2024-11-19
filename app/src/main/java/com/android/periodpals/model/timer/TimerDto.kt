package com.android.periodpals.model.timer

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) for timer data.
 *
 * @property uid The user ID associated with the timer.
 * @property status The current status of the timer.
 * @property elapsedTimes The list of elapsed times for the timer to calculate the average time.
 * @property averageTime The average time for the timer.
 * @property startedAt The date and time when the timer was started.
 * @property updatedAt The date and time when the timer was last updated from/to the server.
 */
@Serializable
data class TimerDto(
    @SerialName("uid") val uid: String,
    @SerialName("status") val status: TimerStatus,
    @SerialName("elapsedTimes") val elapsedTimes: List<Long>,
    @SerialName("averageTime") val averageTime: Long,
    @SerialName("startedAt") val startedAt: String,
    @SerialName("updatedAt") val updatedAt: String
) {
  constructor(
      timer: Timer
  ) : this(
      uid = timer.uid,
      status = timer.status,
      elapsedTimes = timer.elapsedTimes,
      averageTime = timer.averageTime,
      startedAt = timer.startedAt,
      updatedAt = timer.updatedAt)

  fun toTimer(): Timer {
    return Timer(
        uid = uid,
        status = status,
        elapsedTimes = elapsedTimes,
        averageTime = averageTime,
        startedAt = startedAt,
        updatedAt = updatedAt)
  }
}
