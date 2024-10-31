package com.android.periodpals.ui.alert

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.periodpals.resources.C
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
    `when`(navigationActions.currentRoute()).thenReturn(Route.ALERT_LIST)
  }

  @Test
  fun sharedComponentsCorrectlyDisplayed() {
    composeTestRule.setContent { AlertListScreen(navigationActions) }

    composeTestRule.onNodeWithTag(C.Tag.AlertListScreen.SCREEN).assertExists()
    composeTestRule.onNodeWithTag(C.Tag.AlertListScreen.TAB_ROW).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.AlertListScreen.MY_ALERTS_TAB).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.AlertListScreen.PALS_ALERTS_TAB).assertIsDisplayed()
  }

  @Test
  fun myAlertsTabIsSelectedByDefault() {
    composeTestRule.setContent { AlertListScreen(navigationActions) }

    composeTestRule.onNodeWithTag(C.Tag.AlertListScreen.MY_ALERTS_TAB).assertIsSelected()
    composeTestRule.onNodeWithTag(C.Tag.AlertListScreen.PALS_ALERTS_TAB).assertIsNotSelected()
  }

  @Test
  fun myAlertsTabContentIsCorrect() {
    composeTestRule.setContent { AlertListScreen(navigationActions) }

    composeTestRule.onNodeWithTag(C.Tag.AlertListScreen.ALERT).assertIsDisplayed()
  }

  @Test
  fun palsAlertsTabContentIsCorrect() {
    composeTestRule.setContent { AlertListScreen(navigationActions) }

    composeTestRule.onNodeWithTag(C.Tag.AlertListScreen.PALS_ALERTS_TAB).performClick()

    composeTestRule.onNodeWithTag(C.Tag.AlertListScreen.NO_ALERTS_CARD).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.AlertListScreen.NO_ALERTS_TEXT).assertIsDisplayed()
  }

  @Test
  fun clickingOnAlertItemDoesNothing() {
    composeTestRule.setContent { AlertListScreen(navigationActions) }

    composeTestRule.onNodeWithTag(C.Tag.AlertListScreen.ALERT).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.AlertListScreen.ALERT).performClick()
    // check that it did nothing
    composeTestRule.onNodeWithTag(C.Tag.AlertListScreen.MY_ALERTS_TAB).assertIsSelected()
    composeTestRule.onNodeWithTag(C.Tag.AlertListScreen.PALS_ALERTS_TAB).assertIsNotSelected()
    composeTestRule.onNodeWithTag(C.Tag.AlertListScreen.ALERT).assertIsDisplayed()
  }

  @Test
  fun clickingOnNoAlertDialogDoesNothing() {
    composeTestRule.setContent { AlertListScreen(navigationActions) }

    composeTestRule.onNodeWithTag(C.Tag.AlertListScreen.PALS_ALERTS_TAB).performClick()
    composeTestRule.onNodeWithTag(C.Tag.AlertListScreen.MY_ALERTS_TAB).assertIsNotSelected()
    composeTestRule.onNodeWithTag(C.Tag.AlertListScreen.PALS_ALERTS_TAB).assertIsSelected()

    composeTestRule.onNodeWithTag(C.Tag.AlertListScreen.NO_ALERTS_CARD).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.AlertListScreen.NO_ALERTS_CARD).performClick()

    composeTestRule.onNodeWithTag(C.Tag.AlertListScreen.MY_ALERTS_TAB).assertIsNotSelected()
    composeTestRule.onNodeWithTag(C.Tag.AlertListScreen.PALS_ALERTS_TAB).assertIsSelected()
    composeTestRule.onNodeWithTag(C.Tag.AlertListScreen.NO_ALERTS_CARD).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.AlertListScreen.NO_ALERTS_TEXT).assertIsDisplayed()
  }

  @Test
  fun switchingTabWorks() {
    composeTestRule.setContent { AlertListScreen(navigationActions) }

    composeTestRule.onNodeWithTag(C.Tag.AlertListScreen.MY_ALERTS_TAB).assertIsSelected()
    composeTestRule.onNodeWithTag(C.Tag.AlertListScreen.PALS_ALERTS_TAB).assertIsNotSelected()

    composeTestRule.onNodeWithTag(C.Tag.AlertListScreen.PALS_ALERTS_TAB).performClick()

    composeTestRule.onNodeWithTag(C.Tag.AlertListScreen.PALS_ALERTS_TAB).assertIsSelected()
    composeTestRule.onNodeWithTag(C.Tag.AlertListScreen.MY_ALERTS_TAB).assertIsNotSelected()

    composeTestRule.onNodeWithTag(C.Tag.AlertListScreen.MY_ALERTS_TAB).performClick()

    composeTestRule.onNodeWithTag(C.Tag.AlertListScreen.MY_ALERTS_TAB).assertIsSelected()
    composeTestRule.onNodeWithTag(C.Tag.AlertListScreen.PALS_ALERTS_TAB).assertIsNotSelected()
  }

  @Test
  fun testNoAlertDialogContent() {
    composeTestRule.setContent { NoAlertDialog() }

    composeTestRule.onNodeWithTag(C.Tag.AlertListScreen.NO_ALERTS_CARD).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.AlertListScreen.NO_ALERTS_ICON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.AlertListScreen.NO_ALERTS_TEXT).assertExists()
    composeTestRule
        .onNodeWithTag(C.Tag.AlertListScreen.NO_ALERTS_TEXT)
        .assertTextEquals("No alerts here for the moment...")
  }
}
