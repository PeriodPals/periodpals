package com.android.periodpals.model.timer

import android.os.SystemClock
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TimerViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

  private var savedTime: Int = savedStateHandle.get("timeLeft") ?: 21_600 // Default to 6 hours

  // State for timer
  var timeLeft = mutableStateOf(savedTime)
    private set

  var isTimerRunning = mutableStateOf(false)
    private set

  private var startTime = SystemClock.elapsedRealtime() // Track the start time of the timer
  private var accumulatedTime = 0L // To accumulate the time during pauses

  init {
    if (isTimerRunning.value) {
      startTimer()
    }
  }

  // Start or resume the timer
  fun startTimer() {
    if (!isTimerRunning.value) {
      startTime = SystemClock.elapsedRealtime() - accumulatedTime // Correct for accumulated time
      isTimerRunning.value = true
      startTimerLoop()
    }
  }

  // The main timer loop
  private fun startTimerLoop() {
    viewModelScope.launch(Dispatchers.Default) {
      while (isTimerRunning.value && timeLeft.value > 0) {
        delay(1000) // Wait for 1 second
        val elapsedTime = SystemClock.elapsedRealtime() - startTime
        timeLeft.value = (savedTime - (elapsedTime / 1000)).toInt()
      }
    }
  }

  // Pause the timer
  fun pauseTimer() {
    if (isTimerRunning.value) {
      isTimerRunning.value = false
      accumulatedTime = SystemClock.elapsedRealtime() - startTime // Store how much time has passed
    }
  }

  // Save the state when needed (e.g., when navigating away from the screen)
  fun saveState(savedStateHandle: SavedStateHandle) {
    savedStateHandle.set("timeLeft", timeLeft.value)
  }
}
