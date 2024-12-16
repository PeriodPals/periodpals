package com.android.periodpals.model.location

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class UserLocationViewModel(private val userLocationModel: UserLocationModel) : ViewModel() {

  companion object {
    private const val TAG = "UserLocationViewModel"
  }

  fun updateUserLocation(
    uid: String,
    location: LocationGIS,
    onSuccess: () -> Unit = { Log.d(TAG, "updateUserLocation success callback") },
    onFailure: (Exception) -> Unit = { e: Exception ->
      Log.d(TAG, "updateUserLocation failure callback: ${e.message}")
    },
  ) {
    viewModelScope.launch {
      val locationDto = UserLocationDto(uid = uid, location = location)
      userLocationModel.upsert(
        locationDto = locationDto,
        onSuccess = {
          Log.d(TAG, "updateUserLocation: Success")
          onSuccess()
        },
        onFailure = { e: Exception ->
          Log.d(TAG, "updateUserLocation: fail to upsert location: ${e.message}")
          onFailure(e)
        },
      )
    }
  }
}
