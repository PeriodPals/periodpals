package com.android.periodpals.services

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.Manifest
import androidx.activity.result.contract.ActivityResultContracts

/**
 * An implementation of the location service.
 * @param activity The screen from which the location service is being launched from.
 */
class LocationServiceImpl(
  private val activity: ComponentActivity
) : LocationService {

  private val requestPermissionLauncher: ActivityResultLauncher<Array<String>> =
    activity.registerForActivityResult(
      ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
      when {
        permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
          // Precise location access granted
        }
        permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
        // Only approximate location access granted
        }
        else -> {
          // No location access granted
        }
      }
    }

  override fun requestUserPermissionForLocation() {
    requestPermissionLauncher.launch(
      arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION)
    )
  }
}
