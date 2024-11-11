package com.android.periodpals.services

import android.Manifest
import android.app.Activity
import android.app.FragmentManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.capture
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

// @RunWith(RobolectricTestRunner::class)
@RunWith(MockitoJUnitRunner::class)
class GPSServiceImplTest {

  @Mock
  private lateinit var mockActivity: ComponentActivity

  @Mock
  private lateinit var mockFusedLocationProviderClient: FusedLocationProviderClient

  @Mock
  private lateinit var mockPermissionLauncher: ActivityResultLauncher<Array<String>>

  @Captor
  private lateinit var permissionCallbackCaptor:
      ArgumentCaptor<ActivityResultCallback<Map<String, Boolean>>>

  private lateinit var gpsService: GPSServiceImpl

  @Before
  fun setup() {
    // Initialize mocked objects
    MockitoAnnotations.openMocks(this)

    mockActivity = mock(ComponentActivity::class.java)
    mockFusedLocationProviderClient = mock(FusedLocationProviderClient::class.java)

    // Mock the RegisterForActivityResult call
    doReturn(mockPermissionLauncher)
      .`when`(mockActivity)
      .registerForActivityResult(
        any<ActivityResultContracts.RequestMultiplePermissions>(),
        any<ActivityResultCallback<Map<String, Boolean>>>())

    // Mock the static LocationServices class
    mockStatic(LocationServices::class.java).use { mockedLocationServices ->
      // Setup the static LocationServices class
      mockedLocationServices.`when` <FusedLocationProviderClient> {
        LocationServices.getFusedLocationProviderClient(mockActivity)
      }.thenReturn(mockFusedLocationProviderClient)

      gpsService = GPSServiceImpl(mockActivity)

    }

    // Verify that registerForActivityResult was called and capture the callback
    verify(mockActivity)
      .registerForActivityResult(
        any<ActivityResultContracts.RequestMultiplePermissions>(),
        capture(permissionCallbackCaptor))
  }

  @Test
  fun `initial location access type should be NONE`() = runBlocking {

    assertEquals(ActivityCompat.checkSelfPermission(mockActivity, Manifest.permission.ACCESS_COARSE_LOCATION),
      PackageManager.PERMISSION_DENIED)

    assertEquals(ActivityCompat.checkSelfPermission(mockActivity, Manifest.permission.ACCESS_FINE_LOCATION),
      PackageManager.PERMISSION_DENIED)
  }

  /*
  @Test
  fun `requesting permissions should launch permission request`() {
    // When
    gpsService.askUserForLocationPermission()

    // Then
    verify(permissionLauncher)
        .launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION))
  }

  @Test
  fun `should update to PRECISE when fine location permission is granted`() = runBlocking {
    // Given
    val permissions =
        mapOf(
            Manifest.permission.ACCESS_FINE_LOCATION to true,
            Manifest.permission.ACCESS_COARSE_LOCATION to true)

    // Here we are "running" the callback we previously captured
    permissionCallbackCaptor.value.onActivityResult(permissions)

    // Then,check that the location granted switched to precise
    assertEquals(LocationAccessType.PRECISE, gpsService.locationGranted.first())
  }

  @Test
  fun `should update to APPROXIMATE when only coarse location is granted`() = runBlocking {
    // Given
    val permissions =
        mapOf(
            Manifest.permission.ACCESS_FINE_LOCATION to false,
            Manifest.permission.ACCESS_COARSE_LOCATION to true)

    // When
    permissionCallbackCaptor.value.onActivityResult(permissions)

    // Then
    assertEquals(LocationAccessType.APPROXIMATE, gpsService.locationGranted.first())
  }

  @Test
  fun `should update to NONE when no location is granted`() = runBlocking {
    // Given
    val permissions =
        mapOf(
            Manifest.permission.ACCESS_FINE_LOCATION to false,
            Manifest.permission.ACCESS_COARSE_LOCATION to false)

    // When
    permissionCallbackCaptor.value.onActivityResult(permissions)

    // Then
    assertEquals(LocationAccessType.DENIED, gpsService.locationGranted.first())
  }*/
}
