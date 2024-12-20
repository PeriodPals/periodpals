package com.android.periodpals.model.timer

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TAG = "TimerManager"

/**
 * Manages the timer for the app.
 *
 * @param context The context used to access shared preferences.
 */
class TimerManager(context: Context) {
  private var sharedPref: SharedPreferences =
      context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
  private var dateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault())

  private var timerCounting = false
  private var startTime: Date? = null
  private var stopTime: Date? = null

  /** Initializes the timer manager with the values from shared preferences. */
  init {
    timerCounting = sharedPref.getBoolean(COUNTING_KEY, false)

    val startString = sharedPref.getString(START_TIME_KEY, null)
    if (startString != null) startTime = dateFormat.parse(startString)

    val stopString = sharedPref.getString(STOP_TIME_KEY, null)
    if (stopString != null) stopTime = dateFormat.parse(stopString)
  }

  /** Returns the start time of the timer. */
  fun startTime(): Date? = startTime

  /**
   * Sets the start time of the timer.
   *
   * @param date The start time to set.
   */
  fun setStartTime(date: Date?) {
    startTime = date
    with(sharedPref.edit()) {
      val stringDate = if (date == null) null else dateFormat.format(date)
      putString(START_TIME_KEY, stringDate)
      apply()
    }
  }

  /** Returns the stop time of the timer. */
  fun stopTime(): Date? = stopTime

  /**
   * Sets the stop time of the timer.
   *
   * @param date The stop time to set.
   */
  private fun setStopTime(date: Date?) {
    stopTime = date
    with(sharedPref.edit()) {
      val stringDate = if (date == null) null else dateFormat.format(date)
      putString(STOP_TIME_KEY, stringDate)
      apply()
    }
  }

  /** Returns whether the timer is counting. */
  fun timerCounting(): Boolean = timerCounting

  /**
   * Sets whether the timer is counting.
   *
   * @param value The value to set.
   */
  private fun setTimerCounting(value: Boolean) {
    timerCounting = value
    with(sharedPref.edit()) {
      putBoolean(COUNTING_KEY, value)
      apply()
    }
  }

  /**
   * Starts the timer. Timer will continue running after the 6 hours have passed. Elapsed time will
   * be a negative value.
   *
   * @param onSuccess The callback to be invoked when the timer is successfully started.
   * @param onFailure The callback to be invoked when the timer fails to start.
   */
  fun startTimerAction(
      onSuccess: () -> Unit = { Log.d(TAG, "startTimerAction: success callback") },
      onFailure: (Exception) -> Unit = { e: Exception ->
        Log.d(TAG, "startTimerAction: failure callback: ${e.message}")
      }
  ) {
    try {
      setStartTime(Date())
      setStopTime(Date(startTime!!.time + COUNTDOWN_DURATION))
      setTimerCounting(true)
      Log.d(TAG, "startTimerAction: success callback")
      onSuccess()
    } catch (e: Exception) {
      Log.d(TAG, "startTimerAction: failure callback: ${e.message}")
      onFailure(e)
    }
  }

  /**
   * Resets the timer.
   *
   * @param onSuccess The callback to be invoked when the timer is successfully reset.
   * @param onFailure The callback to be invoked when the timer fails to reset.
   */
  fun resetTimerAction(
      onSuccess: () -> Unit = { Log.d(TAG, "resetTimerAction: success callback") },
      onFailure: (Exception) -> Unit = { e: Exception ->
        Log.d(TAG, "resetTimerAction: failure callback: ${e.message}")
      }
  ) {
    try {
      if (startTime == null) {
        onFailure(Exception("Start time is null"))
        return
      }

      setStartTime(null)
      setStopTime(null)
      setTimerCounting(false)
      Log.d(TAG, "resetTimerAction: success callback")
      onSuccess()
    } catch (e: Exception) {
      Log.d(TAG, "resetTimerAction: failure callback: ${e.message}")
      onFailure(e)
    }
  }

  /**
   * Stops the timer.
   *
   * @param onSuccess The callback to be invoked when the timer is successfully stopped.
   * @param onFailure The callback to be invoked when the timer fails to stop.
   */
  fun stopTimerAction(
      onSuccess: (Long) -> Unit = { _: Long -> Log.d(TAG, "stopTimerAction: success callback") },
      onFailure: (Exception) -> Unit = { e: Exception ->
        Log.d(TAG, "stopTimerAction: failure callback: ${e.message}")
      }
  ) {
    try {
      if (startTime == null) {
        onFailure(Exception("Start time is null"))
        return
      }

      setStopTime(Date())
      val elapsedTime = stopTime?.time?.minus(startTime?.time ?: 0L) ?: 0L
      setStartTime(null)
      setStopTime(null)
      setTimerCounting(false)
      Log.d(TAG, "stopTimerAction: success callback")
      onSuccess(elapsedTime)
    } catch (e: Exception) {
      Log.d(TAG, "stopTimerAction: failure callback: ${e.message}")
      onFailure(e)
    }
  }

  /** Constants used for shared preferences. */
  companion object {
    const val PREFERENCES = "prefs"
    const val START_TIME_KEY = "startKey"
    const val STOP_TIME_KEY = "stopKey"
    const val COUNTING_KEY = "countingKey"
  }
}
