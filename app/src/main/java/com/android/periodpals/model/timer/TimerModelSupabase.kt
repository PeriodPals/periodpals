package com.android.periodpals.model.timer

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "TimerModelSupabase"
private const val TIMERS = "timers"

class TimerModelSupabase(private val supabase: SupabaseClient) : TimerModel {
  override suspend fun getTimer(
      uid: String,
      onSuccess: (TimerDto) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      val result =
          withContext(Dispatchers.Main) {
            supabase.postgrest[TIMERS].select {}.decodeSingle<TimerDto>()
          }
      Log.d(TAG, "getTimer: Success")
      onSuccess(result)
    } catch (e: Exception) {
      Log.d(TAG, "getTimer: fail to load timer: ${e.message}")
      onFailure(e)
    }
  }

  override suspend fun createTimer(
      timer: Timer,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      withContext(Dispatchers.Main) {
        val timerDto = TimerDto(timer)
        supabase.postgrest[TIMERS].insert(timerDto)
      }
      Log.d(TAG, "createTimer: Success")
      onSuccess()
    } catch (e: Exception) {
      Log.d(TAG, "createTimer: fail to create timer: ${e.message}")
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
