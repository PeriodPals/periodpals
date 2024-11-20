package com.android.periodpals.model.timer

/**
 * Data class representing a timer.
 *
 * @property uid The user ID associated with the timer.
 * @property startTime The time the timer was started.
 * @property elapsedTime The total time the timer has been running.
 * @property averageTime The average time the timer has been running.
 * @property timerCount The number of times the timer has been started.
 * @property status The current status of the timer.
 */
data class Timer(
    val uid: String,
    val startTime: String?,
    val elapsedTime: Int,
    val averageTime: Int,
    val timerCount: Int,
    val status: TimerStatus,
)

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
