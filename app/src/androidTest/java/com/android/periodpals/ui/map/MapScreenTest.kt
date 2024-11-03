package com.android.periodpals.ui.map

// UI test for MapViewContainer.kt

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.android.periodpals.resources.C.Tag.MapScreen
import com.android.periodpals.resources.C.Tag.TopAppBar
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Route
import com.android.periodpals.ui.theme.PeriodPalsAppTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class MapScreenTest {

  private lateinit var navigationActions: NavigationActions
  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)

    `when`(navigationActions.currentRoute()).thenReturn(Route.ALERT)
  }

  @Test
  fun allComponentsAreDisplayed() {
    composeTestRule.setContent {
      MapScreen(locationPermissionGranted = true, navigationActions = navigationActions)
    }
    composeTestRule.onNodeWithTag(MapScreen.SCREEN).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.TOP_BAR).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.TITLE_TEXT).assertIsDisplayed().assertTextEquals("Map")
    composeTestRule.onNodeWithTag(TopAppBar.GO_BACK_BUTTON).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.EDIT_BUTTON).assertIsNotDisplayed()
  }

  @Test
  fun testMapScreenWithPermissionGranted() {
    composeTestRule.setContent {
      PeriodPalsAppTheme {
        MapScreen(locationPermissionGranted = true, navigationActions = navigationActions)
      }
    }
    // Verify that the map is displayed when permission is granted
    composeTestRule.onNodeWithTag("MapView").assertExists()
  }

  @Test
  fun testMapScreenWithoutPermission() {
    composeTestRule.setContent {
      PeriodPalsAppTheme {
        MapScreen(locationPermissionGranted = false, navigationActions = navigationActions)
      }
    }
    // Verify that the map is still displayed even if permission is not granted
    composeTestRule.onNodeWithTag("MapView").assertExists()
  }
}
