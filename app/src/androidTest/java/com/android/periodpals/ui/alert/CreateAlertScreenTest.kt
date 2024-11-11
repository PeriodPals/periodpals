package com.android.periodpals.ui.alert

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.periodpals.model.location.Location
import com.android.periodpals.model.location.LocationViewModel
import com.android.periodpals.resources.C.Tag.BottomNavigationMenu
import com.android.periodpals.resources.C.Tag.CreateAlertScreen
import com.android.periodpals.resources.C.Tag.TopAppBar
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

@RunWith(AndroidJUnit4::class)
class CreateAlertScreenTest {

  private lateinit var navigationActions: NavigationActions
  private lateinit var locationViewModel: LocationViewModel
  @get:Rule val composeTestRule = createComposeRule()

  companion object {
    private const val PRODUCT = "Pads"
    private const val URGENCY = "!! Medium"
    private const val LOCATION = "Lausanne"
    private val LOCATION_SUGGESTION1 =
        Location(46.5218269, 6.6327025, "Lausanne, District de Lausanne")
    private val LOCATION_SUGGESTION2 = Location(46.2017559, 6.1466014, "Geneva, Switzerland")
    private val LOCATION_SUGGESTION3 = Location(46.1683026, 5.9059776, "Farges, Gex, Ain")
    private const val MESSAGE = "I need help finding a tampon"
    private const val SUBMIT_BUTTON_TEXT = "Ask for Help"
  }

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    locationViewModel = mock(LocationViewModel::class.java)

    `when`(locationViewModel.locationSuggestions)
        .thenReturn(
            MutableStateFlow(
                listOf(LOCATION_SUGGESTION1, LOCATION_SUGGESTION2, LOCATION_SUGGESTION3)))
    `when`(locationViewModel.query).thenReturn(MutableStateFlow(LOCATION_SUGGESTION1.name))
    `when`(navigationActions.currentRoute()).thenReturn(Route.ALERT)

