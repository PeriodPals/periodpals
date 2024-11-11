package com.android.periodpals.services

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.capture
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class GPSServiceImplTest {

  @Mock private lateinit var activity: ComponentActivity

  // Mock the permissionLauncher (this is what handles the system dialog, etc.)
  @Mock private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

  @Captor
  private lateinit var permissionCallbackCaptor:
      ArgumentCaptor<ActivityResultCallback<Map<String, Boolean>>>

  private lateinit var gpsService: GPSServiceImpl

  @Before
  fun setup() {
    // Mock the RegisterForActivityResult call
    doReturn(permissionLauncher)
        .`when`(activity)
        .registerForActivityResult(
            any<ActivityResultContracts.RequestMultiplePermissions>(),
            any<ActivityResultCallback<Map<String, Boolean>>>())

    // Create the actual (not mocked) location service instance
    gpsService = GPSServiceImpl(activity)

    // And then we verify that upon creating the location service,
    // registerForActivityResult was called. We also capture the callback.
    verify(activity)
        .registerForActivityResult(
            any<ActivityResultContracts.RequestMultiplePermissions>(),
            capture(permissionCallbackCaptor))
  }

  @Test
  fun `initial location access type should be NONE`() = runBlocking {

    assertEquals(ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION),
      PackageManager.PERMISSION_DENIED)

    assertEquals(ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION),
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
