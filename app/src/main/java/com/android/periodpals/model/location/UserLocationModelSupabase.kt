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
   * Creates a new location in the Supabase database.
   *
   * @param locationDto The [LocationGIS] data transfer object to be inserted.
   * @param onSuccess A callback function to be invoked upon successful operation.
   * @param onFailure A callback function to be invoked with an Exception if the operation fails.
   */
  override suspend fun create(
    locationDto: UserLocationDto,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit,
  ) {
    Log.d(TAG, "create: sending location dto: $locationDto")
    try {
      withContext(Dispatchers.IO) {
        supabase.postgrest[LOCATIONS].insert(locationDto)
        Log.d(TAG, "create: success")
        onSuccess()
      }
    } catch (e: Exception) {
      Log.e(TAG, "create: fail to create location of user ${locationDto.uid}: ${e.message}")
      onFailure(e)
    }
  }

  /**
   * Updates a location in the Supabase database.
   *
   * @param locationDto The [LocationGIS] data transfer object to be inserted or updated.
   * @param onSuccess A callback function to be invoked upon successful operation.
   * @param onFailure A callback function to be invoked with an Exception if the operation fails.
   */
  override suspend fun update(
    locationDto: UserLocationDto,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit,
  ) {
    Log.d(TAG, "update: sending location dto: $locationDto")
    try {
      withContext(Dispatchers.IO) {
        supabase.postgrest[LOCATIONS].update(locationDto) { filter { eq("uid", locationDto.uid) } }
        Log.d(TAG, "update: success")
        onSuccess()
      }
    } catch (e: Exception) {
      Log.e(TAG, "update: fail to update location of user ${locationDto.uid}: ${e.message}")
      onFailure(e)
    }
  }
}
