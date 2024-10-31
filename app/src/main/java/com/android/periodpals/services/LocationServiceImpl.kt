package com.android.periodpals.services

import android.Manifest
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Enum representing the type of location tha the user granted. */
enum class LocationAccessType {
  PRECISE,
  APPROXIMATE,
  NONE
}

/**
 * An implementation of the location service.
 *
 * @param activity The screen from which the location service is being launched from.
 */
class LocationServiceImpl(private val activity: ComponentActivity) : LocationService {

  private val TAG = "LocationServiceImpl: registerForActivityResult"

  /* For the moment, the locationGrantedType exposes the type of location access
  that the user granted. */
  private val _locationGrantedType = MutableStateFlow(LocationAccessType.NONE)
  val locationGrantedType = _locationGrantedType.asStateFlow()

  // Initialize the ActivityResultLauncher which handles the permission request process
  private val requestPermissionLauncher: ActivityResultLauncher<Array<String>> =
      activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
          permissions -> // callback after the user responds to the permission dialog
        try {
          when {
            // Precise location access granted
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
              _locationGrantedType.value = LocationAccessType.PRECISE
            }

            // Only approximate location access granted
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
              _locationGrantedType.value = LocationAccessType.APPROXIMATE
            }

            // No location access granted
            else -> {
              _locationGrantedType.value = LocationAccessType.NONE
            }
          }
        } catch (e: Exception) {
          Log.e(TAG, "Fail to create permission launcher")
        }
      }

  override fun requestUserPermissionForLocation() {
    requestPermissionLauncher.launch(
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
  }
}