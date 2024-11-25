package com.android.periodpals.model.timer

import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

private const val TAG = "TimerViewModel"

/**
 * View model for the timer feature.
 *
 * @property timerRepository The repository used for loading and saving timer data.
 */
class TimerViewModel(
    private val timerRepository: TimerRepository,
    private val timerManager: TimerManager
) : ViewModel() {
  private val _timer = mutableStateOf<Timer?>(null)
  val timer: State<Timer?> = _timer

  /**
   * Loads the timer and updates the timer state. On failure, the timer state is set to the default
   * timer.
   *
   * @param onSuccess Callback function to be called when the timer is successfully loaded.
   * @param onFailure Callback function to be called when there is an error loading the timer.
   */
  fun loadTimer(
      onSuccess: () -> Unit = { Log.d(TAG, "loadTimer success callback") },
      onFailure: (Exception) -> Unit = { e: Exception ->
        Log.d(TAG, "loadTimer failure callback: $e")
      }
  ) {
    viewModelScope.launch {
      timerRepository.loadTimer(
          onSuccess = {
            Log.d(TAG, "loadTimer: Successful")
            _timer.value = it.asTimer()
            onSuccess()
          },
          onFailure = { e: Exception ->
            Log.e(TAG, "loadTimer: fail to load timer: ${e.message}")
            _timer.value = null
            onFailure(e)
          },
      )
    }
  }

  /**
   * Starts the timer and updates the timer state. On failure, the timer state is set to the default
   * timer.
   */
  fun startTimer() {
    _timer.value?.let { timerManager.startTimerAction() }
  }

  /**
   * Cancels the timer and updates the timer state. On failure, the timer state is set to the
   * default timer.
   */
  fun resetTimer() {
    _timer.value?.let { timerManager.resetTimerAction() }
  }

  /**
   * Stops the timer and updates the timer state. On failure, the timer state is set to the default
   * timer.
   *
   * @param onSuccess Callback function to be called when the timer is successfully stopped.
   * @param onFailure Callback function to be called when there is an error stopping the timer.
   */
  fun stopTimer(
      onSuccess: () -> Unit = { Log.d(TAG, "stopTimer success callback") },
      onFailure: (Exception) -> Unit = { e: Exception ->
        Log.d(TAG, "stopTimer failure callback: $e")
      }
  ) {
    _timer.value?.let {
      val elapsedTime = timerManager.stopTimerAction()
      val newTimer = it.copy(lastTimers = listOf(elapsedTime) + it.lastTimers)

      viewModelScope.launch {
        timerRepository.upsertTimer(
            timerDto = newTimer.asTimerDto(),
            onSuccess = { it ->
              Log.d(TAG, "Timer saved successfully")
              _timer.value = it.asTimer()
              onSuccess()
            },
            onFailure = { e: Exception ->
              Log.e(TAG, "Failed to save timer: ${e.message}")
              _timer.value = null
              onFailure(e)
            },
        )
      }
    }
  }

  /** Retrieves the remaining time on the timer. */
  fun getRemainingTime(): Long {
    return timerManager.getRemainingTime()
  }

  @VisibleForTesting
  fun setTimerForTesting(timer: Timer?) {
    _timer.value = timer
  }
}
