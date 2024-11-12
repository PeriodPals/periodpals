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

    `when`(navigationActions.currentRoute()).thenReturn(Route.ALERT)
  }

  @Test
  fun allComponentsAreDisplayed() {
    `when`(locationViewModel.locationSuggestions)
        .thenReturn(
            MutableStateFlow(
                listOf(LOCATION_SUGGESTION1, LOCATION_SUGGESTION2, LOCATION_SUGGESTION3)))
    `when`(locationViewModel.query).thenReturn(MutableStateFlow(LOCATION_SUGGESTION1.name))
    composeTestRule.setContent { CreateAlertScreen(navigationActions, locationViewModel) }

    composeTestRule.onNodeWithTag(CreateAlertScreen.SCREEN).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CreateAlertScreen.INSTRUCTION_TEXT).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CreateAlertScreen.PRODUCT_FIELD).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CreateAlertScreen.URGENCY_FIELD).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CreateAlertScreen.LOCATION_FIELD).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CreateAlertScreen.GPS_ICON).assertIsDisplayed()
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
    `when`(locationViewModel.locationSuggestions)
        .thenReturn(
            MutableStateFlow(
                listOf(LOCATION_SUGGESTION1, LOCATION_SUGGESTION2, LOCATION_SUGGESTION3)))
    `when`(locationViewModel.query).thenReturn(MutableStateFlow(LOCATION_SUGGESTION1.name))
    composeTestRule.setContent { CreateAlertScreen(navigationActions, locationViewModel) }

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
    `when`(locationViewModel.locationSuggestions)
        .thenReturn(
            MutableStateFlow(
                listOf(LOCATION_SUGGESTION1, LOCATION_SUGGESTION2, LOCATION_SUGGESTION3)))
    `when`(locationViewModel.query).thenReturn(MutableStateFlow(LOCATION_SUGGESTION1.name))
    composeTestRule.setContent { CreateAlertScreen(navigationActions, locationViewModel) }

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
    `when`(locationViewModel.locationSuggestions)
        .thenReturn(
            MutableStateFlow(
                listOf(LOCATION_SUGGESTION1, LOCATION_SUGGESTION2, LOCATION_SUGGESTION3)))
    `when`(locationViewModel.query).thenReturn(MutableStateFlow(LOCATION_SUGGESTION1.name))
    composeTestRule.setContent { CreateAlertScreen(navigationActions, locationViewModel) }

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
    `when`(locationViewModel.locationSuggestions)
        .thenReturn(
            MutableStateFlow(
                listOf(LOCATION_SUGGESTION1, LOCATION_SUGGESTION2, LOCATION_SUGGESTION3)))
    `when`(locationViewModel.query).thenReturn(MutableStateFlow(LOCATION_SUGGESTION1.name))
    composeTestRule.setContent { CreateAlertScreen(navigationActions, locationViewModel) }

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
    `when`(locationViewModel.locationSuggestions)
        .thenReturn(
            MutableStateFlow(
                listOf(LOCATION_SUGGESTION1, LOCATION_SUGGESTION2, LOCATION_SUGGESTION3)))
    `when`(locationViewModel.query).thenReturn(MutableStateFlow(LOCATION_SUGGESTION1.name))
    composeTestRule.setContent { CreateAlertScreen(navigationActions, locationViewModel) }

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
    `when`(locationViewModel.locationSuggestions)
        .thenReturn(
            MutableStateFlow(
                listOf(LOCATION_SUGGESTION1, LOCATION_SUGGESTION2, LOCATION_SUGGESTION3)))
    `when`(locationViewModel.query).thenReturn(MutableStateFlow(LOCATION_SUGGESTION1.name))
    composeTestRule.setContent { CreateAlertScreen(navigationActions, locationViewModel) }

    composeTestRule
        .onNodeWithTag(CreateAlertScreen.SUBMIT_BUTTON)
        .assertIsDisplayed()
        .performClick()
    verify(navigationActions, never()).navigateTo(any<TopLevelDestination>())
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun locationDropdownDoesNotShowItemsWhenNoSuggestions() {
    `when`(locationViewModel.query).thenReturn(MutableStateFlow(LOCATION_SUGGESTION1.name))
    `when`(locationViewModel.locationSuggestions).thenReturn(MutableStateFlow(emptyList()))
    composeTestRule.setContent { CreateAlertScreen(navigationActions, locationViewModel) }

    Log.d("LocationViewModelTest", locationViewModel.locationSuggestions.value.toString())
    composeTestRule.onNodeWithTag(CreateAlertScreen.LOCATION_FIELD).performTextInput(LOCATION)
    composeTestRule
        .onAllNodesWithContentDescription(CreateAlertScreen.DROPDOWN_ITEM)
        .assertCountEquals(0)
  }

  @Test
  fun locationDropdownShowsSuggestionsWhenSuggestions() {
    `when`(locationViewModel.query).thenReturn(MutableStateFlow(LOCATION_SUGGESTION1.name))
    `when`(locationViewModel.locationSuggestions)
        .thenReturn(
            MutableStateFlow(
                listOf(LOCATION_SUGGESTION1, LOCATION_SUGGESTION2, LOCATION_SUGGESTION3)))
    composeTestRule.setContent { CreateAlertScreen(navigationActions, locationViewModel) }

    composeTestRule.onNodeWithTag(CreateAlertScreen.LOCATION_FIELD).performTextInput(LOCATION)
    composeTestRule
        .onAllNodesWithContentDescription(CreateAlertScreen.DROPDOWN_ITEM)
        .assertCountEquals(3)
    composeTestRule
        .onNodeWithTag(CreateAlertScreen.DROPDOWN_ITEM + LOCATION_SUGGESTION1.name)
        .assertExists()
    composeTestRule
        .onNodeWithTag(CreateAlertScreen.DROPDOWN_ITEM + LOCATION_SUGGESTION2.name)
        .assertExists()
    composeTestRule
        .onNodeWithTag(CreateAlertScreen.DROPDOWN_ITEM + LOCATION_SUGGESTION3.name)
        .assertExists()
  }

  @Test
  fun locationDropdownDoesNotShowMoreThanThreeSuggestions() {
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
    composeTestRule.setContent { CreateAlertScreen(navigationActions, locationViewModel) }

    composeTestRule.onNodeWithTag(CreateAlertScreen.LOCATION_FIELD).performTextInput(LOCATION)
    composeTestRule
        .onAllNodesWithContentDescription(CreateAlertScreen.DROPDOWN_ITEM)
        .assertCountEquals(3)
  }
}
