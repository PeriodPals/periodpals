package com.android.periodpals.services

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.android.periodpals.model.location.GPSLocation
import com.google.android.gms.location.FusedLocationProviderClient
//import com.android.periodpals.services.LocationAccessType.APPROXIMATE
//import com.android.periodpals.services.LocationAccessType.DENIED
//import com.android.periodpals.services.LocationAccessType.NOT_YET_ASKED
//import com.android.periodpals.services.LocationAccessType.PRECISE
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

// Debugging tags
private const val CLASS_NAME = "GPSServiceImpl: "
private const val TAG_REG = CLASS_NAME + "registerForActivityResult"
private const val ASK_AND_UPDATE = CLASS_NAME + "startGPSUserLocation"
private const val TAG_CLEANUP = CLASS_NAME + "stopGPSUserLocation"
private const val TAG_CALLBACK = CLASS_NAME + "onLocationResult"
private const val TAG_ACTIVITY_RESULT = CLASS_NAME + "registerForActivityResult"

// Interval between each location update in milliseconds
private const val LOCATION_UPDATE_INTERVAL: Long = 10000

/**
 * Enum representing the type of location tha the user granted:
 * - [PRECISE] is precise within 50 sq. meters.
 * - [APPROXIMATE] is precise within 3 sq. kilometers.
 * - [DENIED] user did not grant access to location.
 * - [NOT_YET_ASKED] the app has not asked for user's location.
 */
/*
enum class LocationAccessType {
  PRECISE,
  APPROXIMATE,
  DENIED,
  NOT_YET_ASKED
}*/

class GPSServiceImpl(private val activity: ComponentActivity) : GPSService {
  private var _location = MutableStateFlow(GPSLocation.DEFAULT_LOCATION)
  val location = _location.asStateFlow()

//  private val _locationGranted = MutableStateFlow(NOT_YET_ASKED)
//  val locationGranted = _locationGranted.asStateFlow()

  private var fusedLocationClient : FusedLocationProviderClient? = null
  private var locationCallback : LocationCallback? = null

  // Configures a high-accuracy location request with a specified update interval
  private val locationRequest =
    LocationRequest.Builder(LOCATION_UPDATE_INTERVAL)
      .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
      .build()

  private var isTrackingLocation = false

  init {
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
    initLocationCallback()
  }

  // Initializes a launcher to request location permissions and updates location access type
  // based on the user's choice
  private val requestPermissionLauncher: ActivityResultLauncher<Array<String>> =
      activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
          permissions ->
        try {
          when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
              Log.d(TAG_ACTIVITY_RESULT, "Precise location granted")
              Toast.makeText(activity, "Precise location granted", Toast.LENGTH_SHORT).show()
//              _locationGranted.value = PRECISE
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
              Log.d(TAG_ACTIVITY_RESULT, "Approximate location granted")
              Toast.makeText(activity, "Approximate location granted", Toast.LENGTH_SHORT).show()
//              _locationGranted.value = APPROXIMATE
            }
            else -> {
              Log.d(TAG_ACTIVITY_RESULT, "No location granted")
              Toast.makeText(activity, "No location granted", Toast.LENGTH_SHORT).show()
//              _locationGranted.value = DENIED
            }
          }
        } catch (e: Exception) {
          Log.e(TAG_REG, "Fail to create permission launcher")
        }
      }

  override fun askPermissionAndStartUpdates() {
    if (!permissionsAreGranted()) {
      try {
        requestPermissionLauncher.launch(
          arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION))
      } catch (e: Exception) {
        Log.e(ASK_AND_UPDATE, "Failed launching permission request")
      }
    }
    startFusedLocationClient()
  }

  override fun cleanup() {
    try {
      locationCallback?.let { callback ->
        fusedLocationClient?.removeLocationUpdates(callback)
      }
      fusedLocationClient = null
      locationCallback = null
      isTrackingLocation = false
      Log.d(TAG_CLEANUP, "Stopped location updates")

    } catch (e: Exception) {
      Log.e(TAG_CLEANUP, "Error removing location updates", e)
    }
  }

  override fun switchToApproximate() {
    TODO("Not yet implemented")
  }

  override fun switchToPrecise() {
    TODO("Not yet implemented")
  }

  private fun initLocationCallback() {
    locationCallback = object : LocationCallback() {
      override fun onLocationResult(result: LocationResult) {
        super.onLocationResult(result)

        result.lastLocation?.let { location ->
          val lat = location.latitude
          val long = location.longitude

          _location.value = GPSLocation(lat, long)
          Log.d(TAG_CALLBACK, "Last (lat, long): ($lat, $long)")

        } ?: run {
          Log.d(TAG_CALLBACK, "Last received location is null")
        }
      }
    }
  }

  // Starts location updates based on current location access permissions.
  @SuppressLint("MissingPermission")
  private fun startFusedLocationClient() {
    if (permissionsAreGranted() && !isTrackingLocation) {
      try {
        locationCallback?.let { callback ->
          fusedLocationClient?.requestLocationUpdates(
            locationRequest,
            callback,
            Looper.getMainLooper()
          )
          isTrackingLocation = true
          Log.d(ASK_AND_UPDATE, "FusedLocationClient created")
        }
      } catch (e: Exception) {
        Log.e(ASK_AND_UPDATE, "Error requesting location updates", e)
      }
    }
  }

  private fun permissionsAreGranted() : Boolean {
    return ActivityCompat.checkSelfPermission(
      activity,
      Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
      activity,
      Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
  }
}