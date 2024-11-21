package com.android.periodpals.model.timer

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.util.Timer
import java.util.TimerTask
import kotlinx.coroutines.launch

private const val TAG = "TimerViewModel"

/**
 * ViewModel for managing timer data.
 *
 * @property timerRepository The repository used for loading and saving timer data.
 */
class TimerViewModel(
    private val timerRepository: TimerRepository,
    private val timerManager: TimerManager
) : ViewModel() {
  private val _timer = mutableStateOf<com.android.periodpals.model.timer.Timer?>(null)
  val timer: State<com.android.periodpals.model.timer.Timer?> = _timer

  private val _elapsedTime = MutableLiveData(0)
  val elapsedTime: LiveData<Int> = _elapsedTime

  private var isRunning: Boolean = false
  private var javaTimer: Timer? = null
  private var elapsedTimeValue: Int = 0

  /**
   * Loads the timer data.
   *
   * @param onSuccess Callback function to be called when the timer data is successfully loaded.
   * @param onFailure Callback function to be called when there is an error loading the timer data.
   */
  fun getTimer(
      onSuccess: () -> Unit = { Log.d(TAG, "getTimer success callback") },
      onFailure: (Exception) -> Unit = { e: Exception ->
        Log.d(TAG, "getTimer failure callback: $e")
      }
  ) {
    viewModelScope.launch {
      timerRepository.getTimer(
          onSuccess = { timerDto ->
            Log.d(TAG, "getTimer: Successful")
            _timer.value = timerDto.asTimer()
            onSuccess()
          },
          onFailure = { e: Exception ->
            Log.d(TAG, "getTimer: fail to get timer: ${e.message}")
            _timer.value = null
            onFailure(e)
          },
      )
    }
  }

  /**
   * User starts the timer.
   *
   * @param onSuccess Callback function to be called when the timer is successfully started.
   * @param onFailure Callback function to be called when there is an error starting the timer.
   */
  fun startTimer(
      context: Context,
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

    timerManager.startTimer(System.currentTimeMillis())

    viewModelScope.launch {
      val timerData =
          Timer(
              startTime = System.currentTimeMillis().toString(),
              elapsedTime = 0,
              lastTimers = timer.value?.lastTimers ?: emptyList(),
              status = TimerStatus.RUNNING)
      timerRepository.upsertTimer(
          timerData.asTimerDto(),
          onSuccess = {
            Log.d(TAG, "startTimer: success")
            _timer.value = it.asTimer()
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
   * @param onSuccess Callback function to be called when the timer is successfully stopped.
   * @param onFailure Callback function to be called when there is an error stopping the timer.
   */
  fun stopTimer(
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

    timerManager.stopTimer()

    viewModelScope.launch {
      val newLastTimers = (timer.value?.lastTimers ?: emptyList()) + elapsedTimeValue
      val timerData =
          Timer(
              startTime = timer.value?.startTime ?: System.currentTimeMillis().toString(),
              elapsedTime = 0,
              lastTimers = newLastTimers,
              status = TimerStatus.STOPPED,
          )
      timerRepository.upsertTimer(
          timerData.asTimerDto(),
          onSuccess = {
            Log.d(TAG, "stopTimer: success")
            _timer.value = it.asTimer()
            onSuccess()
          },
          onFailure = {
            Log.d(TAG, "stopTimer: failure: ${it.message}")
            _timer.value = null
            onFailure(it)
          })
    }
  }

  /** User cancels the timer. */
  fun cancelTimer(
      onSuccess: () -> Unit = { Log.d(TAG, "cancelTimer success callback") },
      onFailure: (Exception) -> Unit = { e: Exception ->
        Log.d(TAG, "cancelTimer failure callback: $e")
      }
  ) {
    if (isRunning) {
      javaTimer?.cancel()
      javaTimer = null
      isRunning = false

      timerManager.stopTimer()

      viewModelScope.launch {
        val timerData =
            Timer(
                startTime = timer.value?.startTime ?: System.currentTimeMillis().toString(),
                elapsedTime = elapsedTimeValue,
                lastTimers = timer.value?.lastTimers ?: emptyList(),
                status = TimerStatus.STOPPED)

        timerRepository.upsertTimer(
            timerData.asTimerDto(),
            onSuccess = {
              Log.d(TAG, "cancelTimer: success")
              _timer.value = it.asTimer()
              onSuccess()
            },
            onFailure = { e: Exception ->
              Log.d(TAG, "cancelTimer: failure: ${e.message}")
              _timer.value = timerData
              onFailure(e)
            })
      }
    }
  }

  /** Returns the average time of the last five timers. */
  fun getAverageTime(): Int {
    val lastTimers = timer.value?.lastTimers ?: emptyList()
    val lastFiveTimers = lastTimers.take(5)
    return if (lastFiveTimers.isEmpty()) {
      0
    } else {
      lastFiveTimers.sum() / lastFiveTimers.size
    }
  }
}
