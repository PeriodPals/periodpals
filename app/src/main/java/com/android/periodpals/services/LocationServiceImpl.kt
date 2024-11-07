package com.android.periodpals.services

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.android.periodpals.model.location.GPSLocation
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val MY_PRECISE_LOCATION = "myPreciseLocation"
private const val TAG = "LocationServiceImpl: registerForActivityResult"

/** Enum representing the type of location tha the user granted. */
enum class LocationAccessType {
  PRECISE,
  APPROXIMATE,
  NONE,
  NOT_YET_ASKED
}

/**
 * An implementation of the location service.
 *
 * @param activity The screen from which the location service is being launched from.
 */
class LocationServiceImpl(private val activity: ComponentActivity) : LocationService {

  private var _location = MutableStateFlow<GPSLocation>(GPSLocation.DEFAULT_LOCATION)
  val location = _location.asStateFlow()

  private val _locationGrantedType = MutableStateFlow(LocationAccessType.NOT_YET_ASKED)
  val locationGrantedType = _locationGrantedType.asStateFlow()

  private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)

  // Initialize the ActivityResultLauncher which handles the permission request process
  private val requestPermissionLauncher: ActivityResultLauncher<Array<String>> =
    activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
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
        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
      )
    )
  }

  override fun refreshCurrentLocation() = logicOfCurrentLocation()

  @SuppressLint("MissingPermission")
  private fun logicOfCurrentLocation() {
    when (locationGrantedType.value) {
      LocationAccessType.NOT_YET_ASKED -> {
        requestUserPermissionForLocation()
        logicOfCurrentLocation()
      }
      LocationAccessType.PRECISE, LocationAccessType.APPROXIMATE -> {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
          location?.let {
            _location.value = GPSLocation(it.latitude, it.longitude)
          }
        }
      }
      LocationAccessType.NONE -> _location.value = GPSLocation.DEFAULT_LOCATION
    }
  }
}
