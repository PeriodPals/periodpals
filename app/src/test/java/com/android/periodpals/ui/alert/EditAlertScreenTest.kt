package com.android.periodpals.ui.alert

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import com.android.periodpals.model.alert.Alert
import com.android.periodpals.model.alert.AlertViewModel
import com.android.periodpals.model.alert.LIST_OF_PRODUCTS
import com.android.periodpals.model.alert.LIST_OF_URGENCIES
import com.android.periodpals.model.alert.Product
import com.android.periodpals.model.alert.Status
import com.android.periodpals.model.alert.Urgency
import com.android.periodpals.model.location.Location
import com.android.periodpals.model.location.LocationViewModel
import com.android.periodpals.resources.C.Tag
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
class EditAlertScreenTest {
  private lateinit var navigationActions: NavigationActions
  private lateinit var locationViewModel: LocationViewModel
  private lateinit var alert: Alert
  private lateinit var gpsService: GPSServiceImpl
  private val mockLocationFLow = MutableStateFlow(Location.DEFAULT_LOCATION)
  private lateinit var alertViewModel: AlertViewModel
  @get:Rule val composeTestRule = createComposeRule()

  companion object {
    private val PRODUCT = LIST_OF_PRODUCTS[1].textId // Pad
    private val URGENCY = LIST_OF_URGENCIES[1].textId // Medium
    private const val LOCATION = "Lausanne"
    private val LOCATION_SUGGESTION1 =
        Location(46.5218269, 6.6327025, "Lausanne, District de Lausanne")
    private val LOCATION_SUGGESTION2 = Location(46.2017559, 6.1466014, "Geneva, Switzerland")
    private val LOCATION_SUGGESTION3 = Location(46.1683026, 5.9059776, "Farges, Gex, Ain")
    private const val MESSAGE = "I need help finding a tampon"
    private const val DELETE_BUTTON_TEXT = "Delete"
    private const val SAVE_BUTTON_TEXT = "Save"
    private const val RESOLVE_BUTTON_TEXT = "Resolve"
  }

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    locationViewModel = mock(LocationViewModel::class.java)
    alert = mock(Alert::class.java)
    gpsService = mock(GPSServiceImpl::class.java)
    alertViewModel = mock(AlertViewModel::class.java)

    `when`(gpsService.location).thenReturn(mockLocationFLow)

    // Set up initial state for the alert object
    `when`(alert.id).thenReturn("1")
    `when`(alert.uid).thenReturn("12")
    `when`(alert.name).thenReturn("Jane Doe")
    `when`(alert.product).thenReturn(Product.TAMPON)
    `when`(alert.urgency).thenReturn(Urgency.HIGH)
    `when`(alert.message).thenReturn("hello")
    `when`(alert.location).thenReturn("19.4326,-99.1331,Mexico City")
    `when`(alert.createdAt).thenReturn("2024-11-28 00:23:00+00")
    `when`(alert.status).thenReturn(Status.CREATED)

