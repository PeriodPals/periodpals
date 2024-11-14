package com.android.periodpals.services

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.android.periodpals.model.location.GPSLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.capture
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.isNull
import org.mockito.kotlin.verify

// See GPSServiceImpl to check the update interval being used
private const val UPDATE_INTERVAL = 2000L

@RunWith(MockitoJUnitRunner::class)
class GPSServiceImplTest {

  @Mock private lateinit var mockActivity: ComponentActivity

  @Mock private lateinit var mockFusedLocationClient: FusedLocationProviderClient

  @Mock private lateinit var mockPermissionLauncher: ActivityResultLauncher<Array<String>>

  // Used to get the FusedLocationProviderClient
  private lateinit var mockLocationServices: MockedStatic<LocationServices>

  // ActivityCompat contains the permissions
  private lateinit var mockActivityCompat: MockedStatic<ActivityCompat>

  // Callback inside the requestLocationUpdates method (see startFusedLocationClient() in
  // GPSServiceImpl)
  private val locationCallbackCaptor = ArgumentCaptor.forClass(LocationCallback::class.java)

  // LocationRequest (e.g. preciseLocationRequest in GPSServiceImpl)
  private val locationRequestCaptor = ArgumentCaptor.forClass(LocationRequest::class.java)

  @Captor
  private lateinit var permissionCallbackCaptor:
      ArgumentCaptor<ActivityResultCallback<Map<String, Boolean>>>

  private lateinit var gpsService: GPSServiceImpl

  @Before
  fun setup() {
    MockitoAnnotations.openMocks(this)

    mockActivity = mock(ComponentActivity::class.java)
    mockFusedLocationClient = mock(FusedLocationProviderClient::class.java)

    // Mock static LocationServices
    mockLocationServices = mockStatic(LocationServices::class.java)

    /* Mocking this line in GPSServiceImpl:
      init {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        ...
      }
    */
    mockLocationServices
        .`when`<FusedLocationProviderClient> {
          LocationServices.getFusedLocationProviderClient(mockActivity)
        }
        .thenReturn(mockFusedLocationClient)

    // Mock static ActivityCompat
    mockActivityCompat = mockStatic(ActivityCompat::class.java)

    // Mock denied permissions
    mockActivityCompat
        .`when`<Int> {
          ActivityCompat.checkSelfPermission(mockActivity, Manifest.permission.ACCESS_FINE_LOCATION)
        }
        .thenReturn(PackageManager.PERMISSION_DENIED)

    mockActivityCompat
        .`when`<Int> {
          ActivityCompat.checkSelfPermission(
              mockActivity, Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        .thenReturn(PackageManager.PERMISSION_DENIED)

    // Mock the registerForActivityResult
    doReturn(mockPermissionLauncher)
        .`when`(mockActivity)
        .registerForActivityResult(
            any<ActivityResultContracts.RequestMultiplePermissions>(),
            any<ActivityResultCallback<Map<String, Boolean>>>())

    // Create instance of GPSServiceImpl...
    gpsService = GPSServiceImpl(mockActivity)

    // ... and verify that registerForActivityResult was called
    verify(mockActivity)
        .registerForActivityResult(
            any<ActivityResultContracts.RequestMultiplePermissions>(),
            capture(permissionCallbackCaptor))
  }

  @After
  fun tearDown() {
    mockLocationServices.close()
    mockActivityCompat.close()
  }

  @Test
  fun `initial location should be default`() = runTest {
    val initialLocation = gpsService.location.first()
    assert(initialLocation == GPSLocation.DEFAULT_LOCATION)
  }

  @Test
  fun `askPermissionAndStartUpdates should launch permission request when permissions not granted`() {
    // Given permissions are not granted (setup in @Before)

    // When
    gpsService.askPermissionAndStartUpdates()

    // Then
    verify(mockPermissionLauncher)
        .launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION))
  }

