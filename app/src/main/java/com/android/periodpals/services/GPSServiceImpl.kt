package com.android.periodpals.services

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.android.periodpals.model.authentication.AuthenticationViewModel
import com.android.periodpals.model.location.Location
import com.android.periodpals.model.location.UserLocationViewModel
import com.android.periodpals.model.location.parseLocationGIS
import com.google.android.gms.location.FusedLocationProviderClient
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
private const val TAG_SWITCH_APPROX = CLASS_NAME + "switchToApproximate"
private const val TAG_SWITCH_PRECISE = CLASS_NAME + "switchToPrecise"
private const val TAG_UPLOAD_LOCATION = CLASS_NAME + "uploadUserLocation"

// Interval between each location update in milliseconds
private const val LOCATION_UPDATE_INTERVAL: Long = 2000

private enum class REQUEST_TYPE {
  PRECISE,
  APPROXIMATE,
}

/**
 * An implementation of the [GPSService] interface.
 * - The location is exposed through the [location] state flow.
 * - The accuracy of the location is exposed through the [accuracy] state flow.
 *
 * @param activity Activity from where the GPSService is being initialized.
 */
class GPSServiceImpl(
    private val activity: ComponentActivity,
    private val authenticationViewModel: AuthenticationViewModel,
    private val userLocationViewModel: UserLocationViewModel,
) : GPSService {
  private var _location = MutableStateFlow(Location.DEFAULT_LOCATION)
  val location = _location.asStateFlow()

  private var _accuracy = MutableStateFlow(0.0F)
  val accuracy = _accuracy.asStateFlow()

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
              startFusedLocationClient()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
              Log.d(TAG_ACTIVITY_RESULT, "Approximate location granted")
              startFusedLocationClient()
            }
            else -> {
              Log.d(TAG_ACTIVITY_RESULT, "No location granted")
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
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ))
      } catch (e: Exception) {
        Log.e(ASK_AND_UPDATE, "Failed launching permission request")
      }
    } else {
      Log.d(ASK_AND_UPDATE, "Permissions already granted, starting location updates")
      startFusedLocationClient()
    }
  }

  /**
   * The @suppress is needed because the Android API wants to add the if statement that is inside
   * the [permissionsAreGranted] function.
   */
  @SuppressLint("MissingPermission")
  override fun switchFromPreciseToApproximate() {
    uploadUserLocation()
    if (approximateIsGranted() && isTrackingLocation && requestType == REQUEST_TYPE.PRECISE) {
      try {
        locationCallback?.let { callback ->
          // First, remove existing updates
          fusedLocationClient?.removeLocationUpdates(callback)

          // Then, request location updates with approximate accuracy
          fusedLocationClient?.requestLocationUpdates(
              approximateLocationRequest,
              callback,
              Looper.getMainLooper(),
          )
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
    if (approximateIsGranted() && isTrackingLocation && requestType == REQUEST_TYPE.APPROXIMATE) {
      try {
        locationCallback?.let { callback ->
          // First, remove existing updates
          fusedLocationClient?.removeLocationUpdates(callback)

          // Then, request location updates with precise accuracy
          fusedLocationClient?.requestLocationUpdates(
              preciseLocationRequest,
              callback,
              Looper.getMainLooper(),
          )
          requestType = REQUEST_TYPE.PRECISE
          Log.d(TAG_SWITCH_PRECISE, "Switched to precise location")
          uploadUserLocation()
        }
      } catch (e: Exception) {
        Log.e(TAG_SWITCH_APPROX, "Failed switching to precise location", e)
      }
    }
  }

  override fun cleanup() {
    uploadUserLocation()
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
   * Uploads the user's location to the server.
   *
   * This function loads the user data from the `UserViewModel`, updates the user's location with
   * the current GPS location, and then saves the updated user data back to the server.
   */
  private fun uploadUserLocation() {
    Log.d(TAG_UPLOAD_LOCATION, "Uploading user location")
    authenticationViewModel.loadAuthenticationUserData(
        onSuccess = {
          if (authenticationViewModel.authUserData.value == null) {
            Log.e(TAG_UPLOAD_LOCATION, "User data is null")
            return@loadAuthenticationUserData
          }
          val uid = authenticationViewModel.authUserData.value?.uid!!
          val location = parseLocationGIS(_location.value)
          Log.d(TAG_UPLOAD_LOCATION, "Uploading location: ${_location.value}")
          userLocationViewModel.uploadUserLocation(uid = uid, location = location)
        },
        onFailure = { Log.e(TAG_UPLOAD_LOCATION, "Failed to upload user location") },
    )
  }

  /**
   * Starts location updates based on current location access permissions. Uses the high-accuracy
   * location request by default.
   */
  @SuppressLint("MissingPermission")
  private fun startFusedLocationClient() {
    Log.d(ASK_AND_UPDATE, "Is approximate granted: ${approximateIsGranted()}")
    if (approximateIsGranted() && !isTrackingLocation) {
      try {
        locationCallback?.let { callback ->
          fusedLocationClient?.requestLocationUpdates(
              preciseLocationRequest,
              callback,
              Looper.getMainLooper(),
          )
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
              _location.value =
                  Location(
                      lat,
                      long,
                      Location.CURRENT_LOCATION_NAME,
                  ) // TODO change CURRENT_LOCATION_NAME to actual
              // location based on the coordinates

              _accuracy.value = location.accuracy

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

  /** Returns `true` if the approximate location access is granted. */
  private fun approximateIsGranted(): Boolean {
    return ActivityCompat.checkSelfPermission(
        activity,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    ) == PackageManager.PERMISSION_GRANTED
  }
}
