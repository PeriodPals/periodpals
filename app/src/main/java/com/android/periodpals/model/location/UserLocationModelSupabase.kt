package com.android.periodpals.model.location

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Implementation of the UserLocationModel interface using Supabase.
 *
 * @property supabase The Supabase client used for database operations.
 */
class UserLocationModelSupabase(private val supabase: SupabaseClient) : UserLocationModel {

  companion object {
    private const val TAG = "UserLocationModelSupabase"
    private const val LOCATIONS = "locations"
  }

  /**
   * Inserts or updates a location in the Supabase database.
   *
   * @param location The Location data transfer object to be inserted or updated.
   * @param onSuccess A callback function to be invoked upon successful operation.
   * @param onFailure A callback function to be invoked with an Exception if the operation fails.
   */
  override suspend fun upsert(
    locationDto: UserLocationDto,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit,
  ) {
    Log.d(TAG, "upsert: sending location dto: $locationDto")
    try {
      withContext(Dispatchers.IO) {
        supabase.postgrest[LOCATIONS].upsert(locationDto)
        Log.d(TAG, "upsert: Success")
        onSuccess()
      }
    } catch (e: Exception) {
      Log.e(TAG, "upsert: fail to upsert location: ${e.message}")
      onFailure(e)
    }
  }
}
