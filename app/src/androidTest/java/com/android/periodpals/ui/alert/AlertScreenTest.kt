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
import com.android.periodpals.resources.C
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
class AlertScreenTest {

  private lateinit var navigationActions: NavigationActions
  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)

    `when`(navigationActions.currentRoute()).thenReturn(Route.ALERT)

    composeTestRule.setContent { AlertScreen(navigationActions) }
  }

  @Test
  fun allComponentsAreDisplayed() {

    composeTestRule.onNodeWithTag(C.Tag.AlertScreen.INSTRUCTION_TEXT).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.AlertScreen.PRODUCT_FIELD).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.AlertScreen.URGENCY_FIELD).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.AlertScreen.LOCATION_FIELD).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.AlertScreen.MESSAGE_FIELD).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(C.Tag.BottomNavigationMenu.BOTTOM_NAVIGATION_MENU)
        .assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.TopBar.TOP_BAR).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.TopBar.GO_BACK_BUTTON).assertIsNotDisplayed()
    composeTestRule
        .onNodeWithTag(C.Tag.AlertScreen.SUBMIT_BUTTON)
        .assertIsDisplayed()
        .assertTextEquals("Ask for Help")
  }

  @Test
  fun createValidAlert() {

    composeTestRule.onNodeWithTag(C.Tag.AlertScreen.PRODUCT_FIELD).performClick()
    composeTestRule.onNodeWithText("Pads").performClick()

    composeTestRule.onNodeWithTag(C.Tag.AlertScreen.URGENCY_FIELD).performClick()
    composeTestRule.onNodeWithText("!! Medium").performClick()

    composeTestRule.onNodeWithTag(C.Tag.AlertScreen.LOCATION_FIELD).performTextInput("Rolex")
    composeTestRule
        .onNodeWithTag(C.Tag.AlertScreen.MESSAGE_FIELD)
        .performTextInput("I need help finding a tampon")

    composeTestRule.onNodeWithTag(C.Tag.AlertScreen.SUBMIT_BUTTON).performClick()
    verify(navigationActions).navigateTo(Screen.ALERT_LIST)
  }

  @Test
  fun createInvalidAlertNoProduct() {

    composeTestRule.onNodeWithTag(C.Tag.AlertScreen.URGENCY_FIELD).performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("!! Medium").performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag(C.Tag.AlertScreen.LOCATION_FIELD).performTextInput("Rolex")
    composeTestRule
        .onNodeWithTag(C.Tag.AlertScreen.MESSAGE_FIELD)
        .performTextInput("I need help finding a tampon")

    composeTestRule.onNodeWithTag(C.Tag.AlertScreen.SUBMIT_BUTTON).performClick()
    verify(navigationActions, never()).navigateTo(any<TopLevelDestination>())
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun createInvalidAlertNoUrgencyLevel() {

    composeTestRule.onNodeWithTag(C.Tag.AlertScreen.PRODUCT_FIELD).performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("Pads").performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag(C.Tag.AlertScreen.LOCATION_FIELD).performTextInput("Rolex")
    composeTestRule
        .onNodeWithTag(C.Tag.AlertScreen.MESSAGE_FIELD)
        .performTextInput("I need help finding a tampon")

    composeTestRule.onNodeWithTag(C.Tag.AlertScreen.SUBMIT_BUTTON).performClick()
    verify(navigationActions, never()).navigateTo(any<TopLevelDestination>())
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun createInvalidAlertNoLocation() {

    composeTestRule.onNodeWithTag(C.Tag.AlertScreen.PRODUCT_FIELD).performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("Pads").performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag(C.Tag.AlertScreen.URGENCY_FIELD).performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("!! Medium").performClick()
    composeTestRule.waitForIdle()

    composeTestRule
        .onNodeWithTag(C.Tag.AlertScreen.MESSAGE_FIELD)
        .performTextInput("I need help finding a tampon")

    composeTestRule.onNodeWithTag(C.Tag.AlertScreen.SUBMIT_BUTTON).performClick()
    verify(navigationActions, never()).navigateTo(any<TopLevelDestination>())
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun createInvalidAlertNoMessage() {
    // Leave message empty
    composeTestRule.onNodeWithTag(C.Tag.AlertScreen.PRODUCT_FIELD).performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("Pads").performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag(C.Tag.AlertScreen.URGENCY_FIELD).performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("!! Medium").performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag(C.Tag.AlertScreen.LOCATION_FIELD).performTextInput("Rolex")

    composeTestRule.onNodeWithTag(C.Tag.AlertScreen.SUBMIT_BUTTON).performClick()
    verify(navigationActions, never()).navigateTo(any<TopLevelDestination>())
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun createInvalidAlertAllEmptyFields() {
    composeTestRule
        .onNodeWithTag(C.Tag.AlertScreen.SUBMIT_BUTTON)
        .assertIsDisplayed()
        .performClick()
    verify(navigationActions, never()).navigateTo(any<TopLevelDestination>())
    verify(navigationActions, never()).navigateTo(any<String>())
  }
}
