package com.android.periodpals.model.timer

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "TimerRepositorySupabase"
private const val TIMERS = "timers"

/**
 * Implementation of TimerRepository using Supabase.
 *
 * @property supabase The Supabase client used for making API calls.
 */
class TimerRepositorySupabase(private val supabase: SupabaseClient) : TimerRepository {

  override suspend fun addTimer(
      timerDto: TimerDto,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      withContext(Dispatchers.IO) {
        supabase.postgrest[TIMERS].insert(timerDto).decodeSingle<TimerDto>()
      }
      Log.d(TAG, "addTimer: Success")
      onSuccess()
    } catch (e: Exception) {
      Log.d(TAG, "addTimer: fail to create timer: ${e.message}")
      onFailure(e)
    }
  }

  override suspend fun getTimersOfUser(
      userID: String,
      onSuccess: (List<Timer>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      val result =
          supabase.postgrest[TIMERS]
              .select { filter { eq("userID", userID) } }
              .decodeList<TimerDto>()
      Log.d(TAG, "getTimersOfUser: Success")
      onSuccess(result.map { it.toTimer() })
    } catch (e: Exception) {
      Log.d(TAG, "getTimersOfUser: fail to get timers: ${e.message}")
      onFailure(e)
    }
  }

  override suspend fun deleteTimerByTimerId(
      timerID: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      supabase.postgrest[TIMERS].delete { filter { eq("timerID", timerID) } }
      Log.d(TAG, "deleteTimerByTimerId: Success")
      onSuccess()
    } catch (e: Exception) {
      Log.d(TAG, "deleteTimerByTimerId: fail to delete timer: ${e.message}")
      onFailure(e)
    }
  }

  override suspend fun deleteTimersByUserId(
      userID: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      supabase.postgrest[TIMERS].delete { filter { eq("userID", userID) } }
      Log.d(TAG, "deleteTimersByUserId: Success")
      onSuccess()
    } catch (e: Exception) {
      Log.d(TAG, "deleteTimersByUserId: fail to delete timers: ${e.message}")
      onFailure(e)
    }
  }
}
