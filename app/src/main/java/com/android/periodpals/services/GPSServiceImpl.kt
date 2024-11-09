package com.android.periodpals.services

import android.Manifest
import android.annotation.SuppressLint
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.android.periodpals.model.location.GPSLocation
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

// Debugging tags
private const val CLASS_NAME = "LocationServiceImpl: "
private const val TAG_REG = CLASS_NAME + "registerForActivityResult"
private const val TAG_START_LOCATION = CLASS_NAME + "startGPSUserLocation"
private const val TAG_STOP_LOCATION = CLASS_NAME + "stopGPSUserLocation"
private const val TAG_CALLBACK = CLASS_NAME + "onLocationResult"
private const val TAG_ACTIVITY_RESULT = CLASS_NAME + "registerForActivityResult"
private const val TAG_ASK_PERMISSION = CLASS_NAME + "askUserForLocationPermission"

// Interval between each location update in milliseconds
private const val LOCATION_UPDATE_INTERVAL : Long = 10000

/** Enum representing the type of location tha the user granted:
 * - [PRECISE] is precise within 50 sq. meters.
 * - [APPROXIMATE] is precise within 3 sq. kilometers.
 * - [NONE] user did not grant access to location.
 * - [NOT_YET_ASKED] the app has not asked for user's location. */
enum class LocationAccessType {
  PRECISE,
  APPROXIMATE,
  NONE,
  NOT_YET_ASKED
}

/**
 * An implementation of the GPS service.
 *
 * It is in charge of starting the GPS location updates with [startGPSUserLocation] and stopping the
 * location updates with [stopGPSUserLocation]. It handles the  request to access the user location
 * internally.
 *
 * In addition, it exposes the location access granted by the user via [locationGrantedType].
 *
 * @param activity The screen from which the location service is being launched from.
 */
class GPSServiceImpl(private val activity: ComponentActivity) : GPSService {
  private var _location = MutableStateFlow(GPSLocation.DEFAULT_LOCATION)
  val location = _location.asStateFlow()

  private val _locationGrantedType = MutableStateFlow(LocationAccessType.NOT_YET_ASKED)
  val locationGrantedType = _locationGrantedType.asStateFlow()

  private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)

  // Configures a high-accuracy location request with a specified update interval
  private val locationRequest = LocationRequest
    .Builder(LOCATION_UPDATE_INTERVAL)
    .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
    .build()

  // Initializes a launcher to request location permissions and updates location access type
  // based on the user's choice
  private val requestPermissionLauncher: ActivityResultLauncher<Array<String>> =
    activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
      try {
        when {
          permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
            Log.d(TAG_ACTIVITY_RESULT, "Location granted: ${LocationAccessType.PRECISE}")
            Toast.makeText(activity, "Precise location granted", Toast.LENGTH_SHORT).show()
            _locationGrantedType.value = LocationAccessType.PRECISE
          }
          permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
            Log.d(TAG_ACTIVITY_RESULT, "Location granted: ${LocationAccessType.APPROXIMATE}")
            Toast.makeText(activity, "Approximate location granted", Toast.LENGTH_SHORT).show()
            _locationGrantedType.value = LocationAccessType.APPROXIMATE

          }
          else -> {
            Log.d(TAG_ACTIVITY_RESULT, "Location granted: ${LocationAccessType.NONE}")
            Toast.makeText(activity, "No location granted", Toast.LENGTH_SHORT).show()
            _locationGrantedType.value = LocationAccessType.NONE
          }
        }
      } catch (e: Exception) {
        Log.e(TAG_REG, "Fail to create permission launcher")
      }
    }

  // Requests user for location permission if not already asked.
  override fun askUserForLocationPermission() {
    if (locationGrantedType.value == LocationAccessType.NOT_YET_ASKED) {
      try {
        requestPermissionLauncher.launch(
          arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
          )
        )
      } catch (e: Exception) {
        Log.e(TAG_ASK_PERMISSION, "Failed launching permission request")
      }
    }
  }

  // Defines a callback to handle location updates and updates the exposed location to the last
  // known device location
  private val locationCallback = object : LocationCallback() {
    override fun onLocationResult(l: LocationResult) {
      super.onLocationResult(l)

      if (l.lastLocation != null) {
        Log.d(
          TAG_CALLBACK,
          "Last (lat, long): (${l.lastLocation!!.latitude}, ${l.lastLocation!!.longitude}"
        )
        _location.value = GPSLocation(l.lastLocation!!.latitude, l.lastLocation!!.longitude)
      } else {
        Log.d(TAG_CALLBACK, "Last location received is null")
      }
    }
  }

  // Explanation for the @SuppressLint("MissingPermission"):
  //
  // The Android API for location wants to do an explicit check that the access is granted:
  //
  // if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
  //      != PackageManager.PERMISSION_GRANTED) { ... }
  //
  // However, exactly this is done with the LocationAccessType enum, which is more readable.

  // Starts location updates based on current location access permissions.
  @SuppressLint("MissingPermission")
  override fun startGPSUserLocation() {
    when (locationGrantedType.value) {
      LocationAccessType.NOT_YET_ASKED -> { // Must be manually asked by calling askUserForPermission()
        Log.d(TAG_START_LOCATION, "Not yet asked for location access permission")
      }
      LocationAccessType.NONE -> {
        Log.d(TAG_START_LOCATION, "Location access is not granted")
        Toast.makeText(activity, "Please modify location permissions", Toast.LENGTH_SHORT).show()
      }
      LocationAccessType.PRECISE, LocationAccessType.APPROXIMATE -> {
        Log.d(TAG_START_LOCATION, "Location access granted: ${locationGrantedType.value}")
        Toast.makeText(activity, "Started location updates", Toast.LENGTH_SHORT).show()
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
      }
    }
  }

  // Stops location updates.
  override fun stopGPSUserLocation() {
    Log.d(TAG_STOP_LOCATION, "Stopped location updates")
    Toast.makeText(activity, "Stopped location updates", Toast.LENGTH_SHORT).show()
    fusedLocationClient.removeLocationUpdates(locationCallback)
  }
}
