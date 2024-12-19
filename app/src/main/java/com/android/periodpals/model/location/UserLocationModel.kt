package com.android.periodpals.model.location

/** Interface representing a model for user location. */
interface UserLocationModel {

  /**
   * Creates a new location.
   *
   * @param locationDto The [Location] data transfer object to be inserted.
   * @param onSuccess A callback function to be invoked upon successful operation.
   * @param onFailure A callback function to be invoked with an `Exception` if the operation fails.
   */
  suspend fun create(
    locationDto: UserLocationDto,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit,
  )

  /**
   * Inserts or updates a location.
   *
   * @param locationDto The [Location] data transfer object to be inserted or updated.
   * @param onSuccess A callback function to be invoked upon successful operation.
   * @param onFailure A callback function to be invoked with an `Exception` if the operation fails.
   */
  suspend fun update(
    locationDto: UserLocationDto,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit,
  )
}
