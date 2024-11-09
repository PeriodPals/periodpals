package com.android.periodpals.services

import android.Manifest
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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

  @Mock private lateinit var activity: ComponentActivity // Mock the "screen"

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
    assertEquals(LocationAccessType.NONE, gpsService.locationGrantedType.first())
  }

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

    // When: simulate permition grant
    // Here we are "running" the callback we previously captured with the "artificial"
    // permission we just set
    permissionCallbackCaptor.value.onActivityResult(permissions)

    // Then
    // Check that the callback set the StateFlow for the type of permission to PRECISE
    assertEquals(LocationAccessType.PRECISE, gpsService.locationGrantedType.first())
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

    // then
    assertEquals(LocationAccessType.APPROXIMATE, gpsService.locationGrantedType.first())
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
    assertEquals(LocationAccessType.NONE, gpsService.locationGrantedType.first())
  }
}
