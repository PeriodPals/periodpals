package com.android.periodpals.ui.map

// UI test for MapViewContainer.kt

import android.Manifest
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.compose.rememberNavController
import com.android.periodpals.resources.C.Tag.MapScreen
import com.android.periodpals.resources.C.Tag.TopAppBar
import com.android.periodpals.services.GPSServiceImpl
import com.android.periodpals.ui.navigation.NavigationActions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.capture
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.verify

class MapScreenTest {

  private lateinit var navigationActions: NavigationActions
  @get:Rule val composeTestRule = createComposeRule()

  @Mock private lateinit var activity: ComponentActivity // Mock the "screen"

  // Mock the permissionLauncher (this is what handles the system dialog, etc.)
  @Mock private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

  // Used to capture the ActivityResultCallback
  @Captor
  private lateinit var permissionCallbackCaptor:
      ArgumentCaptor<ActivityResultCallback<Map<String, Boolean>>>

  // The location service
  private lateinit var locationService: GPSServiceImpl

  @Before
  fun setup() {
    // Instantiates the objects annotated with Mockito annotations
    MockitoAnnotations.openMocks(this)

    // Mock the RegisterForActivityResult call
    doReturn(permissionLauncher)
        .`when`(activity)
        .registerForActivityResult(
            any<ActivityResultContracts.RequestMultiplePermissions>(),
            any<ActivityResultCallback<Map<String, Boolean>>>())

    locationService = GPSServiceImpl(activity)

    // Verify registerForActivityResult is called and capture the callback
    verify(activity)
        .registerForActivityResult(
            any<ActivityResultContracts.RequestMultiplePermissions>(),
            capture(permissionCallbackCaptor))
  }

  @Test
  fun allComponentsAreDisplayed() {
    composeTestRule.setContent {
      MapScreen(locationService, navigationActions = NavigationActions(rememberNavController()))
    }
    composeTestRule.onNodeWithTag(MapScreen.SCREEN).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.TOP_BAR).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.TITLE_TEXT).assertIsDisplayed().assertTextEquals("Map")
    composeTestRule.onNodeWithTag(TopAppBar.GO_BACK_BUTTON).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.EDIT_BUTTON).assertIsNotDisplayed()
  }

  @Test
  fun testMapScreenWithApproximateLocationPermissionGranted() {
    val permissions =
        mapOf(
            Manifest.permission.ACCESS_FINE_LOCATION to false,
            Manifest.permission.ACCESS_COARSE_LOCATION to true)

    // Simulate the permission grant
    permissionCallbackCaptor.value.onActivityResult(permissions)

    composeTestRule.setContent {
      MapScreen(locationService, navigationActions = NavigationActions(rememberNavController()))
    }

    // Verify that the map is displayed when approximate permission is granted
    composeTestRule.onNodeWithTag("MapView").assertExists()
  }

  @Test
  fun testMapScreenWithPreciseLocationPermission() {
    val permissions =
        mapOf(
            Manifest.permission.ACCESS_FINE_LOCATION to true,
            Manifest.permission.ACCESS_COARSE_LOCATION to true)

    permissionCallbackCaptor.value.onActivityResult(permissions)

    composeTestRule.setContent {
      MapScreen(locationService, navigationActions = NavigationActions(rememberNavController()))
    }
    // Verify that the map is displayed when precise permission is granted
    composeTestRule.onNodeWithTag("MapView").assertExists()
  }

  @Test
  fun testMapScreenWithoutPermission() {
    val permissions =
        mapOf(
            Manifest.permission.ACCESS_FINE_LOCATION to false,
            Manifest.permission.ACCESS_COARSE_LOCATION to false)

    permissionCallbackCaptor.value.onActivityResult(permissions)

    composeTestRule.setContent {
      MapScreen(locationService, navigationActions = NavigationActions(rememberNavController()))
    }

    // Verify that the map is still displayed even if no permission is granted
    composeTestRule.onNodeWithTag("MapView").assertExists()
  }
}