  @Test
  fun `askPermissionAndStartUpdates should start updates when permissions granted`() {
    // Given
    mockPermissionsGranted()

    // When
    gpsService.askPermissionAndStartUpdates()

    // Then
    verify(mockFusedLocationClient)
        .requestLocationUpdates(
            locationRequestCaptor.capture(),
            locationCallbackCaptor.capture(),
            isNull() // Since we are calling from a unit test, the Looper is null
            )

    /* Explanation: in the previous line, Mockito verifies that this block of code was called:

         locationCallback?.let { callback ->
           fusedLocationClient?.requestLocationUpdates(
             preciseLocationRequest,
             callback,
             Looper.getMainLooper()
           )
          ...
         }
    */

    // Verify the location request priority
    assert(locationRequestCaptor.value.priority == Priority.PRIORITY_HIGH_ACCURACY)

    // Verify the location request update interval
    assert(locationRequestCaptor.value.intervalMillis == UPDATE_INTERVAL)
  }

  @Test
  fun `switchFromPreciseToApproximate should update location request when tracking`() {
    // Given
    mockPermissionsGranted()

    // Start tracking
    gpsService.askPermissionAndStartUpdates()

    // When
    gpsService.switchFromPreciseToApproximate()

    // Then
    // Verify that requestLocationUpdates was called twice (one for askPermissionAndStartUpdates
    // and one of switchFromPreciseToApproximate)
    verify(mockFusedLocationClient, Mockito.times(2))
        .requestLocationUpdates(
            locationRequestCaptor.capture(), locationCallbackCaptor.capture(), isNull())

    // Verify that the last location request was low power and approx
    val lastRequest = locationRequestCaptor.allValues.last()
    assert(lastRequest.priority == Priority.PRIORITY_LOW_POWER)
  }

  @Test
  fun `switchFromApproximateToPrecise should update location request when tracking`() {
    // Given
    mockPermissionsGranted()

    // Start tracking and switch to approximate
    gpsService.askPermissionAndStartUpdates()
    gpsService.switchFromPreciseToApproximate()

    // When
    gpsService.switchFromApproximateToPrecise()

    // Then
    // Verify that requestLocationUpdates was called three times
    verify(mockFusedLocationClient, Mockito.times(3))
        .requestLocationUpdates(
            locationRequestCaptor.capture(), locationCallbackCaptor.capture(), isNull())

    val lastRequest = locationRequestCaptor.allValues.last()
    assert(lastRequest.priority == Priority.PRIORITY_HIGH_ACCURACY)
  }

  @Test
  fun `cleanup should remove location updates and reset state`() {
    // When
    gpsService.cleanup()

    // Then
    verify(mockFusedLocationClient).removeLocationUpdates(any<LocationCallback>())
  }

  @Test
  fun `location callback should update location state flow`() = runTest {
    // Given
    mockPermissionsGranted()
    gpsService.askPermissionAndStartUpdates()

    // Capture location callback
    verify(mockFusedLocationClient)
        .requestLocationUpdates(any(), locationCallbackCaptor.capture(), isNull())

    // Create mock location
    val mockLat = 42.0
    val mockLong = 16.0
    val mockLocation = mock(android.location.Location::class.java)
    `when`(mockLocation.latitude).thenReturn(mockLat)
    `when`(mockLocation.longitude).thenReturn(mockLong)

    // Create mock location result
    val mockLocationResult = mock(LocationResult::class.java)
    `when`(mockLocationResult.lastLocation).thenReturn(mockLocation)

    // When
    locationCallbackCaptor.value.onLocationResult(mockLocationResult)

    // Then
    val updatedLocation = gpsService.location.first()

    // Verify that the location State Flow was correctly updated
    assert(updatedLocation.lat == mockLat)
    assert(updatedLocation.long == mockLong)

    /* Explanation: we are verifying that the LocationCallback was executed and that it
                   updated the location State Flow. In GPSServiceImpl:

       locationCallback = object : LocationCallback() {
         override fun onLocationResult(result: LocationResult) {
           super.onLocationResult(result)

           result.lastLocation?.let { location ->
             val lat = location.latitude
             val long = location.longitude

             _location.value = GPSLocation(lat, long)
             ...
           }
           ...
           }
         }
       }
    */
  }

  private fun mockPermissionsGranted() {
    mockActivityCompat
        .`when`<Int> {
          ActivityCompat.checkSelfPermission(mockActivity, Manifest.permission.ACCESS_FINE_LOCATION)
        }
        .thenReturn(PackageManager.PERMISSION_GRANTED)

    mockActivityCompat
        .`when`<Int> {
          ActivityCompat.checkSelfPermission(
              mockActivity, Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        .thenReturn(PackageManager.PERMISSION_GRANTED)
  }
}
