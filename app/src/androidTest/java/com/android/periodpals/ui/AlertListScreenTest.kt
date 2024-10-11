package com.android.periodpals.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.periodpals.ui.alertsList.AlertListScreen
import com.android.periodpals.ui.alertsList.NoAlertDialog
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AlertListScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun titleAndNavigationElementsCorrectlyDisplayed() {
    composeTestRule.setContent { AlertListScreen() }

    composeTestRule.onNodeWithTag("alertListScreen").assertExists()
    composeTestRule.onNodeWithTag("alertListTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("tabRowAlert").assertIsDisplayed()
    composeTestRule.onNodeWithTag("myAlertsTab").assertIsDisplayed()
    composeTestRule.onNodeWithTag("palsAlertsTab").assertIsDisplayed()
  }

  @Test
  fun palsAlertsContentIsCorrect() {
    composeTestRule.setContent { AlertListScreen() }

    composeTestRule.onNodeWithTag("palsAlertsTab").performClick()

    composeTestRule.onNodeWithTag("noAlertsCard").assertIsDisplayed()
    composeTestRule.onNodeWithTag("noAlertsCardText").assertIsDisplayed()
  }

  @Test
  fun myAlertsContentIsCorrect() {
    composeTestRule.setContent { AlertListScreen() }

    composeTestRule.onNodeWithTag("alertItem").assertIsDisplayed()
  }

  @Test
  fun clickingOnAlertItemDoesNothing() {
    composeTestRule.setContent { AlertListScreen() }

    // Check that the default tab is My Alerts tab
    composeTestRule.onNodeWithTag("myAlertsTab").assertIsSelected()
    composeTestRule.onNodeWithTag("palsAlertsTab").assertIsNotSelected()

    // Check that the alert item is displayed
    composeTestRule.onNodeWithTag("alertItem").assertIsDisplayed()

    // Click on the card and verify that nothing changes
    composeTestRule.onNodeWithTag("alertItem").performClick()

    // Check that the default tab is My Alerts tab
    composeTestRule.onNodeWithTag("myAlertsTab").assertIsSelected()
    composeTestRule.onNodeWithTag("palsAlertsTab").assertIsNotSelected()

    // Check that the alert item is displayed
    composeTestRule.onNodeWithTag("alertItem").assertIsDisplayed()
  }

  @Test
  fun clickingOnNoAlertDialogDoesNothing() {
    composeTestRule.setContent { AlertListScreen() }

    // Check that the default tab is My Alerts tab
    composeTestRule.onNodeWithTag("myAlertsTab").assertIsSelected()
    composeTestRule.onNodeWithTag("palsAlertsTab").assertIsNotSelected()

    // Switch to Pals Alerts screen
    composeTestRule.onNodeWithTag("palsAlertsTab").performClick()

    // Check that the Pals Alerts tab is selected and my Alerts is not selected
    composeTestRule.onNodeWithTag("myAlertsTab").assertIsNotSelected()
    composeTestRule.onNodeWithTag("palsAlertsTab").assertIsSelected()

    // Check that the dialog is displayed
    composeTestRule.onNodeWithTag("noAlertsCard").assertIsDisplayed()
    composeTestRule.onNodeWithTag("noAlertsCardText").assertIsDisplayed()

    // Click on the card
    composeTestRule.onNodeWithTag("noAlertsCard").assertIsDisplayed()

    // Check that nothing changed
    // Check that the Pals Alerts tab is selected and my Alerts is not selected
    composeTestRule.onNodeWithTag("myAlertsTab").assertIsNotSelected()
    composeTestRule.onNodeWithTag("palsAlertsTab").assertIsSelected()

    // Check that the dialog is displayed
    composeTestRule.onNodeWithTag("noAlertsCard").assertIsDisplayed()
    composeTestRule.onNodeWithTag("noAlertsCardText").assertIsDisplayed()
  }

  @Test
  fun switchingTabWorks() {
    composeTestRule.setContent { AlertListScreen() }

    // Explicitly select My Alerts tab
    composeTestRule.onNodeWithTag("myAlertsTab").performClick()

    // Check that the default tab is My Alerts tab
    composeTestRule.onNodeWithTag("myAlertsTab").assertIsSelected()
    composeTestRule.onNodeWithTag("palsAlertsTab").assertIsNotSelected()

    // Check that My Alert content is correctly displayed
    composeTestRule.onNodeWithTag("alertItem").assertIsDisplayed()

    // Switch to Pals Alerts tab
    composeTestRule.onNodeWithTag("palsAlertsTab").performClick()
    composeTestRule.onNodeWithTag("palsAlertsTab").assertIsSelected()
    composeTestRule.onNodeWithTag("myAlertsTab").assertIsNotSelected()

    // Verify Pals Alerts content is displayed
    composeTestRule.onNodeWithTag("noAlertsCard").assertIsDisplayed()
    composeTestRule.onNodeWithTag("noAlertsCardText").assertIsDisplayed()

    // Switch back to My Alerts tab
    composeTestRule.onNodeWithTag("myAlertsTab").performClick()
    composeTestRule.onNodeWithTag("myAlertsTab").assertIsSelected()
    composeTestRule.onNodeWithTag("palsAlertsTab").assertIsNotSelected()

    // Verify My Alerts content is displayed
    composeTestRule.onNodeWithTag("alertItem").assertIsDisplayed()
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
