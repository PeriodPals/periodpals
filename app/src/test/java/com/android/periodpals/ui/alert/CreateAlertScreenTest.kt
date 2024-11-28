package com.android.periodpals.ui.alert

import android.util.Log
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import com.android.periodpals.model.alert.LIST_OF_PRODUCTS
import com.android.periodpals.model.alert.LIST_OF_URGENCIES
import com.android.periodpals.model.location.Location
import com.android.periodpals.model.location.LocationViewModel
import com.android.periodpals.resources.C
import com.android.periodpals.resources.C.Tag.AlertInputs
import com.android.periodpals.resources.C.Tag.BottomNavigationMenu
import com.android.periodpals.resources.C.Tag.TopAppBar
import com.android.periodpals.services.GPSServiceImpl
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Route
import com.android.periodpals.ui.navigation.Screen
import com.android.periodpals.ui.navigation.TopLevelDestination
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CreateAlertScreenTest {

  private lateinit var navigationActions: NavigationActions
  private lateinit var locationViewModel: LocationViewModel
  private lateinit var gpsService: GPSServiceImpl
  private val mockLocationFLow = MutableStateFlow(Location.DEFAULT_LOCATION)

  @get:Rule val composeTestRule = createComposeRule()

  companion object {
    private val PRODUCT = LIST_OF_PRODUCTS[0].textId // Tampon
    private val URGENCY = LIST_OF_URGENCIES[1].textId // Medium
    private const val LOCATION = "Lausanne"
    private val LOCATION_SUGGESTION1 =
        Location(46.5218269, 6.6327025, "Lausanne, District de Lausanne")
    private val LOCATION_SUGGESTION2 = Location(46.2017559, 6.1466014, "Geneva, Switzerland")
    private val LOCATION_SUGGESTION3 = Location(46.1683026, 5.9059776, "Farges, Gex, Ain")
    private const val MESSAGE = "I need help finding a tampon"
    private const val SUBMIT_BUTTON_TEXT = "Ask for Help"

    private const val NUM_ITEMS_WHEN_SUGGESTION = 4
    private const val NUM_ITEMS_WHEN_NO_SUGGESTION = 1
  }

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    locationViewModel = mock(LocationViewModel::class.java)
    gpsService = mock(GPSServiceImpl::class.java)

    `when`(gpsService.location).thenReturn(mockLocationFLow)

    `when`(navigationActions.currentRoute()).thenReturn(Route.ALERT)
  }

  @Test
  fun allComponentsAreDisplayed() {
    `when`(locationViewModel.locationSuggestions)
        .thenReturn(
            MutableStateFlow(
                listOf(LOCATION_SUGGESTION1, LOCATION_SUGGESTION2, LOCATION_SUGGESTION3)))
    `when`(locationViewModel.query).thenReturn(MutableStateFlow(LOCATION_SUGGESTION1.name))
    composeTestRule.setContent {
      CreateAlertScreen(locationViewModel, gpsService, navigationActions)
    }

    composeTestRule.onNodeWithTag(C.Tag.CreateAlertScreen.SCREEN).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.TOP_BAR).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(TopAppBar.TITLE_TEXT)
        .assertIsDisplayed()
        .assertTextEquals("Create Alert")
    composeTestRule.onNodeWithTag(TopAppBar.GO_BACK_BUTTON).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.SETTINGS_BUTTON).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.EDIT_BUTTON).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(BottomNavigationMenu.BOTTOM_NAVIGATION_MENU).assertIsDisplayed()

    composeTestRule
        .onNodeWithTag(AlertInputs.INSTRUCTION_TEXT)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule.onNodeWithTag(AlertInputs.PRODUCT_FIELD).performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag(AlertInputs.URGENCY_FIELD).performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag(AlertInputs.LOCATION_FIELD).performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag(AlertInputs.MESSAGE_FIELD).performScrollTo().assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(C.Tag.CreateAlertScreen.SUBMIT_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
        .assertTextEquals(SUBMIT_BUTTON_TEXT)
  }

  @Test
  fun createValidAlert() {
    `when`(locationViewModel.locationSuggestions)
        .thenReturn(
            MutableStateFlow(
                listOf(LOCATION_SUGGESTION1, LOCATION_SUGGESTION2, LOCATION_SUGGESTION3)))
    `when`(locationViewModel.query).thenReturn(MutableStateFlow(LOCATION_SUGGESTION1.name))
    composeTestRule.setContent {
      CreateAlertScreen(locationViewModel, gpsService, navigationActions)
    }

    composeTestRule.onNodeWithTag(AlertInputs.PRODUCT_FIELD).performScrollTo().performClick()
    composeTestRule.onNodeWithText(PRODUCT).performScrollTo().performClick()

    composeTestRule.onNodeWithTag(AlertInputs.URGENCY_FIELD).performScrollTo().performClick()
    composeTestRule.onNodeWithText(URGENCY).performScrollTo().performClick()

    composeTestRule
        .onNodeWithTag(AlertInputs.LOCATION_FIELD)
        .performScrollTo()
        .performTextInput(LOCATION)
    composeTestRule
        .onNodeWithTag(AlertInputs.DROPDOWN_ITEM + LOCATION_SUGGESTION1.name)
        .performScrollTo()
        .performClick()
    composeTestRule
        .onNodeWithTag(AlertInputs.LOCATION_FIELD)
        .performScrollTo()
        .assertTextContains(LOCATION_SUGGESTION1.name)
    composeTestRule
        .onNodeWithTag(AlertInputs.MESSAGE_FIELD)
        .performScrollTo()
        .performTextInput(MESSAGE)

    composeTestRule
        .onNodeWithTag(C.Tag.CreateAlertScreen.SUBMIT_BUTTON)
        .performScrollTo()
        .performClick()
    verify(navigationActions).navigateTo(Screen.ALERT_LIST)
  }

  @Test
  fun createValidAlertUsingCurrentLocation() {
    `when`(locationViewModel.query).thenReturn(MutableStateFlow(LOCATION_SUGGESTION1.name))
    `when`(locationViewModel.locationSuggestions).thenReturn(MutableStateFlow(emptyList()))
    composeTestRule.setContent {
      CreateAlertScreen(locationViewModel, gpsService, navigationActions)
    }

    composeTestRule.onNodeWithTag(AlertInputs.PRODUCT_FIELD).performScrollTo().performClick()
    composeTestRule.onNodeWithText(PRODUCT).performScrollTo().performClick()

    composeTestRule.onNodeWithTag(AlertInputs.URGENCY_FIELD).performScrollTo().performClick()
    composeTestRule.onNodeWithText(URGENCY).performScrollTo().performClick()

    composeTestRule
        .onNodeWithTag(AlertInputs.LOCATION_FIELD)
        .performScrollTo()
        .performTextInput(LOCATION)
    composeTestRule
        .onNodeWithTag(AlertInputs.DROPDOWN_ITEM + AlertInputs.CURRENT_LOCATION)
        .performScrollTo()
        .performClick()
    composeTestRule
        .onNodeWithTag(AlertInputs.LOCATION_FIELD)
        .performScrollTo()
        .assertTextContains(Location.CURRENT_LOCATION_NAME)
    composeTestRule
        .onNodeWithTag(AlertInputs.MESSAGE_FIELD)
        .performScrollTo()
        .performTextInput(MESSAGE)

    composeTestRule
        .onNodeWithTag(C.Tag.CreateAlertScreen.SUBMIT_BUTTON)
        .performScrollTo()
        .performClick()
    verify(navigationActions).navigateTo(Screen.ALERT_LIST)
  }

  @Test
  fun createInvalidAlertNoProduct() {
    `when`(locationViewModel.locationSuggestions)
        .thenReturn(
            MutableStateFlow(
                listOf(LOCATION_SUGGESTION1, LOCATION_SUGGESTION2, LOCATION_SUGGESTION3)))
    `when`(locationViewModel.query).thenReturn(MutableStateFlow(LOCATION_SUGGESTION1.name))
    composeTestRule.setContent {
      CreateAlertScreen(locationViewModel, gpsService, navigationActions)
    }

    composeTestRule.onNodeWithTag(AlertInputs.URGENCY_FIELD).performScrollTo().performClick()
    composeTestRule.onNodeWithText(URGENCY).performScrollTo().performClick()

    composeTestRule
        .onNodeWithTag(AlertInputs.LOCATION_FIELD)
        .performScrollTo()
        .performTextInput(LOCATION)
    composeTestRule
        .onNodeWithTag(AlertInputs.DROPDOWN_ITEM + LOCATION_SUGGESTION1.name)
        .performScrollTo()
        .performClick()
    composeTestRule
        .onNodeWithTag(AlertInputs.LOCATION_FIELD)
        .performScrollTo()
        .assertTextContains(LOCATION_SUGGESTION1.name)
    composeTestRule
        .onNodeWithTag(AlertInputs.MESSAGE_FIELD)
        .performScrollTo()
        .performTextInput(MESSAGE)

    composeTestRule
        .onNodeWithTag(C.Tag.CreateAlertScreen.SUBMIT_BUTTON)
        .performScrollTo()
        .performClick()
    verify(navigationActions, never()).navigateTo(any<TopLevelDestination>())
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun createInvalidAlertNoUrgencyLevel() {
    `when`(locationViewModel.locationSuggestions)
        .thenReturn(
            MutableStateFlow(
                listOf(LOCATION_SUGGESTION1, LOCATION_SUGGESTION2, LOCATION_SUGGESTION3)))
    `when`(locationViewModel.query).thenReturn(MutableStateFlow(LOCATION_SUGGESTION1.name))
    composeTestRule.setContent {
      CreateAlertScreen(locationViewModel, gpsService, navigationActions)
    }

    composeTestRule.onNodeWithTag(AlertInputs.PRODUCT_FIELD).performScrollTo().performClick()
    composeTestRule.onNodeWithText(PRODUCT).performScrollTo().performClick()

    composeTestRule
        .onNodeWithTag(AlertInputs.LOCATION_FIELD)
        .performScrollTo()
        .performTextInput(LOCATION)
    composeTestRule
        .onNodeWithTag(AlertInputs.DROPDOWN_ITEM + LOCATION_SUGGESTION1.name)
        .performScrollTo()
        .performClick()
    composeTestRule
        .onNodeWithTag(AlertInputs.LOCATION_FIELD)
        .performScrollTo()
        .assertTextContains(LOCATION_SUGGESTION1.name)
    composeTestRule
        .onNodeWithTag(AlertInputs.MESSAGE_FIELD)
        .performScrollTo()
        .performTextInput(MESSAGE)

    composeTestRule
        .onNodeWithTag(C.Tag.CreateAlertScreen.SUBMIT_BUTTON)
        .performScrollTo()
        .performClick()
    verify(navigationActions, never()).navigateTo(any<TopLevelDestination>())
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun createInvalidAlertNoLocation() {
    `when`(locationViewModel.locationSuggestions)
        .thenReturn(
            MutableStateFlow(
                listOf(LOCATION_SUGGESTION1, LOCATION_SUGGESTION2, LOCATION_SUGGESTION3)))
    `when`(locationViewModel.query).thenReturn(MutableStateFlow(LOCATION_SUGGESTION1.name))
    composeTestRule.setContent {
      CreateAlertScreen(locationViewModel, gpsService, navigationActions)
    }

    composeTestRule.onNodeWithTag(AlertInputs.PRODUCT_FIELD).performScrollTo().performClick()
    composeTestRule.onNodeWithText(PRODUCT).performScrollTo().performClick()

    composeTestRule.onNodeWithTag(AlertInputs.URGENCY_FIELD).performScrollTo().performClick()
    composeTestRule.onNodeWithText(URGENCY).performScrollTo().performClick()

    composeTestRule
        .onNodeWithTag(AlertInputs.MESSAGE_FIELD)
        .performScrollTo()
        .performTextInput(MESSAGE)

    composeTestRule
        .onNodeWithTag(C.Tag.CreateAlertScreen.SUBMIT_BUTTON)
        .performScrollTo()
        .performClick()
    verify(navigationActions, never()).navigateTo(any<TopLevelDestination>())
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun createInvalidAlertNoMessage() {
    `when`(locationViewModel.locationSuggestions)
        .thenReturn(
            MutableStateFlow(
                listOf(LOCATION_SUGGESTION1, LOCATION_SUGGESTION2, LOCATION_SUGGESTION3)))
    `when`(locationViewModel.query).thenReturn(MutableStateFlow(LOCATION_SUGGESTION1.name))
    composeTestRule.setContent {
      CreateAlertScreen(locationViewModel, gpsService, navigationActions)
    }

    composeTestRule.onNodeWithTag(AlertInputs.PRODUCT_FIELD).performScrollTo().performClick()
    composeTestRule.onNodeWithText(PRODUCT).performScrollTo().performClick()

    composeTestRule.onNodeWithTag(AlertInputs.URGENCY_FIELD).performScrollTo().performClick()
    composeTestRule.onNodeWithText(URGENCY).performScrollTo().performClick()

    composeTestRule
        .onNodeWithTag(AlertInputs.LOCATION_FIELD)
        .performScrollTo()
        .performTextInput(LOCATION)
    composeTestRule
        .onNodeWithTag(AlertInputs.DROPDOWN_ITEM + LOCATION_SUGGESTION1.name)
        .performScrollTo()
        .performClick()
    composeTestRule
        .onNodeWithTag(AlertInputs.LOCATION_FIELD)
        .performScrollTo()
        .assertTextContains(LOCATION_SUGGESTION1.name)

    composeTestRule
        .onNodeWithTag(C.Tag.CreateAlertScreen.SUBMIT_BUTTON)
        .performScrollTo()
        .performClick()
    verify(navigationActions, never()).navigateTo(any<TopLevelDestination>())
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun createInvalidAlertAllEmptyFields() {
    `when`(locationViewModel.locationSuggestions)
        .thenReturn(
            MutableStateFlow(
                listOf(LOCATION_SUGGESTION1, LOCATION_SUGGESTION2, LOCATION_SUGGESTION3)))
    `when`(locationViewModel.query).thenReturn(MutableStateFlow(LOCATION_SUGGESTION1.name))
    composeTestRule.setContent {
      CreateAlertScreen(locationViewModel, gpsService, navigationActions)
    }

    composeTestRule
        .onNodeWithTag(C.Tag.CreateAlertScreen.SUBMIT_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
        .performClick()
    verify(navigationActions, never()).navigateTo(any<TopLevelDestination>())
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun locationDropdownOnlyShowsCurrentLocationWhenNoSuggestion() {
    `when`(locationViewModel.query).thenReturn(MutableStateFlow(LOCATION_SUGGESTION1.name))
    `when`(locationViewModel.locationSuggestions).thenReturn(MutableStateFlow(emptyList()))
    composeTestRule.setContent {
      CreateAlertScreen(locationViewModel, gpsService, navigationActions)
    }

    Log.d("LocationViewModelTest", locationViewModel.locationSuggestions.value.toString())
    composeTestRule
        .onNodeWithTag(AlertInputs.LOCATION_FIELD)
        .performScrollTo()
        .performTextInput(LOCATION)
    composeTestRule
        .onAllNodesWithContentDescription(AlertInputs.DROPDOWN_ITEM)
        .assertCountEquals(NUM_ITEMS_WHEN_NO_SUGGESTION)
    composeTestRule
        .onNodeWithTag(AlertInputs.DROPDOWN_ITEM + AlertInputs.CURRENT_LOCATION)
        .assertExists()
  }

  @Test
  fun locationDropdownShowsSuggestionsWhenSuggestions() {
    `when`(locationViewModel.query).thenReturn(MutableStateFlow(LOCATION_SUGGESTION1.name))
    `when`(locationViewModel.locationSuggestions)
        .thenReturn(
            MutableStateFlow(
                listOf(LOCATION_SUGGESTION1, LOCATION_SUGGESTION2, LOCATION_SUGGESTION3)))
    composeTestRule.setContent {
      CreateAlertScreen(locationViewModel, gpsService, navigationActions)
    }

    composeTestRule
        .onNodeWithTag(AlertInputs.LOCATION_FIELD)
        .performScrollTo()
        .performTextInput(LOCATION)
    composeTestRule
        .onAllNodesWithContentDescription(AlertInputs.DROPDOWN_ITEM)
        .assertCountEquals(NUM_ITEMS_WHEN_SUGGESTION)
    composeTestRule
        .onNodeWithTag(AlertInputs.DROPDOWN_ITEM + LOCATION_SUGGESTION1.name)
        .performScrollTo()
        .assertExists()
    composeTestRule
        .onNodeWithTag(AlertInputs.DROPDOWN_ITEM + LOCATION_SUGGESTION2.name)
        .performScrollTo()
        .assertExists()
    composeTestRule
        .onNodeWithTag(AlertInputs.DROPDOWN_ITEM + LOCATION_SUGGESTION3.name)
        .performScrollTo()
        .assertExists()
    composeTestRule
        .onNodeWithTag(AlertInputs.DROPDOWN_ITEM + AlertInputs.CURRENT_LOCATION)
        .performScrollTo()
        .assertExists()
  }

  @Test
  fun locationDropdownShowsAtMostThreeLocationsPlusCurrentLocation() {
    `when`(locationViewModel.query).thenReturn(MutableStateFlow(LOCATION_SUGGESTION1.name))
    `when`(locationViewModel.locationSuggestions)
        .thenReturn(
            MutableStateFlow(
                listOf(
                    LOCATION_SUGGESTION1,
                    LOCATION_SUGGESTION2,
                    LOCATION_SUGGESTION3,
                    Location(46.1683026, 5.9059776, "Farges, Gex, Ain"),
                    Location(46.1683026, 5.9059776, "Farges, Gex, Ain"),
                )))
    composeTestRule.setContent {
      CreateAlertScreen(locationViewModel, gpsService, navigationActions)
    }

    composeTestRule
        .onNodeWithTag(AlertInputs.LOCATION_FIELD)
        .performScrollTo()
        .performTextInput(LOCATION)
    composeTestRule
        .onAllNodesWithContentDescription(AlertInputs.DROPDOWN_ITEM)
        .assertCountEquals(NUM_ITEMS_WHEN_SUGGESTION)
  }
}
