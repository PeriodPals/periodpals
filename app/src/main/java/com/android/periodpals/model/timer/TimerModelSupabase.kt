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
 * @property supabaseClient The Supabase client used for making API calls.
 */
class TimerRepositorySupabase(private val supabaseClient: SupabaseClient) : TimerRepository {

  override suspend fun loadTimer(onSuccess: (TimerDto) -> Unit, onFailure: (Exception) -> Unit) {
    try {
      withContext(Dispatchers.Main) {
        val result =
            supabaseClient.postgrest[TIMERS]
                .select {}
                .decodeSingle<TimerDto>() // RLS rules only allows user to check their own line
        Log.d(TAG, "getTimer: Success")
        onSuccess(result)
      }
    } catch (e: Exception) {
      Log.d(TAG, "getTimer: fail to get timer: ${e.message}")
      onFailure(e)
    }
  }

  override suspend fun upsertTimer(
      timerDto: TimerDto,
      onSuccess: (TimerDto) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      withContext(Dispatchers.Main) {
        val result =
            supabaseClient.postgrest[TIMERS].upsert(timerDto) { select() }.decodeSingle<TimerDto>()
        Log.d(TAG, "upsertTimer: Success")
        onSuccess(result)
      }
    } catch (e: Exception) {
      Log.d(TAG, "upsertTimer: fail to upsert timer: ${e.message}")
      onFailure(e)
    }
  }
}