    `when`(navigationActions.currentRoute()).thenReturn(Route.ALERT_LIST)
    `when`(locationViewModel.locationSuggestions)
        .thenReturn(
            MutableStateFlow(
                listOf(LOCATION_SUGGESTION1, LOCATION_SUGGESTION2, LOCATION_SUGGESTION3)))
    `when`(locationViewModel.query).thenReturn(MutableStateFlow(LOCATION_SUGGESTION1.name))
  }

  @Test
  fun allComponentsAreDisplayed() {
    composeTestRule.setContent {
      EditAlertScreen(alert, locationViewModel, gpsService, alertViewModel, navigationActions)
    }

    composeTestRule.onNodeWithTag(Tag.EditAlertScreen.SCREEN).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.TOP_BAR).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(TopAppBar.TITLE_TEXT)
        .assertIsDisplayed()
        .assertTextEquals("Edit Your Alert")
    composeTestRule.onNodeWithTag(TopAppBar.GO_BACK_BUTTON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.SETTINGS_BUTTON).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.EDIT_BUTTON).assertIsNotDisplayed()
    composeTestRule
        .onNodeWithTag(BottomNavigationMenu.BOTTOM_NAVIGATION_MENU)
        .assertIsNotDisplayed()

    composeTestRule
        .onNodeWithTag(Tag.AlertInputs.INSTRUCTION_TEXT)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(Tag.AlertInputs.PRODUCT_FIELD)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(Tag.AlertInputs.URGENCY_FIELD)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(Tag.AlertInputs.LOCATION_FIELD)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(Tag.AlertInputs.MESSAGE_FIELD)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(Tag.EditAlertScreen.DELETE_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
        .assertTextEquals(DELETE_BUTTON_TEXT)
    composeTestRule
        .onNodeWithTag(Tag.EditAlertScreen.SAVE_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
        .assertTextEquals(SAVE_BUTTON_TEXT)
    composeTestRule
        .onNodeWithTag(Tag.EditAlertScreen.RESOLVE_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
        .assertTextEquals(RESOLVE_BUTTON_TEXT)
  }

  @Test
  fun updateAlertSuccessful() {
    composeTestRule.setContent {
      EditAlertScreen(alert, locationViewModel, gpsService, alertViewModel, navigationActions)
    }

    composeTestRule.onNodeWithTag(Tag.AlertInputs.PRODUCT_FIELD).performScrollTo().performClick()
    composeTestRule.onNodeWithText(PRODUCT).performScrollTo().performClick()

    composeTestRule.onNodeWithTag(Tag.AlertInputs.URGENCY_FIELD).performScrollTo().performClick()
    composeTestRule.onNodeWithText(URGENCY).performScrollTo().performClick()

    composeTestRule
        .onNodeWithTag(Tag.AlertInputs.LOCATION_FIELD)
        .performScrollTo()
        .performTextClearance()
    composeTestRule
        .onNodeWithTag(Tag.AlertInputs.LOCATION_FIELD)
        .performScrollTo()
        .performTextInput(LOCATION)
    composeTestRule
        .onNodeWithTag(Tag.AlertInputs.DROPDOWN_ITEM + LOCATION_SUGGESTION1.name)
        .performScrollTo()
        .performClick()
    composeTestRule
        .onNodeWithTag(Tag.AlertInputs.LOCATION_FIELD)
        .performScrollTo()
        .assertTextContains(LOCATION_SUGGESTION1.name)

    composeTestRule
        .onNodeWithTag(Tag.AlertInputs.MESSAGE_FIELD)
        .performScrollTo()
        .performTextClearance()
    composeTestRule
        .onNodeWithTag(Tag.AlertInputs.MESSAGE_FIELD)
        .performScrollTo()
        .performTextInput(MESSAGE)

    composeTestRule.onNodeWithTag(Tag.EditAlertScreen.SAVE_BUTTON).performScrollTo().performClick()
    verify(navigationActions).navigateTo(Screen.ALERT_LIST)
  }

  @Test
  fun updateAlertUsingCurrentLocation() {
    composeTestRule.setContent {
      EditAlertScreen(alert, locationViewModel, gpsService, alertViewModel, navigationActions)
    }

    composeTestRule.onNodeWithTag(Tag.AlertInputs.PRODUCT_FIELD).performScrollTo().performClick()
    composeTestRule.onNodeWithText(PRODUCT).performScrollTo().performClick()

    composeTestRule.onNodeWithTag(Tag.AlertInputs.URGENCY_FIELD).performScrollTo().performClick()
    composeTestRule.onNodeWithText(URGENCY).performScrollTo().performClick()

    composeTestRule.onNodeWithTag(Tag.AlertInputs.LOCATION_FIELD).performScrollTo().performClick()
    composeTestRule
        .onNodeWithTag(Tag.AlertInputs.DROPDOWN_ITEM + Tag.AlertInputs.CURRENT_LOCATION)
        .performScrollTo()
        .performClick()

    composeTestRule
        .onNodeWithTag(Tag.AlertInputs.MESSAGE_FIELD)
        .performScrollTo()
        .performTextClearance()
    composeTestRule
        .onNodeWithTag(Tag.AlertInputs.MESSAGE_FIELD)
        .performScrollTo()
        .performTextInput(MESSAGE)

    composeTestRule.onNodeWithTag(Tag.EditAlertScreen.SAVE_BUTTON).performScrollTo().performClick()
    verify(navigationActions).navigateTo(Screen.ALERT_LIST)
  }

  @Test
  fun updateAlertEmptyLocation() {
    composeTestRule.setContent {
      EditAlertScreen(alert, locationViewModel, gpsService, alertViewModel, navigationActions)
    }

    composeTestRule
        .onNodeWithTag(Tag.AlertInputs.LOCATION_FIELD)
        .performScrollTo()
        .performTextClearance()
    composeTestRule
        .onNodeWithTag(Tag.AlertInputs.LOCATION_FIELD)
        .performScrollTo()
        .performTextInput(" ")

    composeTestRule
        .onNodeWithTag(Tag.EditAlertScreen.SAVE_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
        .performClick()

    verify(navigationActions).navigateTo(Screen.ALERT_LIST)
  }

  @Test
  fun updateAlertInvalidMessage() {
    composeTestRule.setContent {
      EditAlertScreen(alert, locationViewModel, gpsService, alertViewModel, navigationActions)
    }
    composeTestRule
        .onNodeWithTag(Tag.AlertInputs.MESSAGE_FIELD)
        .performScrollTo()
        .performTextClearance()
    composeTestRule
        .onNodeWithTag(Tag.AlertInputs.MESSAGE_FIELD)
        .performScrollTo()
        .performTextInput("")
    composeTestRule
        .onNodeWithTag(Tag.EditAlertScreen.SAVE_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
        .performClick()
    verify(navigationActions, never()).navigateTo(any<TopLevelDestination>())
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun deleteAlertSuccessfully() {
    composeTestRule.setContent {
      EditAlertScreen(alert, locationViewModel, gpsService, alertViewModel, navigationActions)
    }

    composeTestRule
        .onNodeWithTag(Tag.EditAlertScreen.DELETE_BUTTON)
        .performScrollTo()
        .performClick()
    verify(navigationActions).navigateTo(Screen.ALERT_LIST)
  }

  @Test
  fun resolveAlertSuccessfully() {
    composeTestRule.setContent {
      EditAlertScreen(alert, locationViewModel, gpsService, alertViewModel, navigationActions)
    }

    composeTestRule
        .onNodeWithTag(Tag.EditAlertScreen.RESOLVE_BUTTON)
        .performScrollTo()
        .performClick()
    verify(navigationActions).navigateTo(Screen.ALERT_LIST)
  }
}
