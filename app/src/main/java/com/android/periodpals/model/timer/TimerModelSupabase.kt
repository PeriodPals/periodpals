package com.android.periodpals.model.timer

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.filter.PostgrestFilterBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "TimerRepositorySupabase"
private const val TIMERS = "timers"
const val ACTIVE_TIMER_TIME = -1L

/**
 * Implementation of TimerRepository using Supabase.
 *
 * @property supabase The Supabase client used for making API calls.
 */
class TimerRepositorySupabase(private val supabase: SupabaseClient) : TimerRepository {

  override suspend fun getActiveTimer(
      uid: String,
      onSuccess: (Timer?) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      withContext(Dispatchers.Main) {
        val result =
            supabase.postgrest[TIMERS]
                .select {
                  filter {
                    eq("uid", uid)
                    eq("time", -1L)
                  }
                }
                .decodeList<TimerDto>()
        if (result.size == 1) {
          Log.d(TAG, "getActiveTimer: Success")
          onSuccess(result[0].toTimer())
        } else {
          Log.d(TAG, "getActiveTimer: Did not find exactly one active timer")
          onSuccess(null)
        }
      }
    } catch (e: Exception) {
      Log.d(TAG, "getActiveTimer: fail to get active timer: ${e.message}")
      onFailure(e)
    }
  }

  override suspend fun addTimer(
      timerDto: TimerDto,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      withContext(Dispatchers.Main) {
        supabase.postgrest[TIMERS].insert(timerDto).decodeSingle<TimerDto>()
      }
      Log.d(TAG, "addTimer: Success")
      onSuccess()
    } catch (e: Exception) {
      Log.d(TAG, "addTimer: fail to create timer: ${e.message}")
      onFailure(e)
    }
  }

  override suspend fun updateTimer(
      timerDto: TimerDto,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      withContext(Dispatchers.Main) {
        supabase.postgrest[TIMERS].upsert(timerDto).decodeSingle<TimerDto>()
      }
      Log.d(TAG, "updateTimer: Success")
      onSuccess()
    } catch (e: Exception) {
      Log.d(TAG, "updateTimer: fail to update timer: ${e.message}")
      onFailure(e)
    }
  }

  override suspend fun getTimersOfUser(
      uid: String,
      onSuccess: (List<Timer>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      withContext(Dispatchers.Main) {
        val result =
            supabase.postgrest[TIMERS].select { filter { eq("uid", uid) } }.decodeList<TimerDto>()
        Log.d(TAG, "getTimersOfUser: Success")
        onSuccess(result.map { it.toTimer() })
      }
    } catch (e: Exception) {
      Log.d(TAG, "getTimersOfUser: fail to get timers: ${e.message}")
      onFailure(e)
    }
  }

  override suspend fun deleteTimersFilteredBy(
      cond: PostgrestFilterBuilder.() -> Unit,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      withContext(Dispatchers.Main) {
        supabase.postgrest[TIMERS].delete { filter(cond) }
        Log.d(TAG, "deleteTimerFilteredBy: Success")
        onSuccess()
      }
    } catch (e: Exception) {
      Log.d(TAG, "deleteTimerFilteredBy: fail to delete timer(s): ${e.message}")
      onFailure(e)
    }
  }
}
