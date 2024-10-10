package com.android.periodpals.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.periodpals.ui.alertsList.AlertListScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AlertListScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun allElementsAreDisplayed() {
    composeTestRule.setContent { AlertListScreen() }

    composeTestRule.onNodeWithTag("alertListScreen").assertExists()
    composeTestRule.onNodeWithTag("alertListTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("tabRowAlert").assertIsDisplayed()
    composeTestRule.onNodeWithTag("myAlertsTab").assertIsDisplayed()
    composeTestRule.onNodeWithTag("palsAlertsTab").assertIsDisplayed()
    composeTestRule.onNodeWithTag("alertItem").assertIsDisplayed()
  }

  @Test
  fun defaultTabIsMyAlerts() {
    composeTestRule.setContent { AlertListScreen() }

    composeTestRule.onNodeWithTag("myAlertsTab").assertIsSelected()
    composeTestRule.onNodeWithTag("palsAlertsTab").assertIsNotSelected()
  }

  @Test
  fun switchingTabsWorks() {
    composeTestRule.setContent { AlertListScreen() }

    // Switch to Pals Alerts tab
    composeTestRule.onNodeWithTag("palsAlertsTab").performClick()
    composeTestRule.onNodeWithTag("palsAlertsTab").assertIsSelected()
    composeTestRule.onNodeWithTag("myAlertsTab").assertIsNotSelected()

    // Verify Pals Alerts content is displayed
    composeTestRule.onNodeWithTag("noAlertsCard").assertIsDisplayed()
    composeTestRule.onNodeWithTag("noAlertsPals").assertIsDisplayed()

    // Switch back to My Alerts tab
    composeTestRule.onNodeWithTag("myAlertsTab").performClick()
    composeTestRule.onNodeWithTag("myAlertsTab").assertIsSelected()
    composeTestRule.onNodeWithTag("palsAlertsTab").assertIsNotSelected()

    // Verify My Alerts content is displayed
    composeTestRule.onNodeWithTag("alertItem").assertIsDisplayed()
  }

  @Test
  fun palsAlertsContentIsCorrect() {
    composeTestRule.setContent { AlertListScreen() }

    composeTestRule.onNodeWithTag("palsAlertsTab").performClick()

    composeTestRule.onNodeWithTag("noAlertsCard").assertIsDisplayed()
    composeTestRule.onNodeWithTag("noAlertsIcon").assertIsDisplayed()
    composeTestRule.onNodeWithTag("noAlertsPals").assertIsDisplayed()
  }
}
