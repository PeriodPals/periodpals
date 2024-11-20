package com.android.periodpals.model.timer

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "TimerRepositorySupabase"
private const val TIMERS = "timers"

class TimerRepositorySupabase(private val supabase: SupabaseClient) : TimerRepository {
  override suspend fun getTimer(
      uid: String,
      onSuccess: (Timer) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      val result =
          withContext(Dispatchers.Main) {
            supabase.postgrest[TIMERS].select {}.decodeSingle<TimerDto>()
          }
      Log.d(TAG, "getTimer: Success")
      onSuccess(result.toTimer())
    } catch (e: Exception) {
      Log.d(TAG, "getTimer: fail to load timer: ${e.message}")
      onFailure(e)
    }
  }

  override suspend fun upsertTimer(
      timer: TimerDto,
      onSuccess: (TimerDto) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      withContext(Dispatchers.Main) {
        val result = supabase.postgrest[TIMERS].upsert(timer) { select() }.decodeSingle<TimerDto>()
        Log.d(TAG, "upsertTimer: Success")
        onSuccess(result)
      }
    } catch (e: Exception) {
      Log.d(TAG, "upsertTimer: fail to upsert timer: ${e.message}")
      onFailure(e)
    }
  }
}
