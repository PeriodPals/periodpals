package com.android.periodpals.ui.alert

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.periodpals.resources.C.Tag.AlertListsScreen
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Route
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@RunWith(AndroidJUnit4::class)
class AlertListsScreenTest {

  private lateinit var navigationActions: NavigationActions
  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(Route.ALERT_LIST)
  }

  @Test
  fun sharedComponentsCorrectlyDisplayed() {
    composeTestRule.setContent { AlertListsScreen(navigationActions) }

    composeTestRule.onNodeWithTag(AlertListsScreen.SCREEN).assertExists()
    composeTestRule.onNodeWithTag(AlertListsScreen.TAB_ROW).assertIsDisplayed()
    composeTestRule.onNodeWithTag(AlertListsScreen.MY_ALERTS_TAB).assertIsDisplayed()
    composeTestRule.onNodeWithTag(AlertListsScreen.PALS_ALERTS_TAB).assertIsDisplayed()
  }

  @Test
  fun myAlertsTabIsSelectedByDefault() {
    composeTestRule.setContent { AlertListsScreen(navigationActions) }

    composeTestRule.onNodeWithTag(AlertListsScreen.MY_ALERTS_TAB).assertIsSelected()
    composeTestRule.onNodeWithTag(AlertListsScreen.PALS_ALERTS_TAB).assertIsNotSelected()
  }

  @Test
  fun myAlertsTabContentIsCorrect() {
    composeTestRule.setContent { AlertListsScreen(navigationActions) }

    composeTestRule.onNodeWithTag(AlertListsScreen.ALERT).assertIsDisplayed()
  }

  @Test
  fun palsAlertsTabContentIsCorrect() {
    composeTestRule.setContent { AlertListsScreen(navigationActions) }

    composeTestRule.onNodeWithTag(AlertListsScreen.PALS_ALERTS_TAB).performClick()

    composeTestRule.onNodeWithTag(AlertListsScreen.NO_ALERTS_CARD).assertIsDisplayed()
    composeTestRule.onNodeWithTag(AlertListsScreen.NO_ALERTS_TEXT).assertIsDisplayed()
  }

  @Test
  fun clickingOnAlertItemDoesNothing() {
    composeTestRule.setContent { AlertListsScreen(navigationActions) }

    composeTestRule.onNodeWithTag(AlertListsScreen.ALERT).assertIsDisplayed()
    composeTestRule.onNodeWithTag(AlertListsScreen.ALERT).performClick()
    // check that it did nothing
    composeTestRule.onNodeWithTag(AlertListsScreen.MY_ALERTS_TAB).assertIsSelected()
    composeTestRule.onNodeWithTag(AlertListsScreen.PALS_ALERTS_TAB).assertIsNotSelected()
    composeTestRule.onNodeWithTag(AlertListsScreen.ALERT).assertIsDisplayed()
  }

  @Test
  fun clickingOnNoAlertDialogDoesNothing() {
    composeTestRule.setContent { AlertListsScreen(navigationActions) }

    composeTestRule.onNodeWithTag(AlertListsScreen.PALS_ALERTS_TAB).performClick()
    composeTestRule.onNodeWithTag(AlertListsScreen.MY_ALERTS_TAB).assertIsNotSelected()
    composeTestRule.onNodeWithTag(AlertListsScreen.PALS_ALERTS_TAB).assertIsSelected()

    composeTestRule.onNodeWithTag(AlertListsScreen.NO_ALERTS_CARD).assertIsDisplayed()
    composeTestRule.onNodeWithTag(AlertListsScreen.NO_ALERTS_CARD).performClick()

    composeTestRule.onNodeWithTag(AlertListsScreen.MY_ALERTS_TAB).assertIsNotSelected()
    composeTestRule.onNodeWithTag(AlertListsScreen.PALS_ALERTS_TAB).assertIsSelected()
    composeTestRule.onNodeWithTag(AlertListsScreen.NO_ALERTS_CARD).assertIsDisplayed()
    composeTestRule.onNodeWithTag(AlertListsScreen.NO_ALERTS_TEXT).assertIsDisplayed()
  }

  @Test
  fun switchingTabWorks() {
    composeTestRule.setContent { AlertListsScreen(navigationActions) }

    composeTestRule.onNodeWithTag(AlertListsScreen.MY_ALERTS_TAB).assertIsSelected()
    composeTestRule.onNodeWithTag(AlertListsScreen.PALS_ALERTS_TAB).assertIsNotSelected()

    composeTestRule.onNodeWithTag(AlertListsScreen.PALS_ALERTS_TAB).performClick()

    composeTestRule.onNodeWithTag(AlertListsScreen.PALS_ALERTS_TAB).assertIsSelected()
    composeTestRule.onNodeWithTag(AlertListsScreen.MY_ALERTS_TAB).assertIsNotSelected()

    composeTestRule.onNodeWithTag(AlertListsScreen.MY_ALERTS_TAB).performClick()

    composeTestRule.onNodeWithTag(AlertListsScreen.MY_ALERTS_TAB).assertIsSelected()
    composeTestRule.onNodeWithTag(AlertListsScreen.PALS_ALERTS_TAB).assertIsNotSelected()
  }

  @Test
  fun testNoAlertDialogContent() {
    composeTestRule.setContent { NoAlertDialog() }

    composeTestRule.onNodeWithTag(AlertListsScreen.NO_ALERTS_CARD).assertIsDisplayed()
    composeTestRule.onNodeWithTag(AlertListsScreen.NO_ALERTS_ICON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(AlertListsScreen.NO_ALERTS_TEXT).assertExists()
    composeTestRule
        .onNodeWithTag(AlertListsScreen.NO_ALERTS_TEXT)
        .assertTextEquals("No alerts here for the moment...")
  }
}
