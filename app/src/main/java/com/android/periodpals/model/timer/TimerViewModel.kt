package com.android.periodpals.model.timer

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

private const val TAG = "TimerViewModel"

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
            _timer.value = DEFAULT_TIMER
            onFailure(e)
          })
    }
  }

  /**
   * Starts the timer and updates the timer state. On failure, the timer state is set to the default
   * timer.
   *
   * @param onSuccess Callback function to be called when the timer is successfully started.
   * @param onFailure Callback function to be called when there is an error starting the timer.
   */
  fun startTimer(
      onSuccess: () -> Unit = { Log.d(TAG, "startTimer success callback") },
      onFailure: (Exception) -> Unit = { e: Exception ->
        Log.d(TAG, "startTimer failure callback: $e")
      }
  ) {
    _timer.value?.let {
      timerManager.startTimerAction()
      val newTimer =
          it.copy(
              startTime = timerManager.startTime().toString(),
              stopTime = timerManager.stopTime().toString(),
              status = TimerStatus.RUNNING,
          )

      viewModelScope.launch {
        timerRepository.upsertTimer(
            timerDto = newTimer.asTimerDto(),
            onSuccess = {
              Log.d(TAG, "Timer saved successfully")
              _timer.value = it.asTimer()
              onSuccess()
            },
            onFailure = { e: Exception ->
              Log.e(TAG, "Failed to save timer: ${e.message}")
              _timer.value = DEFAULT_TIMER
              onFailure(e)
            })
      }
    }
  }

  /**
   * Cancels the timer and updates the timer state. On failure, the timer state is set to the
   * default timer.
   *
   * @param onSuccess Callback function to be called when the timer is successfully canceled.
   * @param onFailure Callback function to be called when there is an error canceling the timer.
   */
  fun resetTimer(
      onSuccess: () -> Unit = { Log.d(TAG, "cancelTimer success callback") },
      onFailure: (Exception) -> Unit = { e: Exception ->
        Log.d(TAG, "cancelTimer failure callback: $e")
      }
  ) {
    _timer.value?.let {
      timerManager.resetTimerAction()
      val newTimer =
          it.copy(
              startTime = null,
              stopTime = null,
              status = TimerStatus.STOPPED,
          )

      viewModelScope.launch {
        timerRepository.upsertTimer(
            timerDto = newTimer.asTimerDto(),
            onSuccess = { it ->
              Log.d(TAG, "Timer saved successfully")
              _timer.value = it.asTimer()
            },
            onFailure = { e: Exception ->
              Log.e(TAG, "Failed to save timer: ${e.message}")
              _timer.value = DEFAULT_TIMER
              onFailure(e)
            })
      }
    }
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
      val newTimer =
          it.copy(
              startTime = timerManager.startTime().toString(),
              stopTime = timerManager.stopTime().toString(),
              status = TimerStatus.STOPPED,
              lastTimers = listOf(elapsedTime) + it.lastTimers)

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
              _timer.value = DEFAULT_TIMER
              onFailure(e)
            })
      }
    }
  }

  /** Retrieves the remaining time on the timer. */
  fun getRemainingTime(): Long {
    return timerManager.getRemainingTime()
  }

  /** Retrieves the average time of the last five timers. */
  fun getAverage(): Double {
    val lastFiveTimers = _timer.value?.lastTimers?.take(5) ?: emptyList()
    return if (lastFiveTimers.isNotEmpty()) {
      lastFiveTimers.average()
    } else {
      0.0
    }
  }
}
