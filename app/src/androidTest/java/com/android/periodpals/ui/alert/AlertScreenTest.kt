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

    composeTestRule.onNodeWithTag("alertInstruction").assertIsDisplayed()
    composeTestRule.onNodeWithTag("alertProduct").assertIsDisplayed()
    composeTestRule.onNodeWithTag("alertUrgency").assertIsDisplayed()
    composeTestRule.onNodeWithTag("alertLocation").assertIsDisplayed()
    composeTestRule.onNodeWithTag("alertMessage").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
    composeTestRule.onNodeWithTag("topBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("goBackButton").assertIsNotDisplayed()
    composeTestRule
      .onNodeWithTag("alertSubmit")
      .assertIsDisplayed()
      .assertTextEquals("Ask for Help")
  }

  @Test
  fun createValidAlert() {

    composeTestRule.onNodeWithTag("alertProduct").performClick()
    composeTestRule.onNodeWithText("Pads").performClick()

    composeTestRule.onNodeWithTag("alertUrgency").performClick()
    composeTestRule.onNodeWithText("!! Medium").performClick()

    composeTestRule.onNodeWithTag("alertLocation").performTextInput("Rolex")
    composeTestRule.onNodeWithTag("alertMessage").performTextInput("I need help finding a tampon")

    composeTestRule.onNodeWithTag("alertSubmit").performClick()
    verify(navigationActions).navigateTo(Screen.ALERT_LIST)
  }

  @Test
  fun createInvalidAlertNoProduct() {

    composeTestRule.onNodeWithTag("alertUrgency").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("!! Medium").performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("alertLocation").performTextInput("Rolex")
    composeTestRule.onNodeWithTag("alertMessage").performTextInput("I need help finding a tampon")

    composeTestRule.onNodeWithTag("alertSubmit").performClick()
    verify(navigationActions, never()).navigateTo(any<TopLevelDestination>())
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun createInvalidAlertNoUrgencyLevel() {

    composeTestRule.onNodeWithTag("alertProduct").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("Pads").performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("alertLocation").performTextInput("Rolex")
    composeTestRule.onNodeWithTag("alertMessage").performTextInput("I need help finding a tampon")

    composeTestRule.onNodeWithTag("alertSubmit").performClick()
    verify(navigationActions, never()).navigateTo(any<TopLevelDestination>())
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun createInvalidAlertNoLocation() {

    composeTestRule.onNodeWithTag("alertProduct").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("Pads").performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("alertUrgency").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("!! Medium").performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("alertMessage").performTextInput("I need help finding a tampon")

    composeTestRule.onNodeWithTag("alertSubmit").performClick()
    verify(navigationActions, never()).navigateTo(any<TopLevelDestination>())
    verify(navigationActions, never()).navigateTo(any<String>())
  }
}
