package com.android.periodpals.model.timer

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

private const val TAG = "TimerManager"

/**
 * Manages the timer service.
 *
 * @property context The context of the application.
 */
class TimerManager(private val context: Context) {

  private val _timerState = MutableLiveData<Long>()
  val timerState: LiveData<Long>
    get() = _timerState // TODO: remove (?)

  /**
   * Starts the timer with the given start time.
   *
   * @param startTime The time the timer was started.
   */
  fun startTimer(startTime: Long) {
    val intent =
        Intent(context, TimerService::class.java).apply {
          action = "START_TIMER"
          putExtra("start_time", startTime)
        }
    context.startService(intent)

    // Update LiveData with the start time
    _timerState.value = startTime
  }

  /** Stops the timer and resets the LiveData. */
  fun stopTimer() {
    val intent = Intent(context, TimerService::class.java).apply { action = "STOP_TIMER" }
    context.startService(intent)

    // Stop timer and reset LiveData
    _timerState.value = 0L
  }
}
