package com.android.periodpals.model.location

/** Interface representing a model for user location. */
interface UserLocationModel {

  /**
   * Inserts or updates a location.
   *
   * @param uid The unique identifier of the user.
   * @param location The `LocationGIS` object to be inserted or updated.
   * @param onSuccess A callback function to be invoked with the updated `LocationGIS` object upon
   *   successful operation.
   * @param onFailure A callback function to be invoked with an `Exception` if the operation fails.
   */
  suspend fun upsert(
    locationDto: UserLocationDto,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit,
  )
}
