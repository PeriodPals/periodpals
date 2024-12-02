package com.android.periodpals.model.timer

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.util.Date
import java.util.TimerTask
import kotlinx.coroutines.launch

private const val TAG = "TimerViewModel"

/**
 * View model for the timer feature.
 *
 * @property timerRepository The repository used for loading and saving timer data.
 * @property timerManager The manager used for starting, stopping, and resetting the timer.
 */
class TimerViewModel(
    private val timerRepository: TimerRepository,
    private val timerManager: TimerManager
) : ViewModel() {
  private var _userAverageTimer = mutableDoubleStateOf(0.0)
  val userAverageTimer: MutableState<Double> = _userAverageTimer

  private val timer = java.util.Timer()
  private val _remainingTime = MutableLiveData(COUNTDOWN_6_HOURS)
  val remainingTime: LiveData<Long>
    get() = _remainingTime

  internal inner class TimeTask : TimerTask() {
    override fun run() {
      if (timerManager.timerCounting()) {
        val currentTime = Date().time
        val startTimeMillis = timerManager.startTime()!!.time
        _remainingTime.postValue(COUNTDOWN_6_HOURS - (currentTime - startTimeMillis))

        Log.d(TAG, "run: remaining time: ${_remainingTime.value}")
      } else {
        _remainingTime.postValue(COUNTDOWN_6_HOURS)
      }
    }
  }

  init {
    timer.schedule(TimeTask(), 0, 500)
  }

  /**
   * Starts the timer.
   *
   * @param onSuccess Callback function to be called when the timer is successfully started.
   * @param onFailure Callback function to be called when there is an error starting the timer.
   */
  fun startTimer(
      onSuccess: () -> Unit = { Log.d(TAG, "startTimer: success callback") },
      onFailure: (Exception) -> Unit = { e: Exception ->
        Log.d(TAG, "startTimer: failure callback: $e")
      }
  ) {
    timerManager.startTimerAction(
        onSuccess = {
          Log.d(TAG, "startTimer: success callback")
          onSuccess()
        },
        onFailure = { e: Exception ->
          Log.d(TAG, "startTimer: failure callback: $e")
          onFailure(e)
        })
  }

  /**
   * Resets the timer.
   *
   * @param onSuccess Callback function to be called when the timer is successfully reset.
   * @param onFailure Callback function to be called when there is an error resetting the timer.
   */
  fun resetTimer(
      onSuccess: () -> Unit = { Log.d(TAG, "resetTimer: success callback") },
      onFailure: (Exception) -> Unit = { e: Exception ->
        Log.d(TAG, "resetTimer: failure callback: $e")
      }
  ) {
    timerManager.resetTimerAction(
        onSuccess = {
          Log.d(TAG, "resetTimer: success callback")
          onSuccess()
        },
        onFailure = { e: Exception ->
          Log.d(TAG, "resetTimer: failure callback: $e")
          onFailure(e)
        })
  }

  /**
   * Stops the timer and saves the elapsed time to the database (Supabase).
   *
   * @param userID The ID of the user whose timer is to be stopped.
   * @param onSuccess Callback function to be called when the timer is successfully stopped.
   * @param onFailure Callback function to be called when there is an error stopping the timer.
   */
  fun stopTimer(
      userID: String,
      onSuccess: () -> Unit = { Log.d(TAG, "stopTimer success callback") },
      onFailure: (Exception) -> Unit = { e: Exception ->
        Log.d(TAG, "stopTimer failure callback: $e")
      }
  ) {
    timerManager.stopTimerAction(
        onSuccess = { time ->
          Log.d(TAG, "stopTimer: success callback")
          viewModelScope.launch {
            timerRepository.addTimer(
                timerDto = TimerDto(Timer(time = time)),
                onSuccess = {
                  Log.d(TAG, "stopTimer: success callback")
                  onSuccess()
                },
                onFailure = { e ->
                  Log.d(TAG, "stopTimer: fail to create timer: ${e.message}")
                  onFailure(e)
                },
            )
          }
          computeAverageTime(userID, onSuccess, onFailure)
        },
        onFailure = { e: Exception ->
          Log.d(TAG, "stopTimer: fail to stop timer: ${e.message}")
          onFailure(e)
        },
    )
  }

  /**
   * Fetches the timers of a user and computes the average time.
   *
   * @param userID The ID of the user whose timers are to be fetched.
   * @param onSuccess Callback function to be called when the timers are successfully fetched.
   * @param onFailure Callback function to be called when there is an error fetching the timers.
   */
  fun computeAverageTime(
      userID: String,
      onSuccess: () -> Unit = { Log.d(TAG, "updateOverallAverageTime success callback") },
      onFailure: (Exception) -> Unit = { e: Exception ->
        Log.d(TAG, "updateOverallAverageTime failure callback: $e")
      }
  ) {
    viewModelScope.launch {
      timerRepository.getTimersOfUser(
          userID = userID,
          onSuccess = { timerList ->
            Log.d(TAG, "updateOverallAverageTime: success callback")
            _userAverageTimer.doubleValue = timerList.map { it.time }.average()
            onSuccess()
          },
          onFailure = { e ->
            Log.d(TAG, "updateOverallAverageTime: fail to create timer: ${e.message}")
            onFailure(e)
          })
    }
  }

  /**
   * Checks if the timer is running.
   *
   * @return True if the timer is running, false otherwise.
   */
  fun timerRunning(): Boolean {
    return timerManager.timerCounting()
  }
}
