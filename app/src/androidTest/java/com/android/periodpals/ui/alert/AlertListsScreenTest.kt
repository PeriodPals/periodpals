package com.android.periodpals.ui.alert

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.periodpals.resources.C.Tag.AlertListsScreen
import com.android.periodpals.resources.C.Tag.AlertListsScreen.MyAlertItem
import com.android.periodpals.resources.C.Tag.AlertListsScreen.PalsAlertItem
import com.android.periodpals.resources.C.Tag.BottomNavigationMenu
import com.android.periodpals.resources.C.Tag.TopAppBar
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

  companion object {
    private const val NO_MY_ALERTS_TEXT = "You haven't asked for help yet !"
    private const val NO_PALS_ALERTS_TEXT = "No pal needs help yet !"
  }

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)

    `when`(navigationActions.currentRoute()).thenReturn(Route.ALERT_LIST)
  }

  @Test
  fun sharedComponentsCorrectlyDisplayed() {
    composeTestRule.setContent { AlertListsScreen(navigationActions) }

    composeTestRule.onNodeWithTag(AlertListsScreen.SCREEN).assertIsDisplayed()
    composeTestRule.onNodeWithTag(AlertListsScreen.TAB_ROW).assertIsDisplayed()
    composeTestRule.onNodeWithTag(AlertListsScreen.MY_ALERTS_TAB).assertIsDisplayed()
    composeTestRule.onNodeWithTag(AlertListsScreen.PALS_ALERTS_TAB).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.TOP_BAR).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(TopAppBar.TITLE_TEXT)
        .assertIsDisplayed()
        .assertTextEquals("Alert Lists")
    composeTestRule.onNodeWithTag(TopAppBar.GO_BACK_BUTTON).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.EDIT_BUTTON).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(BottomNavigationMenu.BOTTOM_NAVIGATION_MENU).assertIsDisplayed()
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

    composeTestRule.onNodeWithTag(AlertListsScreen.NO_ALERTS_CARD).assertIsDisplayed()
    composeTestRule.onNodeWithTag(AlertListsScreen.NO_ALERTS_ICON).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(AlertListsScreen.NO_ALERTS_TEXT)
        .assertIsDisplayed()
        .assertTextEquals(NO_MY_ALERTS_TEXT)

    composeTestRule.onNodeWithTag(MyAlertItem.MY_ALERT).assertDoesNotExist()
    composeTestRule.onNodeWithTag(AlertListsScreen.ALERT_PROFILE_PICTURE).assertDoesNotExist()
    composeTestRule.onNodeWithTag(AlertListsScreen.ALERT_TIME_AND_LOCATION).assertDoesNotExist()
    composeTestRule.onNodeWithTag(AlertListsScreen.ALERT_PRODUCT_AND_URGENCY).assertDoesNotExist()
    composeTestRule.onNodeWithTag(AlertListsScreen.ALERT_PRODUCT_TYPE).assertDoesNotExist()
    composeTestRule.onNodeWithTag(AlertListsScreen.ALERT_URGENCY).assertDoesNotExist()
    composeTestRule.onNodeWithTag(MyAlertItem.MY_EDIT_BUTTON).assertDoesNotExist()
    composeTestRule.onNodeWithTag(MyAlertItem.MY_EDIT_ICON).assertDoesNotExist()
    composeTestRule.onNodeWithTag(MyAlertItem.MY_EDIT_TEXT).assertDoesNotExist()
  }

  @Test
  fun palsAlertsTabContentIsCorrect() {
    composeTestRule.setContent { AlertListsScreen(navigationActions) }

    composeTestRule.onNodeWithTag(AlertListsScreen.PALS_ALERTS_TAB).performClick()

    composeTestRule.onNodeWithTag(AlertListsScreen.NO_ALERTS_CARD).assertIsDisplayed()
    composeTestRule.onNodeWithTag(AlertListsScreen.NO_ALERTS_ICON).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(AlertListsScreen.NO_ALERTS_TEXT)
        .assertIsDisplayed()
        .assertTextEquals(NO_PALS_ALERTS_TEXT)

    composeTestRule.onNodeWithTag(AlertListsScreen.ALERT_PROFILE_PICTURE).assertDoesNotExist()
    composeTestRule.onNodeWithTag(AlertListsScreen.ALERT_TIME_AND_LOCATION).assertDoesNotExist()
    composeTestRule.onNodeWithTag(PalsAlertItem.PAL_NAME).assertDoesNotExist()
    composeTestRule.onNodeWithTag(AlertListsScreen.ALERT_PRODUCT_AND_URGENCY).assertDoesNotExist()
    composeTestRule.onNodeWithTag(AlertListsScreen.ALERT_PRODUCT_TYPE).assertDoesNotExist()
    composeTestRule.onNodeWithTag(AlertListsScreen.ALERT_URGENCY).assertDoesNotExist()

    composeTestRule.onNodeWithTag(PalsAlertItem.PAL_MESSAGE).assertDoesNotExist().apply {
      composeTestRule.onNodeWithTag(PalsAlertItem.PAL_BUTTONS).assertDoesNotExist()
      composeTestRule.onNodeWithTag(PalsAlertItem.PAL_ACCEPT_BUTTON).assertDoesNotExist()
      composeTestRule.onNodeWithTag(PalsAlertItem.PAL_ACCEPT_ICON).assertDoesNotExist()
      composeTestRule.onNodeWithTag(PalsAlertItem.PAL_ACCEPT_TEXT).assertDoesNotExist()
      composeTestRule.onNodeWithTag(PalsAlertItem.PAL_DECLINE_BUTTON).assertDoesNotExist()
      composeTestRule.onNodeWithTag(PalsAlertItem.PAL_DECLINE_ICON).assertDoesNotExist()
      composeTestRule.onNodeWithTag(PalsAlertItem.PAL_DECLINE_TEXT).assertDoesNotExist()
    }
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
}
