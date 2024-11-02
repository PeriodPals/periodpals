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

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)

    `when`(navigationActions.currentRoute()).thenReturn(Route.ALERT)

    composeTestRule.setContent { CreateAlertScreen(navigationActions) }
  }

  @Test
  fun allComponentsAreDisplayed() {

    composeTestRule.onNodeWithTag(CreateAlertScreen.INSTRUCTION_TEXT).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CreateAlertScreen.PRODUCT_FIELD).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CreateAlertScreen.URGENCY_FIELD).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CreateAlertScreen.LOCATION_FIELD).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CreateAlertScreen.MESSAGE_FIELD).assertIsDisplayed()
    composeTestRule.onNodeWithTag(BottomNavigationMenu.BOTTOM_NAVIGATION_MENU).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.TOP_BAR).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.GO_BACK_BUTTON).assertIsNotDisplayed()
    composeTestRule
        .onNodeWithTag(CreateAlertScreen.SUBMIT_BUTTON)
        .assertIsDisplayed()
        .assertTextEquals("Ask for Help")
  }

  @Test
  fun createValidAlert() {

    composeTestRule.onNodeWithTag(CreateAlertScreen.PRODUCT_FIELD).performClick()
    composeTestRule.onNodeWithText("Pads").performClick()

    composeTestRule.onNodeWithTag(CreateAlertScreen.URGENCY_FIELD).performClick()
    composeTestRule.onNodeWithText("!! Medium").performClick()

    composeTestRule.onNodeWithTag(CreateAlertScreen.LOCATION_FIELD).performTextInput("Rolex")
    composeTestRule
        .onNodeWithTag(CreateAlertScreen.MESSAGE_FIELD)
        .performTextInput("I need help finding a tampon")

    composeTestRule.onNodeWithTag(CreateAlertScreen.SUBMIT_BUTTON).performClick()
    verify(navigationActions).navigateTo(Screen.ALERT_LIST)
  }

  @Test
  fun createInvalidAlertNoProduct() {

    composeTestRule.onNodeWithTag(CreateAlertScreen.URGENCY_FIELD).performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("!! Medium").performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag(CreateAlertScreen.LOCATION_FIELD).performTextInput("Rolex")
    composeTestRule
        .onNodeWithTag(CreateAlertScreen.MESSAGE_FIELD)
        .performTextInput("I need help finding a tampon")

    composeTestRule.onNodeWithTag(CreateAlertScreen.SUBMIT_BUTTON).performClick()
    verify(navigationActions, never()).navigateTo(any<TopLevelDestination>())
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun createInvalidAlertNoUrgencyLevel() {

    composeTestRule.onNodeWithTag(CreateAlertScreen.PRODUCT_FIELD).performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("Pads").performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag(CreateAlertScreen.LOCATION_FIELD).performTextInput("Rolex")
    composeTestRule
        .onNodeWithTag(CreateAlertScreen.MESSAGE_FIELD)
        .performTextInput("I need help finding a tampon")

    composeTestRule.onNodeWithTag(CreateAlertScreen.SUBMIT_BUTTON).performClick()
    verify(navigationActions, never()).navigateTo(any<TopLevelDestination>())
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun createInvalidAlertNoLocation() {

    composeTestRule.onNodeWithTag(CreateAlertScreen.PRODUCT_FIELD).performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("Pads").performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag(CreateAlertScreen.URGENCY_FIELD).performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("!! Medium").performClick()
    composeTestRule.waitForIdle()

    composeTestRule
        .onNodeWithTag(CreateAlertScreen.MESSAGE_FIELD)
        .performTextInput("I need help finding a tampon")

    composeTestRule.onNodeWithTag(CreateAlertScreen.SUBMIT_BUTTON).performClick()
    verify(navigationActions, never()).navigateTo(any<TopLevelDestination>())
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun createInvalidAlertNoMessage() {
    // Leave message empty
    composeTestRule.onNodeWithTag(CreateAlertScreen.PRODUCT_FIELD).performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("Pads").performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag(CreateAlertScreen.URGENCY_FIELD).performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("!! Medium").performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag(CreateAlertScreen.LOCATION_FIELD).performTextInput("Rolex")

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
