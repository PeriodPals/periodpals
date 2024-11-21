package com.android.periodpals.model.timer

/**
 * Data class representing a timer.
 *
 * @property startTime The time the timer was started.
 * @property elapsedTime The total time the timer has been running.
 * @property status The current status of the timer.
 * @property lastTimers The last timers that have been run.
 */
data class Timer(
    val startTime: String?,
    val elapsedTime: Int,
    val status: TimerStatus,
    val lastTimers: List<Int>
) {
  /** Converts the timer to a timer data transfer object. */
  inline fun asTimerDto(): TimerDto {
    return TimerDto(
        startTime = this.startTime,
        elapsedTime = this.elapsedTime,
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
