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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import com.android.periodpals.model.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.ramani.compose.LocationRequestProperties

// Debugging tags
private const val CLASS_NAME = "GPSServiceImpl: "
private const val TAG_REG = CLASS_NAME + "registerForActivityResult"
private const val ASK_AND_UPDATE = CLASS_NAME + "startGPSUserLocation"
private const val TAG_CLEANUP = CLASS_NAME + "stopGPSUserLocation"
private const val TAG_CALLBACK = CLASS_NAME + "onLocationResult"
private const val TAG_ACTIVITY_RESULT = CLASS_NAME + "registerForActivityResult"
private const val TAG_SWITCH_APPROX = CLASS_NAME + "switchToApproximate"
private const val TAG_SWITCH_PRECISE = CLASS_NAME + "switchToPrecise"

// Interval between each location update in milliseconds
private const val LOCATION_UPDATE_INTERVAL: Long = 2000

private enum class REQUEST_TYPE {
  PRECISE,
  APPROXIMATE
}

/**
 * An implementation of the [GPSService] interface. The location is exposed through the [location]
 * state flow.
 *
 * @param activity Activity from where the GPSService is being initialized.
 */
class GPSServiceImpl(private val activity: ComponentActivity) : GPSService {
  private var _location = MutableStateFlow(Location.DEFAULT_LOCATION)
  val location = _location.asStateFlow()

  private val _locationPropertiesState: MutableState<LocationRequestProperties?> = mutableStateOf(null)
  val locationPropertiesState: MutableState<LocationRequestProperties?>
    get() = _locationPropertiesState

  private var fusedLocationClient: FusedLocationProviderClient? = null
  private var locationCallback: LocationCallback? = null

  // Configures a high-accuracy location request with a specified update interval
  private val preciseLocationRequest =
      LocationRequest.Builder(LOCATION_UPDATE_INTERVAL)
          .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
          .build()

  // Configures a low-power less accurate location request
  private val approximateLocationRequest =
      LocationRequest.Builder(LOCATION_UPDATE_INTERVAL)
          .setPriority(Priority.PRIORITY_LOW_POWER)
          .build()

  // The app does not start to track unless the askPermissionAndStartUpdates is called
  private var isTrackingLocation = false

  // By default, the location updates are set to precise
  private var requestType = REQUEST_TYPE.PRECISE

  init {
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
    initLocationCallback()
  }

  // Initializes a launcher to request location permissions
  private val requestPermissionLauncher: ActivityResultLauncher<Array<String>> =
      activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
          permissions ->
        try {
          when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
              Log.d(TAG_ACTIVITY_RESULT, "Precise location granted")
              _locationPropertiesState.value = LocationRequestProperties()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
              Log.d(TAG_ACTIVITY_RESULT, "Approximate location granted")
              _locationPropertiesState.value = LocationRequestProperties()
            }
            else -> {
              Log.d(TAG_ACTIVITY_RESULT, "No location granted")
              _locationPropertiesState.value = null
            }
          }
        } catch (e: Exception) {
          Log.e(TAG_REG, "Fail to create permission launcher")
        }
      }

  override fun askPermissionAndStartUpdates() {
    try {
      requestPermissionLauncher.launch(
          arrayOf(
              Manifest.permission.ACCESS_FINE_LOCATION,
              Manifest.permission.ACCESS_COARSE_LOCATION))
    } catch (e: Exception) {
      Log.e(ASK_AND_UPDATE, "Failed launching permission request")
    }
  }

  /**
   * The @suppress is needed because the Android API wants to add the if statement that is inside
   * the [permissionsAreGranted] function.
   */
  @SuppressLint("MissingPermission")
  override fun switchFromPreciseToApproximate() {
    if (permissionsAreGranted() && isTrackingLocation && requestType == REQUEST_TYPE.PRECISE) {
      try {
        locationCallback?.let { callback ->
          // First, remove existing updates
          fusedLocationClient?.removeLocationUpdates(callback)

          // Then, request location updates with approximate accuracy
          fusedLocationClient?.requestLocationUpdates(
              approximateLocationRequest, callback, Looper.getMainLooper())
          requestType = REQUEST_TYPE.APPROXIMATE
          Log.d(TAG_SWITCH_APPROX, "Switched to approximate location")
        }
      } catch (e: Exception) {
        Log.e(TAG_SWITCH_APPROX, "Failed switching to approximate location", e)
      }
    }
  }

  @SuppressLint("MissingPermission")
  override fun switchFromApproximateToPrecise() {
    if (permissionsAreGranted() && isTrackingLocation && requestType == REQUEST_TYPE.APPROXIMATE) {
      try {
        locationCallback?.let { callback ->
          // First, remove existing updates
          fusedLocationClient?.removeLocationUpdates(callback)

          // Then, request location updates with precise accuracy
          fusedLocationClient?.requestLocationUpdates(
              preciseLocationRequest, callback, Looper.getMainLooper())
          requestType = REQUEST_TYPE.PRECISE
          Log.d(TAG_SWITCH_PRECISE, "Switched to precise location")
        }
      } catch (e: Exception) {
        Log.e(TAG_SWITCH_APPROX, "Failed switching to precise location", e)
      }
    }
  }

  override fun cleanup() {
    try {
      locationCallback?.let { callback -> fusedLocationClient?.removeLocationUpdates(callback) }
      fusedLocationClient = null
      locationCallback = null
      isTrackingLocation = false
      Log.d(TAG_CLEANUP, "Stopped location updates")
    } catch (e: Exception) {
      Log.e(TAG_CLEANUP, "Error removing location updates", e)
    }
  }

  /**
   * Starts location updates based on current location access permissions. Uses the high-accuracy
   * location request by default.
   */
  @SuppressLint("MissingPermission")
  private fun startFusedLocationClient() {
    if (permissionsAreGranted() && !isTrackingLocation) {
      try {
        locationCallback?.let { callback ->
          fusedLocationClient?.requestLocationUpdates(
              preciseLocationRequest, callback, Looper.getMainLooper())
          isTrackingLocation = true
          Log.d(ASK_AND_UPDATE, "FusedLocationClient created")
        }
      } catch (e: Exception) {
        Log.e(ASK_AND_UPDATE, "Error requesting location updates", e)
      }
    }
  }

  /**
   * Defines the [LocationCallback] that is run whenever the [FusedLocationProviderClient] receives
   * a location update. This callback updates the [_location] value.
   */
  private fun initLocationCallback() {
    locationCallback =
        object : LocationCallback() {
          override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)

            result.lastLocation?.let { location ->
              val lat = location.latitude
              val long = location.longitude

              // TODO change CURRENT_LOCATION_NAME to actual location based on the coordinates
              _location.value = Location(lat, long, Location.CURRENT_LOCATION_NAME)
              Log.d(TAG_CALLBACK, "Last (lat, long): ($lat, $long)")
            } ?: run { Log.d(TAG_CALLBACK, "Last received location is null") }
          }
        }
  }

  /**
   * Returns `true` if the fine and approximate location access are granted and `false` otherwise.
   */
  private fun permissionsAreGranted(): Boolean {
    return ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
  }
}
