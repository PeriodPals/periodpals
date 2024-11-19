package com.android.periodpals.model.timer

/**
 * Data class representing a timer. There can only be one timer per user.
 *
 * @property uid The user ID associated with the timer.
 * @property status The current status of the timer.
 * @property elapsedTimes The list of elapsed times for the timer to calculate the average time.
 * @property averageTime The average time for the timer.
 * @property startedAt The date and time when the timer was started.
 * @property updatedAt The date and time when the timer was last updated from/to the server.
 */
data class Timer(
    val uid: String,
    val status: TimerStatus,
    val elapsedTimes: List<Long>,
    val averageTime: Long,
    val startedAt: String,
    val updatedAt: String
)

// TODO: update timer status for the reminders
/**
 * Enum class representing the current status of the timer.
 *
 * @property RUNNING The timer is currently running.
 * @property STOPPED The timer has been stopped.
 */
enum class TimerStatus {
  RUNNING,
  STOPPED
}
