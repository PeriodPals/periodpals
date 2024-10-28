package com.android.periodpals.services

import android.Manifest
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

/**
 * An implementation of the location service.
 *
 * @param activity The screen from which the location service is being launched from.
 */
class LocationServiceImpl(private val activity: ComponentActivity) : LocationService {

  // Initialize the ActivityResultLauncher which handles the permission request process
  private val requestPermissionLauncher: ActivityResultLauncher<Array<String>> =
      activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
          permissions -> // callback after the user responds to the permission dialog
        when {
          // Precise location access granted
          permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
            Toast.makeText(activity, "Precise location access granted", Toast.LENGTH_LONG).show()
          }

          // Only approximate location access granted
          permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
            Toast.makeText(activity, "Only approximate location access granted", Toast.LENGTH_LONG)
                .show()
          }

          // No location access granted
          else -> {
            Toast.makeText(activity, "No location access granted", Toast.LENGTH_LONG).show()
          }
        }
      }

  override fun requestUserPermissionForLocation() {
    // Launch the permission request
    requestPermissionLauncher.launch(
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
  }
}
