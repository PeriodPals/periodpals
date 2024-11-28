package com.android.periodpals.model.timer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
  private var _userTimersList = mutableListOf<Timer>()
  val userTimersList: List<Timer>
    get() = _userTimersList

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
   * @param onSuccess Callback function to be called when the timer is successfully stopped.
   * @param onFailure Callback function to be called when there is an error stopping the timer.
   */
  fun stopTimer(
      onSuccess: () -> Unit = { Log.d(TAG, "stopTimer success callback") },
      onFailure: (Exception) -> Unit = { e: Exception ->
        Log.d(TAG, "stopTimer failure callback: $e")
      }
  ) {
    var elapsedTime = 0L
    timerManager.stopTimerAction(
        onSuccess = { elapsedTime = it },
        onFailure = { e: Exception ->
          Log.d(TAG, "stopTimer: fail to stop timer: ${e.message}")
          onFailure(e)
        })

    viewModelScope.launch {
      timerRepository.addTimer(
          timerDto = TimerDto(Timer(time = elapsedTime)),
          onSuccess = {
            Log.d(TAG, "stopTimer: Success")
            onSuccess()
          },
          onFailure = { e ->
            Log.d(TAG, "stopTimer: fail to create timer: ${e.message}")
            onFailure(e)
          })
    }
  }

  /**
   * Fetches all the timers of a user from the database.
   *
   * @param userID The ID of the user whose timers are to be fetched.
   * @param onSuccess Callback function to be called when the timers are successfully fetched.
   * @param onFailure Callback function to be called when there is an error fetching the timers.
   */
  fun fetchTimersOfUser(
      userID: String,
      onSuccess: () -> Unit = { Log.d(TAG, "updateOverallAverageTime success callback") },
      onFailure: (Exception) -> Unit = { e: Exception ->
        Log.d(TAG, "updateOverallAverageTime failure callback: $e")
      }
  ) {
    viewModelScope.launch {
      timerRepository.getTimersOfUser(
          userID = userID,
          onSuccess = {
            Log.d(TAG, "updateOverallAverageTime: Success")
            _userTimersList = it.toMutableList()
            onSuccess()
          },
          onFailure = { e ->
            Log.d(TAG, "updateOverallAverageTime: fail to create timer: ${e.message}")
            _userTimersList = mutableListOf()
            onFailure(e)
          })
    }
  }

  /** Retrieves the remaining time on the timer. */
  fun getRemainingTime(): Long {
    return timerManager.getRemainingTime()
  }
}
