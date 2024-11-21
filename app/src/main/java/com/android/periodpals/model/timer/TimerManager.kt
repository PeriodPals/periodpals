package com.android.periodpals.model.timer

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

private const val TAG = "TimerManager"

class TimerManager(private val context: Context) {

  private val _timerState = MutableLiveData<Long>()
  val timerState: LiveData<Long>
    get() = _timerState

  // Start the timer
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

  // Stop the timer
  fun stopTimer() {
    val intent = Intent(context, TimerService::class.java).apply { action = "STOP_TIMER" }
    context.startService(intent)

    // Stop timer and reset LiveData
    _timerState.value = 0L
  }
}
