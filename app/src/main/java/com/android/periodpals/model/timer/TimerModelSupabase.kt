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

  /**
   * Retrieves the timer data for the specified user. RLS rules only allows user to check their own
   * line in the database so the user ID is not required.
   *
   * @param onSuccess The callback to be invoked when the timer data is retrieved successfully.
   * @param onFailure The callback to be invoked when an error occurs.
   */
  override suspend fun getTimer(onSuccess: (Timer) -> Unit, onFailure: (Exception) -> Unit) {
    try {
      withContext(Dispatchers.IO) {
        val response = supabaseClient.postgrest[TIMERS].select {}.decodeSingle<TimerDto>()
        Log.d(TAG, "getTimer: success")
        onSuccess(response.toTimer())
      }
    } catch (e: Exception) {
      Log.d(TAG, "getTimer: failure: ${e.message}")
      onFailure(e)
    }
  }

  /**
   * Updates the specified timer. If the timer does not exist, it is created.
   *
   * @param timerDto The timer data to update.
   * @param onSuccess The callback to be invoked when the timer is updated successfully.
   * @param onFailure The callback to be invoked when an error occurs while updating the timer.
   */
  override suspend fun upsertTimer(
      timerDto: TimerDto,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      withContext(Dispatchers.IO) {
        supabaseClient.postgrest[TIMERS].update(timerDto) { select() }.decodeSingle<TimerDto>()
        Log.d(TAG, "updateTimer: success")
        onSuccess()
      }
    } catch (e: Exception) {
      Log.d(TAG, "updateTimer: failure: ${e.message}")
      onFailure(e)
    }
  }
}
