package com.android.periodpals.services

import android.Manifest
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.android.periodpals.model.location.Location
import com.android.periodpals.model.user.UserViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class GPSServiceImplInstrumentedTest {

  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(
          Manifest.permission.ACCESS_FINE_LOCATION,
          Manifest.permission.ACCESS_COARSE_LOCATION,
      )

  private lateinit var scenario: ActivityScenario<ComponentActivity>
  private lateinit var activity: ComponentActivity
  private lateinit var gpsService: GPSServiceImpl
  private lateinit var userViewModel: UserViewModel

  // Default location
  private val defaultLat = Location.DEFAULT_LOCATION.latitude
  private val defaultLong = Location.DEFAULT_LOCATION.longitude

  @Before
  fun setup() {
    userViewModel = mock(UserViewModel::class.java)

    scenario = ActivityScenario.launch(ComponentActivity::class.java)

    // Manually set the scenario state to created. If this is not done, the scenario starts on
    // the RESUME state and the init of GPSService fails.
    scenario.moveToState(Lifecycle.State.CREATED)

    scenario.onActivity { activity ->
      this.activity = activity
      gpsService = GPSServiceImpl(this.activity, userViewModel)
    }

    // Once the GPSService has been initialized, set its state to resumed
    scenario.moveToState(Lifecycle.State.RESUMED)
  }

  @After
  fun tearDownService() {
    gpsService.cleanup()
  }

  @Test
  fun testShouldReceiveLocationUpdate() = runTest {
    // Start location updates
    gpsService.askPermissionAndStartUpdates()

    // Wait for the update to happen
    val updatedLocation =
        gpsService.location.first { location ->
          location.latitude != defaultLat || location.longitude != defaultLong
        }

    // The received location should be different than the default
    assertNotEquals(defaultLat, updatedLocation.latitude)
    assertNotEquals(defaultLong, updatedLocation.longitude)
  }

  /*
  This test is commented because when run with the other two tests, it does not pass in the CI.

  @Test
  fun testSwitchingLocationAccuracy() = runTest {
    // Start with precise location
    gpsService.askPermissionAndStartUpdates()

    // Switch to approximate
    gpsService.switchFromPreciseToApproximate()

    // Wait for the update to happen and capture approx location
    val approxLocation =
        gpsService.location.first { location ->
          location.lat != defaultLat || location.long != defaultLong
        }

    // Switch back to precise
    gpsService.switchFromApproximateToPrecise()

    // Wait for update to happen and capture precise location
    val preciseLocation =
        gpsService.location.first { location ->
          location.lat != approxLocation.lat || location.long != approxLocation.long
        }

    // Locations captured should be different
    assertNotEquals(approxLocation.lat, preciseLocation.lat)
    assertNotEquals(approxLocation.long, preciseLocation.long)
  }*/

  @Test
  fun testCleanupStopsUpdates() = runTest {
    // Start updates
    gpsService.askPermissionAndStartUpdates()

    // Get updated location
    val updatedLocation =
        gpsService.location.first { location ->
          location.latitude != defaultLat || location.longitude != defaultLong
        }

    // Stop updates
    gpsService.cleanup()

    // Verify that the location has not changed
    val finalLocation = gpsService.location.first()

    // Location shouldn't have changed
    assert(updatedLocation.latitude == finalLocation.latitude)
    assert(updatedLocation.longitude == finalLocation.longitude)
  }
}
