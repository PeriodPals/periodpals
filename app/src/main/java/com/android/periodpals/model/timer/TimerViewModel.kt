package com.android.periodpals.model.timer

import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.util.Date
import java.util.TimerTask
import kotlinx.coroutines.launch

private const val TAG = "TimerViewModel"
private const val PERIOD = 500L
private const val FIRST_REMINDER = 3 * 60 * 60 * 1000
private const val REMINDERS_INTERVAL = 30 * 60 * 1000
private const val STARTED_INSTRUCTION_TEXT = "Stay strong! Don't forget to stay hydrated!"
private const val POSITIVE_REMINDER_TEXT = "You've got this! Almost time to change your tampon!"
private const val NEGATIVE_REMINDER_TEXT =
    "Time's up! Change your tampon for you comfort and health!"

/**
 * View model for the timer feature.
 *
 * @property timerRepository The repository used for loading and saving timer data.
 * @property timerManager The manager used for starting, stopping, and resetting the timer.
 * @property _activeTimer Mutable state of the active timer.
 * @property activeTimer Public state of the active timer.
 * @property _isRunning Mutable state of the timer's running status.
 * @property isRunning Public state of the timer's running status.
 * @property _userAverageTimer Mutable state of the user's average timer.
 * @property userAverageTimer Public state of the user's average timer.
 * @property timer The timer used for updating the remaining time.
 * @property _remainingTime Mutable live data of the remaining time.
 * @property remainingTime Public live data of the remaining time.
 */
