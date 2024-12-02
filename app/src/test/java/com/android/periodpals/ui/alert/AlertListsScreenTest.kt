package com.android.periodpals.ui.alert

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.android.periodpals.model.alert.Alert
import com.android.periodpals.model.alert.AlertViewModel
import com.android.periodpals.model.alert.Product
import com.android.periodpals.model.alert.Status
import com.android.periodpals.model.alert.Urgency
import com.android.periodpals.model.authentication.AuthenticationViewModel
import com.android.periodpals.model.user.AuthenticationUserData
import com.android.periodpals.resources.C.Tag.AlertListsScreen
import com.android.periodpals.resources.C.Tag.AlertListsScreen.MyAlertItem
import com.android.periodpals.resources.C.Tag.AlertListsScreen.PalsAlertItem
import com.android.periodpals.resources.C.Tag.BottomNavigationMenu
import com.android.periodpals.resources.C.Tag.TopAppBar
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Route
import com.android.periodpals.ui.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AlertListsScreenTest {

  private lateinit var navigationActions: NavigationActions
  private lateinit var alertViewModel: AlertViewModel
  private lateinit var authenticationViewModel: AuthenticationViewModel
  @get:Rule val composeTestRule = createComposeRule()

  private val uid = "12345"
  private val email = "john.doe@example.com"
  private val authUserData = mutableStateOf(AuthenticationUserData(uid, email))

  companion object {
    private const val NO_MY_ALERTS_TEXT = "You haven't asked for help yet !"
    private const val NO_PALS_ALERTS_TEXT = "No pal needs help yet !"
    private val MY_ALERTS_LIST: List<Alert> =
        listOf(
            Alert(
                id = "1",
                uid = "1",
                name = "Hippo Alpha",
                product = Product.TAMPON,
                urgency = Urgency.HIGH,
                createdAt = "2011-12-03T10:15:30+01:00",
                location = "46.9484,7.4521,Bern",
                message = "I need help!",
                status = Status.CREATED,
            ),
            Alert(
                id = "2",
                uid = "1",
                name = "Hippo Beta",
                product = Product.PAD,
                urgency = Urgency.LOW,
                createdAt = "2011-12-03T10:15:30+01:00",
                location = "46.9484,7.4521,Bern",
                message = "I forgot my pads at home :/",
                status = Status.PENDING,
            ),
        )
    private val PALS_ALERTS_LIST: List<Alert> =
        listOf(
            Alert(
                id = "3",
                uid = "2",
                name = "Hippo Gamma",
                product = Product.TAMPON,
                urgency = Urgency.MEDIUM,
                createdAt = "2011-12-03T10:15:30+01:00",
                location = "19.4326,-99.1331,Mexico City",
                message = "I need help!",
                status = Status.CREATED,
            ),
            Alert(
                id = "4",
                uid = "3",
                name = "Hippo Delta",
                product = Product.PAD,
                urgency = Urgency.HIGH,
                createdAt = "2011-12-03T10:15:30+01:00",
                location = "19.4326,-99.1331,Mexico City",
                message = "I forgot my pads at home :/",
                status = Status.PENDING,
            ),
        )
  }

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    alertViewModel = mock(AlertViewModel::class.java)
    authenticationViewModel = mock(AuthenticationViewModel::class.java)

    `when`(navigationActions.currentRoute()).thenReturn(Route.ALERT_LIST)
    `when`(authenticationViewModel.authUserData).thenReturn(authUserData)
    `when`(alertViewModel.myAlerts).thenReturn(mutableStateOf(MY_ALERTS_LIST))
    `when`(alertViewModel.palAlerts).thenReturn(mutableStateOf(PALS_ALERTS_LIST))
    `when`(alertViewModel.alerts).thenReturn(mutableStateOf(MY_ALERTS_LIST + PALS_ALERTS_LIST))
  }

  @Test
  fun sharedComponentsCorrectlyDisplayed() {
    composeTestRule.setContent {
      AlertListsScreen(navigationActions, alertViewModel, authenticationViewModel)
    }

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
    composeTestRule.onNodeWithTag(TopAppBar.SETTINGS_BUTTON).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.EDIT_BUTTON).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(BottomNavigationMenu.BOTTOM_NAVIGATION_MENU).assertIsDisplayed()
  }

  @Test
  fun myAlertsTabIsSelectedByDefault() {
    composeTestRule.setContent {
      AlertListsScreen(navigationActions, alertViewModel, authenticationViewModel)
    }

    composeTestRule.onNodeWithTag(AlertListsScreen.MY_ALERTS_TAB).assertIsSelected()
    composeTestRule.onNodeWithTag(AlertListsScreen.PALS_ALERTS_TAB).assertIsNotSelected()
  }

  @Test
  fun switchingTabWorks() {
    composeTestRule.setContent {
      AlertListsScreen(navigationActions, alertViewModel, authenticationViewModel)
    }

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
  fun myAlertsEmptyIsCorrect() {
    `when`(alertViewModel.alerts).thenReturn(mutableStateOf(PALS_ALERTS_LIST))
    `when`(alertViewModel.myAlerts).thenReturn(mutableStateOf(emptyList()))
    composeTestRule.setContent {
      AlertListsScreen(navigationActions, alertViewModel, authenticationViewModel)
    }

    composeTestRule.onNodeWithTag(AlertListsScreen.MY_ALERTS_TAB).assertIsSelected()
    composeTestRule.onNodeWithTag(AlertListsScreen.PALS_ALERTS_TAB).assertIsNotSelected()

    composeTestRule
        .onNodeWithTag(AlertListsScreen.NO_ALERTS_CARD)
        .performScrollTo()
        .assertIsDisplayed()
        .assertHasNoClickAction()
    composeTestRule
        .onNodeWithTag(AlertListsScreen.NO_ALERTS_ICON)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(AlertListsScreen.NO_ALERTS_TEXT)
        .performScrollTo()
        .assertIsDisplayed()
        .assertTextEquals(NO_MY_ALERTS_TEXT)
  }

  @Test
  fun myAlertsListIsCorrect() {
    composeTestRule.setContent {
      AlertListsScreen(navigationActions, alertViewModel, authenticationViewModel)
    }

    composeTestRule.onNodeWithTag(AlertListsScreen.MY_ALERTS_TAB).assertIsSelected()
    composeTestRule.onNodeWithTag(AlertListsScreen.PALS_ALERTS_TAB).assertIsNotSelected()
    composeTestRule.onNodeWithTag(AlertListsScreen.NO_ALERTS_CARD).assertDoesNotExist()

    MY_ALERTS_LIST.forEach { alert ->
      val alertId: String = alert.id
      composeTestRule
          .onNodeWithTag(MyAlertItem.MY_ALERT + alertId)
          .performScrollTo()
          .assertIsDisplayed()
          .assertHasNoClickAction()
      composeTestRule
          .onNodeWithTag(AlertListsScreen.ALERT_PROFILE_PICTURE + alertId, useUnmergedTree = true)
          .performScrollTo()
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag(AlertListsScreen.ALERT_TIME_AND_LOCATION + alertId, useUnmergedTree = true)
          .performScrollTo()
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag(
              AlertListsScreen.ALERT_PRODUCT_AND_URGENCY + alertId, useUnmergedTree = true)
          .performScrollTo()
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag(AlertListsScreen.ALERT_PRODUCT_TYPE + alertId, useUnmergedTree = true)
          .performScrollTo()
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag(AlertListsScreen.ALERT_URGENCY + alertId, useUnmergedTree = true)
          .performScrollTo()
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag(MyAlertItem.MY_EDIT_BUTTON + alertId, useUnmergedTree = true)
          .performScrollTo()
          .assertIsDisplayed()
          .assertHasClickAction()
    }
  }

  @Test
  fun myAlertsEditNavigates() {
    composeTestRule.setContent {
      AlertListsScreen(navigationActions, alertViewModel, authenticationViewModel)
    }

    composeTestRule.onNodeWithTag(AlertListsScreen.MY_ALERTS_TAB).assertIsSelected()

    val alertId = MY_ALERTS_LIST.first().id
    composeTestRule.onNodeWithTag(MyAlertItem.MY_EDIT_BUTTON + alertId).performClick()

    verify(navigationActions).navigateTo(Screen.EDIT_ALERT)
  }

  @Test
  fun palsAlertsEmptyIsCorrect() {
    `when`(alertViewModel.alerts).thenReturn(mutableStateOf(MY_ALERTS_LIST))
    `when`(alertViewModel.palAlerts).thenReturn(mutableStateOf(emptyList()))
    composeTestRule.setContent {
      AlertListsScreen(navigationActions, alertViewModel, authenticationViewModel)
    }

    composeTestRule.onNodeWithTag(AlertListsScreen.MY_ALERTS_TAB).assertIsSelected()
    composeTestRule
        .onNodeWithTag(AlertListsScreen.PALS_ALERTS_TAB)
        .assertIsNotSelected()
        .performClick()
        .assertIsSelected()
    composeTestRule.onNodeWithTag(AlertListsScreen.MY_ALERTS_TAB).assertIsNotSelected()

    composeTestRule
        .onNodeWithTag(AlertListsScreen.NO_ALERTS_CARD)
        .performScrollTo()
        .assertIsDisplayed()
        .assertHasNoClickAction()
    composeTestRule
        .onNodeWithTag(AlertListsScreen.NO_ALERTS_ICON)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(AlertListsScreen.NO_ALERTS_TEXT)
        .performScrollTo()
        .assertIsDisplayed()
        .assertTextEquals(NO_PALS_ALERTS_TEXT)
  }

  @Test
  fun palsAlertsListNoActionIsCorrect() {
    composeTestRule.setContent {
      AlertListsScreen(navigationActions, alertViewModel, authenticationViewModel)
    }

    composeTestRule.onNodeWithTag(AlertListsScreen.MY_ALERTS_TAB).assertIsSelected()
    composeTestRule
        .onNodeWithTag(AlertListsScreen.PALS_ALERTS_TAB)
        .assertIsNotSelected()
        .performClick()
        .assertIsSelected()
    composeTestRule.onNodeWithTag(AlertListsScreen.MY_ALERTS_TAB).assertIsNotSelected()
    composeTestRule.onNodeWithTag(AlertListsScreen.NO_ALERTS_CARD).assertDoesNotExist()

    PALS_ALERTS_LIST.forEach { alert ->
      val alertId: String = alert.id
      composeTestRule
          .onNodeWithTag(PalsAlertItem.PAL_ALERT + alertId)
          .performScrollTo()
          .assertIsDisplayed()
          .assertHasClickAction()
      composeTestRule
          .onNodeWithTag(AlertListsScreen.ALERT_PROFILE_PICTURE + alertId, useUnmergedTree = true)
          .performScrollTo()
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag(AlertListsScreen.ALERT_TIME_AND_LOCATION + alertId, useUnmergedTree = true)
          .performScrollTo()
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag(PalsAlertItem.PAL_NAME + alertId, useUnmergedTree = true)
          .performScrollTo()
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag(
              AlertListsScreen.ALERT_PRODUCT_AND_URGENCY + alertId, useUnmergedTree = true)
          .performScrollTo()
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag(AlertListsScreen.ALERT_PRODUCT_TYPE + alertId, useUnmergedTree = true)
          .performScrollTo()
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag(AlertListsScreen.ALERT_URGENCY + alertId, useUnmergedTree = true)
          .performScrollTo()
          .assertIsDisplayed()

      composeTestRule
          .onNodeWithTag(PalsAlertItem.PAL_MESSAGE + alertId, useUnmergedTree = true)
          .assertIsNotDisplayed()
      composeTestRule
          .onNodeWithTag(PalsAlertItem.PAL_DIVIDER + alertId, useUnmergedTree = true)
          .assertIsNotDisplayed()
      composeTestRule
          .onNodeWithTag(PalsAlertItem.PAL_BUTTONS + alertId, useUnmergedTree = true)
          .assertIsNotDisplayed()
      composeTestRule
          .onNodeWithTag(PalsAlertItem.PAL_ACCEPT_BUTTON + alertId, useUnmergedTree = true)
          .assertIsNotDisplayed()
      composeTestRule
          .onNodeWithTag(PalsAlertItem.PAL_DECLINE_BUTTON + alertId, useUnmergedTree = true)
          .assertIsNotDisplayed()
    }
  }

  @Test
  fun palsAlertsListDoubleClickIsCorrect() {
    composeTestRule.setContent {
      AlertListsScreen(navigationActions, alertViewModel, authenticationViewModel)
    }

    composeTestRule.onNodeWithTag(AlertListsScreen.MY_ALERTS_TAB).assertIsSelected()
    composeTestRule
        .onNodeWithTag(AlertListsScreen.PALS_ALERTS_TAB)
        .assertIsNotSelected()
        .performClick()
        .assertIsSelected()
    composeTestRule.onNodeWithTag(AlertListsScreen.MY_ALERTS_TAB).assertIsNotSelected()
    composeTestRule.onNodeWithTag(AlertListsScreen.NO_ALERTS_CARD).assertDoesNotExist()

    PALS_ALERTS_LIST.forEach { alert ->
      val alertId: String = alert.id
      composeTestRule
          .onNodeWithTag(PalsAlertItem.PAL_ALERT + alertId)
          .performScrollTo()
          .assertIsDisplayed()
          .assertHasClickAction()
      composeTestRule
          .onNodeWithTag(AlertListsScreen.ALERT_PROFILE_PICTURE + alertId, useUnmergedTree = true)
          .performScrollTo()
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag(AlertListsScreen.ALERT_TIME_AND_LOCATION + alertId, useUnmergedTree = true)
          .performScrollTo()
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag(PalsAlertItem.PAL_NAME + alertId, useUnmergedTree = true)
          .performScrollTo()
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag(
              AlertListsScreen.ALERT_PRODUCT_AND_URGENCY + alertId, useUnmergedTree = true)
          .performScrollTo()
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag(AlertListsScreen.ALERT_PRODUCT_TYPE + alertId, useUnmergedTree = true)
          .performScrollTo()
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag(AlertListsScreen.ALERT_URGENCY + alertId, useUnmergedTree = true)
          .performScrollTo()
          .assertIsDisplayed()

      // First click to display the alert's details
      composeTestRule.onNodeWithTag(PalsAlertItem.PAL_ALERT + alertId).performClick()
      composeTestRule
          .onNodeWithTag(PalsAlertItem.PAL_MESSAGE + alertId, useUnmergedTree = true)
          .assertIsDisplayed()
      if (alert.status == Status.CREATED) {
        composeTestRule
            .onNodeWithTag(PalsAlertItem.PAL_DIVIDER + alertId, useUnmergedTree = true)
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithTag(PalsAlertItem.PAL_BUTTONS + alertId, useUnmergedTree = true)
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithTag(PalsAlertItem.PAL_ACCEPT_BUTTON + alertId, useUnmergedTree = true)
            .assertIsDisplayed()
            .assertHasClickAction()
        composeTestRule
            .onNodeWithTag(PalsAlertItem.PAL_DECLINE_BUTTON + alertId, useUnmergedTree = true)
            .assertIsDisplayed()
            .assertHasClickAction()
      }

      // Second click to toggle the alert
      composeTestRule.onNodeWithTag(PalsAlertItem.PAL_ALERT + alertId).performClick()
      composeTestRule
          .onNodeWithTag(PalsAlertItem.PAL_MESSAGE + alertId, useUnmergedTree = true)
          .assertIsNotDisplayed()
      composeTestRule
          .onNodeWithTag(PalsAlertItem.PAL_DIVIDER + alertId, useUnmergedTree = true)
          .assertIsNotDisplayed()
      composeTestRule
          .onNodeWithTag(PalsAlertItem.PAL_BUTTONS + alertId, useUnmergedTree = true)
          .assertIsNotDisplayed()
      composeTestRule
          .onNodeWithTag(PalsAlertItem.PAL_ACCEPT_BUTTON + alertId, useUnmergedTree = true)
          .assertIsNotDisplayed()
      composeTestRule
          .onNodeWithTag(PalsAlertItem.PAL_DECLINE_BUTTON + alertId, useUnmergedTree = true)
          .assertIsNotDisplayed()
    }
  }
}
