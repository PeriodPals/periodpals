package com.android.periodpals.model.timer

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.filter.PostgrestFilterBuilder
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

  override suspend fun getTimersOfUser(
      userID: String,
      onSuccess: (List<Timer>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      withContext(Dispatchers.Main) {
        val result =
            supabase.postgrest[TIMERS]
                .select { filter { eq("userID", userID) } }
                .decodeList<TimerDto>()
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
