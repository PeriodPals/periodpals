package com.android.periodpals.model.timer

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.time.OffsetDateTime
import java.util.Timer
import java.util.TimerTask
import kotlinx.coroutines.launch

private const val TAG = "TimerViewModel"

/**
 * ViewModel for managing timer data.
 *
 * @property timerRepository The repository used for loading and saving timer data.
 */
class TimerViewModel(private val timerRepository: TimerRepository) : ViewModel() {
  private val _timer = mutableStateOf<com.android.periodpals.model.timer.Timer?>(null)
  val timer: State<com.android.periodpals.model.timer.Timer?> = _timer

  private val _elapsedTime = MutableLiveData(0)
  val elapsedTime: LiveData<Int> = _elapsedTime

  private var isRunning: Boolean = false
  private var javaTimer: Timer? = null
  private var elapsedTimeValue: Int = 0

  /**
   * User starts the timer.
   *
   * @param timer The timer to start.
   * @param onSuccess Callback function to be called when the timer is successfully started.
   * @param onFailure Callback function to be called when there is an error starting the timer.
   */
  fun startTimer(
      timer: com.android.periodpals.model.timer.Timer,
      onSuccess: () -> Unit = { Log.d(TAG, "startTimer success callback") },
      onFailure: (Exception) -> Unit = { e: Exception ->
        Log.d(TAG, "startTimer failure callback: $e")
      }
  ) {
    if (isRunning) {
      Log.e(TAG, "Timer is already running")
      onFailure(Exception("Timer is already running"))
      return
    }

    isRunning = true
    javaTimer = Timer()
    javaTimer?.schedule(
        object : TimerTask() {
          override fun run() {
            elapsedTimeValue++
            _elapsedTime.postValue(elapsedTimeValue)
          }
        },
        0,
        1000)

    viewModelScope.launch {
      val startTime = OffsetDateTime.now().toString()
      val timerData =
          Timer(
              uid = timer.uid,
              startTime = startTime,
              elapsedTime = elapsedTimeValue,
              averageTime = timer.averageTime,
              timerCount = timer.timerCount,
              status = TimerStatus.RUNNING,
          )
      timerRepository.upsertTimer(
          TimerDto(timerData),
          onSuccess = {
            Log.d(TAG, "startTimer: success")
            _timer.value = timerData
            onSuccess()
          },
          onFailure = {
            Log.d(TAG, "startTimer: failure: ${it.message}")
            _timer.value = null
            onFailure(it)
          })
    }
  }

  /**
   * User stops the timer.
   *
   * @param timer The timer to stop.
   * @param onSuccess Callback function to be called when the timer is successfully stopped.
   * @param onFailure Callback function to be called when there is an error stopping the timer.
   */
  fun stopTimer(
      timer: com.android.periodpals.model.timer.Timer,
      onSuccess: () -> Unit = { Log.d(TAG, "stopTimer success callback") },
      onFailure: (Exception) -> Unit = { e: Exception ->
        Log.d(TAG, "stopTimer failure callback: $e")
      }
  ) {
    if (!isRunning) {
      Log.e(TAG, "Timer is not running")
      onFailure(Exception("Timer is not running"))
      return
    }

    javaTimer?.cancel()
    javaTimer = null
    isRunning = false

    viewModelScope.launch {
      val newAverageTime =
          (timer.averageTime * timer.timerCount + elapsedTimeValue) / (timer.timerCount + 1)
      val timerData =
          Timer(
              uid = timer.uid,
              startTime = timer.startTime,
              elapsedTime = 0,
              averageTime = newAverageTime,
              timerCount = timer.timerCount + 1,
              status = TimerStatus.STOPPED,
          )
      timerRepository.upsertTimer(
          TimerDto(timerData),
          onSuccess = {
            Log.d(TAG, "stopTimer: success")
            _timer.value = timerData
            onSuccess()
          },
          onFailure = {
            Log.d(TAG, "stopTimer: failure: ${it.message}")
            _timer.value = null
            onFailure(it)
          })
    }
  }

  /** Returns the elapsed time of the timer. */
  fun getElapsedTime(): Int {
    return elapsedTimeValue
  }

  private fun cancelTimer() {
    if (isRunning) {
      javaTimer?.cancel()
      javaTimer = null
      isRunning = false

      viewModelScope.launch {
        val timerData = _timer.value?.copy(elapsedTime = 0, status = TimerStatus.STOPPED)
        if (timerData != null) {
          timerRepository.upsertTimer(
              TimerDto(timerData),
              onSuccess = {
                Log.d(TAG, "cancelTimer: success")
                _timer.value = timerData
              },
              onFailure = { e: Exception ->
                Log.d(TAG, "cancelTimer: failure: ${e.message}")
                _timer.value = timerData
              })
        }
      }
    }
  }

  /** Cancels the timer. */
  override fun onCleared() {
    super.onCleared()
    cancelTimer()
  }
}
