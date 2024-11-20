package com.android.periodpals.model.timer

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

private const val TAG = "TimerRepositorySupabase"
private const val TIMERS = "timers"

class TimerRepositorySupabase(private val supabaseClient: SupabaseClient) : TimerRepository {

  override suspend fun saveElapsedTime(
      timer: Timer,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      withContext(Dispatchers.IO) {
        val data =
            mapOf(
                "user_id" to timer.uid,
                "elapsed_time" to timer.elapsedTime,
                "average_time" to timer.averageTime,
                "timer_count" to timer.timerCount,
                "status" to timer.status)
        supabaseClient.postgrest[TIMERS].upsert(data)
        Log.d(TAG, "saveElapsedTime: success")
        onSuccess()
      }
    } catch (e: Exception) {
      Log.d(TAG, "saveElapsedTime: failure: ${e.message}")
      onFailure(e)
    }
  }

  override suspend fun getTimer(
      uid: String,
      onSuccess: (Timer) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      withContext(Dispatchers.IO) {
        val response =
            supabaseClient.postgrest[TIMERS]
                .select { filter { eq("user_id", UUID.fromString(uid)) } }
                .decodeSingle<TimerDto>()
        Log.d(TAG, "getTimer: success")
        onSuccess(response.toTimer())
      }
    } catch (e: Exception) {
      Log.d(TAG, "getTimer: failure: ${e.message}")
      onFailure(e)
    }
  }

  override suspend fun updateTimer(
      timer: Timer,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      withContext(Dispatchers.IO) {
        val data =
            mapOf(
                "elapsed_time" to timer.elapsedTime,
                "average_time" to timer.averageTime,
                "timer_count" to timer.timerCount,
                "status" to timer.status)
        supabaseClient.postgrest[TIMERS].update(data) { filter { eq("user_id", timer.uid) } }
        Log.d(TAG, "updateTimer: success")
        onSuccess()
      }
    } catch (e: Exception) {
      Log.d(TAG, "updateTimer: failure: ${e.message}")
      onFailure(e)
    }
  }
}