    composeTestRule.setContent { CreateAlertScreen(navigationActions, locationViewModel) }
  }

  @Test
  fun allComponentsAreDisplayed() {
    composeTestRule.onNodeWithTag(CreateAlertScreen.SCREEN).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CreateAlertScreen.INSTRUCTION_TEXT).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CreateAlertScreen.PRODUCT_FIELD).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CreateAlertScreen.URGENCY_FIELD).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CreateAlertScreen.LOCATION_FIELD).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CreateAlertScreen.MESSAGE_FIELD).assertIsDisplayed()
    composeTestRule.onNodeWithTag(BottomNavigationMenu.BOTTOM_NAVIGATION_MENU).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.TOP_BAR).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(TopAppBar.TITLE_TEXT)
        .assertIsDisplayed()
        .assertTextEquals("Create Alert")
    composeTestRule.onNodeWithTag(TopAppBar.GO_BACK_BUTTON).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.EDIT_BUTTON).assertIsNotDisplayed()
    composeTestRule
        .onNodeWithTag(CreateAlertScreen.SUBMIT_BUTTON)
        .assertIsDisplayed()
        .assertTextEquals(SUBMIT_BUTTON_TEXT)
  }

  @Test
  fun createValidAlert() {
    composeTestRule.onNodeWithTag(CreateAlertScreen.PRODUCT_FIELD).performClick()
    composeTestRule.onNodeWithText(PRODUCT).performClick()

    composeTestRule.onNodeWithTag(CreateAlertScreen.URGENCY_FIELD).performClick()
    composeTestRule.onNodeWithText(URGENCY).performClick()

    composeTestRule.onNodeWithTag(CreateAlertScreen.LOCATION_FIELD).performTextInput(LOCATION)
    composeTestRule
        .onNodeWithTag(CreateAlertScreen.DROPDOWN_ITEM + LOCATION_SUGGESTION1.name)
        .performClick()
    composeTestRule
        .onNodeWithTag(CreateAlertScreen.LOCATION_FIELD)
        .assertTextContains(LOCATION_SUGGESTION1.name)

    composeTestRule.onNodeWithTag(CreateAlertScreen.MESSAGE_FIELD).performTextInput(MESSAGE)

    composeTestRule.onNodeWithTag(CreateAlertScreen.SUBMIT_BUTTON).performClick()
    verify(navigationActions).navigateTo(Screen.ALERT_LIST)
  }

  @Test
  fun createInvalidAlertNoProduct() {

    composeTestRule.onNodeWithTag(CreateAlertScreen.URGENCY_FIELD).performClick()
    composeTestRule.onNodeWithText(URGENCY).performClick()

    composeTestRule.onNodeWithTag(CreateAlertScreen.LOCATION_FIELD).performTextInput(LOCATION)
    composeTestRule
        .onNodeWithTag(CreateAlertScreen.DROPDOWN_ITEM + LOCATION_SUGGESTION1.name)
        .performClick()
    composeTestRule
        .onNodeWithTag(CreateAlertScreen.LOCATION_FIELD)
        .assertTextContains(LOCATION_SUGGESTION1.name)
    composeTestRule.onNodeWithTag(CreateAlertScreen.MESSAGE_FIELD).performTextInput(MESSAGE)

    composeTestRule.onNodeWithTag(CreateAlertScreen.SUBMIT_BUTTON).performClick()
    verify(navigationActions, never()).navigateTo(any<TopLevelDestination>())
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun createInvalidAlertNoUrgencyLevel() {

    composeTestRule.onNodeWithTag(CreateAlertScreen.PRODUCT_FIELD).performClick()
    composeTestRule.onNodeWithText(PRODUCT).performClick()

    composeTestRule.onNodeWithTag(CreateAlertScreen.LOCATION_FIELD).performTextInput(LOCATION)
    composeTestRule
        .onNodeWithTag(CreateAlertScreen.DROPDOWN_ITEM + LOCATION_SUGGESTION1.name)
        .performClick()
    composeTestRule
        .onNodeWithTag(CreateAlertScreen.LOCATION_FIELD)
        .assertTextContains(LOCATION_SUGGESTION1.name)
    composeTestRule.onNodeWithTag(CreateAlertScreen.MESSAGE_FIELD).performTextInput(MESSAGE)

    composeTestRule.onNodeWithTag(CreateAlertScreen.SUBMIT_BUTTON).performClick()
    verify(navigationActions, never()).navigateTo(any<TopLevelDestination>())
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun createInvalidAlertNoLocation() {

    composeTestRule.onNodeWithTag(CreateAlertScreen.PRODUCT_FIELD).performClick()
    composeTestRule.onNodeWithText(PRODUCT).performClick()

    composeTestRule.onNodeWithTag(CreateAlertScreen.URGENCY_FIELD).performClick()
    composeTestRule.onNodeWithText(URGENCY).performClick()

    composeTestRule.onNodeWithTag(CreateAlertScreen.MESSAGE_FIELD).performTextInput(MESSAGE)

    composeTestRule.onNodeWithTag(CreateAlertScreen.SUBMIT_BUTTON).performClick()
    verify(navigationActions, never()).navigateTo(any<TopLevelDestination>())
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun createInvalidAlertNoMessage() {

    composeTestRule.onNodeWithTag(CreateAlertScreen.PRODUCT_FIELD).performClick()
    composeTestRule.onNodeWithText(PRODUCT).performClick()

    composeTestRule.onNodeWithTag(CreateAlertScreen.URGENCY_FIELD).performClick()
    composeTestRule.onNodeWithText(URGENCY).performClick()

    composeTestRule.onNodeWithTag(CreateAlertScreen.LOCATION_FIELD).performTextInput(LOCATION)
    composeTestRule
        .onNodeWithTag(CreateAlertScreen.DROPDOWN_ITEM + LOCATION_SUGGESTION1.name)
        .performClick()
    composeTestRule
        .onNodeWithTag(CreateAlertScreen.LOCATION_FIELD)
        .assertTextContains(LOCATION_SUGGESTION1.name)

    composeTestRule.onNodeWithTag(CreateAlertScreen.SUBMIT_BUTTON).performClick()
    verify(navigationActions, never()).navigateTo(any<TopLevelDestination>())
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun createInvalidAlertAllEmptyFields() {
    composeTestRule
        .onNodeWithTag(CreateAlertScreen.SUBMIT_BUTTON)
        .assertIsDisplayed()
        .performClick()
    verify(navigationActions, never()).navigateTo(any<TopLevelDestination>())
    verify(navigationActions, never()).navigateTo(any<String>())
  }
}
