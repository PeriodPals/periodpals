package com.android.periodpals.model.location

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class UserLocationViewModel(private val userLocationModel: UserLocationModel) : ViewModel() {

  companion object {
    private const val TAG = "UserLocationViewModel"
  }

  /**
   * Create a user location, or update it if it does already exists.
   *
   * Note: This implementation does not use upsert for security reasons.
   *
   * @param uid The unique identifier of the user.
   * @param location The new [LocationGIS] of the user.
   * @param onSuccess A callback function to be invoked upon successful operation.
   * @param onFailure A callback function to be invoked with an `Exception` if the operation fails.
   */
  fun uploadUserLocation(
      uid: String,
      location: LocationGIS,
      onSuccess: () -> Unit = { Log.d(TAG, "uploadUserLocation success callback") },
      onFailure: (Exception) -> Unit = { e: Exception ->
        Log.d(TAG, "uploadUserLocation failure callback: ${e.message}")
      },
  ) {
    val locationDto = UserLocationDto(uid = uid, location = location)
    viewModelScope.launch {
      // try to create a new location
      Log.d(TAG, "uploadUserLocation: trying to create location $locationDto for user $uid")
      userLocationModel.create(
          locationDto = locationDto,
          onSuccess = {
            Log.d(TAG, "uploadUserLocation: create user location successful")
            onSuccess()
          },
          onFailure = { e: Exception ->
            Log.d(TAG, "createUserLocation: location already exists, updating instead")
            update(locationDto, onSuccess, onFailure)
          },
      )
    }
  }

  private fun update(
      locationDto: UserLocationDto,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit,
  ) {
    Log.d(TAG, "update: trying to update location $locationDto")
    viewModelScope.launch {
      userLocationModel.update(
          locationDto = locationDto,
          onSuccess = {
            Log.d(TAG, "update: update user location successful")
            onSuccess()
          },
          onFailure = { e: Exception ->
            Log.d(TAG, "update: failed to upsert location of user ${locationDto.uid}: ${e.message}")
            onFailure(e)
          },
      )
    }
  }
}
