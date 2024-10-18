package com.android.periodpals.ui.map

// UI test for MapViewContainer.kt

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.compose.rememberNavController
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.theme.PeriodPalsAppTheme
import org.junit.Rule
import org.junit.Test

class MapScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testMapScreenWithPermissionGranted() {
    composeTestRule.setContent {
      PeriodPalsAppTheme { MapScreen(locationPermissionGranted = true, navigationActions = NavigationActions(
        rememberNavController()
      )) }
    }
    // Verify that the map is displayed when permission is granted
    composeTestRule.onNodeWithTag("MapView").assertExists()
  }

  @Test
  fun testMapScreenWithoutPermission() {
    composeTestRule.setContent {
      PeriodPalsAppTheme { MapScreen(locationPermissionGranted = false, navigationActions = NavigationActions(
        rememberNavController()
      )) }
    }
    // Verify that the map is still displayed even if permission is not granted
    composeTestRule.onNodeWithTag("MapView").assertExists()
  }
}
