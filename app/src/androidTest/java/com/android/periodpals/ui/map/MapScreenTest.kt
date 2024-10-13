package com.android.periodpals.ui.map

// UI test for MapViewContainer.kt
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import org.junit.Rule
import org.junit.Test

class MapScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testMapViewIsDisplayedWhenPermissionGranted() {
    // Set up the content with MapViewContainer and location permission granted
    composeTestRule.setContent {
      MapScreen(locationPermissionGranted = true, modifier = Modifier.testTag("MapView"))
    }

    // Verify that the map is displayed when permission is granted
    composeTestRule.onNodeWithTag("MapView").assertIsDisplayed()
  }

  @Test
  fun testMapViewIsDisplayedWhenPermissionNotGranted() {
    // Set up the content with MapViewContainer and location permission not granted
    composeTestRule.setContent {
      MapScreen(locationPermissionGranted = false, modifier = Modifier.testTag("MapView"))
    }

    // Verify that the map is still displayed even if permission is not granted
    composeTestRule.onNodeWithTag("MapView").assertIsDisplayed()
  }
}
