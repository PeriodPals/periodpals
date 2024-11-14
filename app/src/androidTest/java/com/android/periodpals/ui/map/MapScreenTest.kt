package com.android.periodpals.ui.map

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.android.periodpals.model.location.GPSLocation
import com.android.periodpals.resources.C
import com.android.periodpals.resources.C.Tag.MapScreen
import com.android.periodpals.resources.C.Tag.TopAppBar
import com.android.periodpals.services.GPSServiceImpl
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.whenever

class MapScreenTest {
  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var mockGpsService: GPSServiceImpl
  private lateinit var mockNavigationActions: NavigationActions

  private val mockLocationFLow = MutableStateFlow(GPSLocation.DEFAULT_LOCATION)

  @Before
  fun setup() {
    mockGpsService = mock(GPSServiceImpl::class.java)
    mockNavigationActions = mock(NavigationActions::class.java)

    whenever(mockGpsService.location).thenReturn(mockLocationFLow)

    `when`(mockNavigationActions.currentRoute()).thenReturn(Screen.MAP)
  }

  @Test
  fun allComponentsAreDisplayed() {
    composeTestRule.setContent { MapScreen(mockGpsService, mockNavigationActions) }

    composeTestRule.onNodeWithTag(MapScreen.SCREEN).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.TOP_BAR).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.TITLE_TEXT).assertIsDisplayed().assertTextEquals("Map")
    composeTestRule.onNodeWithTag(TopAppBar.GO_BACK_BUTTON).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.EDIT_BUTTON).assertIsNotDisplayed()

    composeTestRule.onNodeWithTag(MapScreen.MAP_VIEW_CONTAINER).assertIsDisplayed()
  }
}