class TimerViewModel(
    private val timerRepository: TimerRepository,
    private val timerManager: TimerManager
) : ViewModel() {
  private var _activeTimer = mutableStateOf<Timer?>(null)
  val activeTimer: MutableState<Timer?> = _activeTimer

  private var _isRunning = mutableStateOf(false)
  val isRunning: MutableState<Boolean> = _isRunning

  private var _userAverageTimer = mutableDoubleStateOf(0.0)
  val userAverageTimer: MutableState<Double> = _userAverageTimer

  private val timer = java.util.Timer()
  private val _remainingTime = MutableLiveData(COUNTDOWN_DURATION)
  val remainingTime: LiveData<Long>
    get() = _remainingTime

  /** Task that updates the remaining time of the timer. */
  internal inner class TimeTask : TimerTask() {
    override fun run() {
      if (timerManager.timerCounting()) {
        val currentTime = Date().time
        val startTimeMillis = timerManager.startTime()!!.time
        _remainingTime.postValue(COUNTDOWN_DURATION - (currentTime - startTimeMillis))

        Log.d(TAG, "run: remaining time: ${_remainingTime.value}")
        val remainingTime = _remainingTime.value!!

        if (remainingTime % REMINDERS_INTERVAL in 0..PERIOD) {
          if (remainingTime <= PERIOD) {
            updateTimer(activeTimer.value!!.copy(instructionText = NEGATIVE_REMINDER_TEXT))
          } else if (remainingTime <= FIRST_REMINDER + PERIOD) {
            updateTimer(activeTimer.value!!.copy(instructionText = POSITIVE_REMINDER_TEXT))
          }
        }
      } else {
        _remainingTime.postValue(COUNTDOWN_DURATION)
      }
    }
  }

  init {
    _isRunning.value = timerManager.timerCounting()
    timer.schedule(TimeTask(), 0, PERIOD)
  }

  /**
   * Loads the active timer of a user.
   *
   * @param uid The ID of the user whose active timer is to be loaded.
   * @param onSuccess Callback function to be called when the active timer is successfully loaded.
   * @param onFailure Callback function to be called when there is an error loading the active
   *   timer.
   */
  fun loadActiveTimer(
      uid: String,
      onSuccess: () -> Unit = { Log.d(TAG, "loadActiveTimer: success callback") },
      onFailure: (Exception) -> Unit = { e: Exception ->
        Log.d(TAG, "loadActiveTimer: failure callback: $e")
      }
  ) {
    viewModelScope.launch {
      timerRepository.getActiveTimer(
          uid = uid,
          onSuccess = { timer ->
            Log.d(TAG, "loadActiveTimer: success callback")
            computeAverageTime(uid, onSuccess, onFailure)
            _activeTimer.value = timer
            onSuccess()
          },
          onFailure = { e ->
            Log.d(TAG, "loadActiveTimer: fail to get active timer: ${e.message}")
            _activeTimer.value = null
            onFailure(e)
          })
    }
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
          _isRunning.value = timerManager.timerCounting()
          val newTimer = Timer(time = ACTIVE_TIMER_TIME, instructionText = STARTED_INSTRUCTION_TEXT)
          viewModelScope.launch {
            timerRepository.addTimer(
                timerDto = TimerDto(newTimer),
                onSuccess = {
                  Log.d(TAG, "startTimer: success callback")
                  _activeTimer.value = newTimer
                  onSuccess()
                },
                onFailure = { e ->
                  Log.d(TAG, "startTimer: fail to create timer: ${e.message}")
                  onFailure(e)
                },
            )
          }
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
          _isRunning.value = timerManager.timerCounting()
          viewModelScope.launch {
            timerRepository.deleteTimersFilteredBy(
                cond = { eq("id", _activeTimer.value!!.id) },
                onSuccess = {
                  Log.d(TAG, "resetTimer: success callback")
                  _activeTimer.value = null
                  onSuccess()
                },
                onFailure = { e ->
                  Log.d(TAG, "resetTimer: fail to delete timer: ${e.message}")
                  onFailure(e)
                })
          }
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
   * @param uid The ID of the user whose timer is to be stopped.
   * @param onSuccess Callback function to be called when the timer is successfully stopped.
   * @param onFailure Callback function to be called when there is an error stopping the timer.
   */
  fun stopTimer(
      uid: String,
      onSuccess: () -> Unit = { Log.d(TAG, "stopTimer success callback") },
      onFailure: (Exception) -> Unit = { e: Exception ->
        Log.d(TAG, "stopTimer failure callback: $e")
      }
  ) {
    timerManager.stopTimerAction(
        onSuccess = { time ->
          Log.d(TAG, "stopTimer: success callback")
          _isRunning.value = timerManager.timerCounting()
          val newTimer = _activeTimer.value!!.copy(time = time, instructionText = null)
          updateTimer(
              newTimer,
              onSuccess = {
                Log.d(TAG, "stopTimer: update timer success callback")
                computeAverageTime(uid, onSuccess, onFailure)
                _activeTimer.value = null
                onSuccess()
              },
              onFailure = { e -> Log.d(TAG, "stopTimer: fail to update timer: ${e.message}") })
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
   * @param uid The ID of the user whose timers are to be fetched.
   * @param onSuccess Callback function to be called when the timers are successfully fetched.
   * @param onFailure Callback function to be called when there is an error fetching the timers.
   */
  @VisibleForTesting
  internal fun computeAverageTime(
      uid: String,
      onSuccess: () -> Unit = { Log.d(TAG, "computeAverageTime success callback") },
      onFailure: (Exception) -> Unit = { e: Exception ->
        Log.d(TAG, "computeAverageTime failure callback: $e")
      }
  ) {
    viewModelScope.launch {
      timerRepository.getTimersOfUser(
          uid = uid,
          onSuccess = { timerList ->
            Log.d(TAG, "updateOverallAverageTime: success callback")
            val nonNegativeTimes = timerList.map { it.time }.filter { it >= 0L }
            _userAverageTimer.doubleValue =
                if (nonNegativeTimes.isNotEmpty()) nonNegativeTimes.average() else 0.0
            onSuccess()
          },
          onFailure = { e ->
            Log.d(TAG, "updateOverallAverageTime: fail to create timer: ${e.message}")
            onFailure(e)
          })
    }
  }

  /**
   * Updates the timer in the database.
   *
   * @param timer The updated timer to be saved.
   * @param onSuccess Callback function to be called when the timer is successfully updated.
   * @param onFailure Callback function to be called when there is an error updating the timer.
   */
  private fun updateTimer(
      timer: Timer,
      onSuccess: () -> Unit = { Log.d(TAG, "updateTimer success callback") },
      onFailure: (Exception) -> Unit = { e: Exception ->
        Log.d(TAG, "updateTimer failure callback: $e")
      }
  ) {
    viewModelScope.launch {
      val currentTimer = _activeTimer.value
      if (currentTimer == null) {
        Log.d(TAG, "updateTimer: active timer is null")
        return@launch
      }

      val newTimer =
          _activeTimer.value!!.copy(time = timer.time, instructionText = timer.instructionText)
      timerRepository.updateTimer(
          timerDto = TimerDto(newTimer),
          onSuccess = {
            Log.d(TAG, "updateTimer: success callback")
            _activeTimer.value = newTimer
            onSuccess()
          },
          onFailure = { e ->
            Log.d(TAG, "updateTimer: fail to update timer: ${e.message}")
            onFailure(e)
          })
    }
  }
}
