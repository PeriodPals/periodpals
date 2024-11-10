package com.android.periodpals.ui.alert

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.periodpals.resources.C.Tag.BottomNavigationMenu
import com.android.periodpals.resources.C.Tag.CreateAlertScreen
import com.android.periodpals.resources.C.Tag.TopAppBar
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Route
import com.android.periodpals.ui.navigation.Screen
import com.android.periodpals.ui.navigation.TopLevelDestination
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
  @get:Rule val composeTestRule = createComposeRule()

  companion object {
    private const val PRODUCT = "Pads"
    private const val URGENCY = "!! Medium"
    private const val LOCATION = "Lausanne"
    private const val DISPLAYED_LOCATION = "Lausanne, District de Lausanne, Vaud, Schweiz/Suisse/Svizzera/Svizra"
    private const val MESSAGE = "I need help finding a tampon"
    private const val SUBMIT_BUTTON_TEXT = "Ask for Help"
  }

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)

    `when`(navigationActions.currentRoute()).thenReturn(Route.ALERT)

    composeTestRule.setContent { CreateAlertScreen(navigationActions) }
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
    composeTestRule.onNodeWithText(LOCATION).performClick()
    composeTestRule.onNodeWithTag(CreateAlertScreen.MESSAGE_FIELD).performTextInput(MESSAGE)

    composeTestRule.onNodeWithTag(CreateAlertScreen.SUBMIT_BUTTON).performClick()
    verify(navigationActions).navigateTo(Screen.ALERT_LIST)
  }

  @Test
  fun createInvalidAlertNoProduct() {

    composeTestRule.onNodeWithTag(CreateAlertScreen.URGENCY_FIELD).performClick()
    composeTestRule.onNodeWithText(URGENCY).performClick()

    composeTestRule.onNodeWithTag(CreateAlertScreen.LOCATION_FIELD).performTextInput(LOCATION)
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
