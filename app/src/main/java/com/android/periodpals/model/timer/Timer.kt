package com.android.periodpals.model.timer

/**
 * Data class representing a timer. There can only be one timer per user.
 *
 * @property uid The user ID associated with the timer.
 * @property status The current status of the timer.
 * @property averageTime The average time for the timer.
 * @property timerCount The number of times the timer has run.
 * @property startedAt The date and time when the timer was started.
 * @property updatedAt The date and time when the timer was last updated from/to the server.
 */
data class Timer(
    val uid: String,
    val status: TimerStatus,
    val averageTime: Long,
    val timerCount: Int,
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
