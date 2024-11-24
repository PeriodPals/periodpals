package com.android.periodpals.model.timer

/**
 * Data class representing a timer.
 *
 * @property startTime The time the timer was started.
 * @property stopTime The time the timer was ended.
 * @property remainingTime The remaining time on the timer.
 * @property status The current status of the timer.
 * @property lastTimers The last timers that have been run.
 */
data class Timer(
    val startTime: String?,
    val stopTime: String?,
    val remainingTime: Long,
    val status: TimerStatus,
    val lastTimers: List<Int>
) {
  /** Converts the timer to a timer data transfer object. */
  inline fun asTimerDto(): TimerDto {
    return TimerDto(
        startTime = this.startTime,
        stopTime = this.stopTime,
        remainingTime = this.remainingTime,
        status = this.status,
        lastTimers = this.lastTimers)
  }
}

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
