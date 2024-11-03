package com.android.periodpals.ui.map

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.android.periodpals.resources.C.Tag.MapScreen
import com.android.periodpals.resources.C.Tag.TopAppBar
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Route
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
    `when`(navigationActions.currentRoute()).thenReturn(Route.MAP)
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
  fun checkMapIsDisplayedWhenPermissionGranted() {
    composeTestRule.setContent {
      MapScreen(locationPermissionGranted = true, navigationActions = navigationActions)
    }
    composeTestRule.onNodeWithTag("MapView").assertExists()
  }

  @Test
  fun checkMapIsDisplayedWhenNoPermissions() {
    composeTestRule.setContent {
      MapScreen(locationPermissionGranted = false, navigationActions = navigationActions)
    }
    composeTestRule.onNodeWithTag("MapView").assertExists()
  }
}
