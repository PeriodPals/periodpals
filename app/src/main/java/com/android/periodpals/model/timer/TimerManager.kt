package com.android.periodpals.model.timer

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.annotation.VisibleForTesting
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TAG = "TimerManager"

/**
 * Manages the timer for the app.
 *
 * @param context The context used to access shared preferences.
 */
@VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
open class TimerManager(context: Context) {
  private var sharedPref: SharedPreferences =
      context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
  private var dateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault())

  private var timerCounting = false
  private var startTime: Date? = null
  private var stopTime: Date? = null

  init {
    timerCounting = sharedPref.getBoolean(COUNTING_KEY, false)

    val startString = sharedPref.getString(START_TIME_KEY, null)
    if (startString != null) startTime = dateFormat.parse(startString)

    val stopString = sharedPref.getString(STOP_TIME_KEY, null)
    if (stopString != null) stopTime = dateFormat.parse(stopString)
  }

  fun startTime(): Date? = startTime

  fun setStartTime(date: Date?) {
    startTime = date
    with(sharedPref.edit()) {
      val stringDate = if (date == null) null else dateFormat.format(date)
      putString(START_TIME_KEY, stringDate)
      apply()
    }
  }

  fun stopTime(): Date? = stopTime

  private fun setStopTime(date: Date?) {
    stopTime = date
    with(sharedPref.edit()) {
      val stringDate = if (date == null) null else dateFormat.format(date)
      putString(STOP_TIME_KEY, stringDate)
      apply()
    }
  }

  fun timerCounting(): Boolean = timerCounting

  private fun setTimerCounting(value: Boolean) {
    timerCounting = value
    with(sharedPref.edit()) {
      putBoolean(COUNTING_KEY, value)
      apply()
    }
  }

  fun startTimerAction(
      onSuccess: () -> Unit = { Log.d(TAG, "startTimerAction: success callback") },
      onFailure: (Exception) -> Unit = { e: Exception ->
        Log.d(TAG, "startTimerAction: fail to start timer: ${e.message}")
      }
  ) {
    try {
      setStartTime(Date())
      setStopTime(Date(startTime!!.time + COUNTDOWN_DURATION))
      setTimerCounting(true)
      Log.d(TAG, "startTimerAction: Success")
      onSuccess()
    } catch (e: Exception) {
      Log.d(TAG, "startTimerAction: fail to start timer: ${e.message}")
      onFailure(e)
    }
  }

  fun resetTimerAction(
      onSuccess: () -> Unit = { Log.d(TAG, "resetTimerAction: success callback") },
      onFailure: (Exception) -> Unit = { e: Exception ->
        Log.d(TAG, "resetTimerAction: fail to reset timer: ${e.message}")
      }
  ) {
    try {
      setStartTime(null)
      setStopTime(null)
      setTimerCounting(false)
      Log.d(TAG, "resetTimerAction: Success")
      onSuccess()
    } catch (e: Exception) {
      Log.d(TAG, "resetTimerAction: fail to reset timer: ${e.message}")
      onFailure(e)
    }
  }

  fun stopTimerAction(
      onSuccess: (Long) -> Unit = { _: Long -> Log.d(TAG, "stopTimerAction: success callback") },
      onFailure: (Exception) -> Unit = { e: Exception ->
        Log.d(TAG, "stopTimerAction: fail to stop timer: ${e.message}")
      }
  ) {
    try {
      setStopTime(Date())
      val elapsedTime = stopTime?.time?.minus(startTime?.time ?: 0L) ?: 0L
      setStartTime(null)
      setStopTime(null)
      setTimerCounting(false)
      Log.d(TAG, "stopTimerAction: Success")
      onSuccess(elapsedTime)
    } catch (e: Exception) {
      Log.d(TAG, "stopTimerAction: fail to stop timer: ${e.message}")
      onFailure(e)
    }
  }

  fun getRemainingTime(): Long {
    val currentTime = Date().time
    val stopTime = stopTime?.time ?: return 0
    return if (currentTime < stopTime) stopTime - currentTime else 0
  }

  companion object {
    const val PREFERENCES = "prefs"
    const val START_TIME_KEY = "startKey"
    const val STOP_TIME_KEY = "stopKey"
    const val COUNTING_KEY = "countingKey"
    const val COUNTDOWN_DURATION = 6 * 60 * 60 * 1000 // 6 hours in milliseconds
  }

  @VisibleForTesting
  fun setStartActionForTesting(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    onFailure(Exception("Test start action failure"))
  }

  @VisibleForTesting
  fun setResetActionForTesting(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    onFailure(Exception("Test reset action failure"))
  }

  @VisibleForTesting
  fun setStopActionForTesting(onSuccess: (Long) -> Unit, onFailure: (Exception) -> Unit) {
    onFailure(Exception("Test stop action failure"))
  }
}
