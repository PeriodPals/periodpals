package com.android.periodpals.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.periodpals.ui.alert.AlertListScreen
import com.android.periodpals.ui.alert.NoAlertDialog
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Route
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@RunWith(AndroidJUnit4::class)
class AlertListScreenTest {

  private lateinit var navigationActions: NavigationActions
  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(Route.ALERT)
  }

  @Test
  fun sharedComponentsCorrectlyDisplayed() {
    composeTestRule.setContent { AlertListScreen(navigationActions) }

    composeTestRule.onNodeWithTag("alertListScreen").assertExists()
    composeTestRule.onNodeWithTag("tabRowAlert").assertIsDisplayed()
    composeTestRule.onNodeWithTag("myAlertsTab").assertIsDisplayed()
    composeTestRule.onNodeWithTag("palsAlertsTab").assertIsDisplayed()
  }

  @Test
  fun myAlertsTabIsSelectedByDefault() {
    composeTestRule.setContent { AlertListScreen(navigationActions) }

    composeTestRule.onNodeWithTag("myAlertsTab").assertIsSelected()
    composeTestRule.onNodeWithTag("palsAlertsTab").assertIsNotSelected()
  }

  @Test
  fun myAlertsTabContentIsCorrect() {
    composeTestRule.setContent { AlertListScreen(navigationActions) }

    composeTestRule.onNodeWithTag("alertItem").assertIsDisplayed()
  }

  @Test
  fun palsAlertsTabContentIsCorrect() {
    composeTestRule.setContent { AlertListScreen(navigationActions) }

    composeTestRule.onNodeWithTag("palsAlertsTab").performClick()

    composeTestRule.onNodeWithTag("noAlertsCard").assertIsDisplayed()
    composeTestRule.onNodeWithTag("noAlertsCardText").assertIsDisplayed()
  }

  @Test
  fun clickingOnAlertItemDoesNothing() {
    composeTestRule.setContent { AlertListScreen(navigationActions) }

    composeTestRule.onNodeWithTag("alertItem").assertIsDisplayed()
    composeTestRule.onNodeWithTag("alertItem").performClick()
    // check that it did nothing
    composeTestRule.onNodeWithTag("myAlertsTab").assertIsSelected()
    composeTestRule.onNodeWithTag("palsAlertsTab").assertIsNotSelected()
    composeTestRule.onNodeWithTag("alertItem").assertIsDisplayed()
  }

  @Test
  fun clickingOnNoAlertDialogDoesNothing() {
    composeTestRule.setContent { AlertListScreen(navigationActions) }

    composeTestRule.onNodeWithTag("palsAlertsTab").performClick()
    composeTestRule.onNodeWithTag("myAlertsTab").assertIsNotSelected()
    composeTestRule.onNodeWithTag("palsAlertsTab").assertIsSelected()

    composeTestRule.onNodeWithTag("noAlertsCard").assertIsDisplayed()
    composeTestRule.onNodeWithTag("noAlertsCard").performClick()

    composeTestRule.onNodeWithTag("myAlertsTab").assertIsNotSelected()
    composeTestRule.onNodeWithTag("palsAlertsTab").assertIsSelected()
    composeTestRule.onNodeWithTag("noAlertsCard").assertIsDisplayed()
    composeTestRule.onNodeWithTag("noAlertsCardText").assertIsDisplayed()
  }

  @Test
  fun switchingTabWorks() {
    composeTestRule.setContent { AlertListScreen(navigationActions) }

    composeTestRule.onNodeWithTag("myAlertsTab").assertIsSelected()
    composeTestRule.onNodeWithTag("palsAlertsTab").assertIsNotSelected()

    composeTestRule.onNodeWithTag("palsAlertsTab").performClick()

    composeTestRule.onNodeWithTag("palsAlertsTab").assertIsSelected()
    composeTestRule.onNodeWithTag("myAlertsTab").assertIsNotSelected()

    composeTestRule.onNodeWithTag("myAlertsTab").performClick()

    composeTestRule.onNodeWithTag("myAlertsTab").assertIsSelected()
    composeTestRule.onNodeWithTag("palsAlertsTab").assertIsNotSelected()
  }

  @Test
  fun testNoAlertDialogContent() {
    composeTestRule.setContent { NoAlertDialog() }

    composeTestRule.onNodeWithTag("noAlertsCard").assertIsDisplayed()
    composeTestRule.onNodeWithTag("noAlertsIcon").assertIsDisplayed()
    composeTestRule.onNodeWithTag("noAlertsCardText").assertExists()
    composeTestRule
      .onNodeWithTag("noAlertsCardText")
      .assertTextEquals("No alerts here for the moment...")
  }
}
