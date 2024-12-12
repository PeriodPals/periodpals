package com.android.periodpals.ui.alert

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.SemanticsActions
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
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextInput
import com.android.periodpals.model.alert.Alert
import com.android.periodpals.model.alert.AlertViewModel
import com.android.periodpals.model.alert.Product
import com.android.periodpals.model.alert.Status
import com.android.periodpals.model.alert.Urgency
import com.android.periodpals.model.authentication.AuthenticationViewModel
import com.android.periodpals.model.location.Location
import com.android.periodpals.model.location.LocationViewModel
import com.android.periodpals.model.user.AuthenticationUserData
import com.android.periodpals.model.user.UserViewModel
import com.android.periodpals.resources.C.Tag.AlertInputs
import com.android.periodpals.resources.C.Tag.AlertListsScreen
import com.android.periodpals.resources.C.Tag.AlertListsScreen.MyAlertItem
import com.android.periodpals.resources.C.Tag.AlertListsScreen.PalsAlertItem
import com.android.periodpals.resources.C.Tag.BottomNavigationMenu
import com.android.periodpals.resources.C.Tag.TopAppBar
import com.android.periodpals.services.GPSServiceImpl
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Route
import com.android.periodpals.ui.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

private const val LOCATION = "Bern"

@RunWith(RobolectricTestRunner::class)
class AlertListsScreenTest {

  private lateinit var navigationActions: NavigationActions
  private lateinit var userViewModel: UserViewModel
  private lateinit var alertViewModel: AlertViewModel
  private lateinit var authenticationViewModel: AuthenticationViewModel
  private lateinit var locationViewModel: LocationViewModel
  private lateinit var gpsService: GPSServiceImpl
  private val mockLocationFLow = MutableStateFlow(Location.DEFAULT_LOCATION)
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
    private var PALS_ALERTS_LIST: List<Alert> =
        listOf(
            Alert(
                id = "3",
                uid = "2",
                name = "Hippo Gamma",
                product = Product.TAMPON,
                urgency = Urgency.MEDIUM,
                createdAt = "2011-12-03T10:15:30+01:00",
                location = "46.9484,7.4521,Bern",
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
    userViewModel = mock(UserViewModel::class.java)
    alertViewModel = mock(AlertViewModel::class.java)
    authenticationViewModel = mock(AuthenticationViewModel::class.java)
    locationViewModel = mock(LocationViewModel::class.java)
    gpsService = mock(GPSServiceImpl::class.java)

    `when`(gpsService.location).thenReturn(mockLocationFLow)
    `when`(navigationActions.currentRoute()).thenReturn(Route.ALERT_LIST)
    `when`(authenticationViewModel.authUserData).thenReturn(authUserData)
    `when`(alertViewModel.myAlerts).thenReturn(mutableStateOf(MY_ALERTS_LIST))
    `when`(alertViewModel.palAlerts).thenReturn(mutableStateOf(PALS_ALERTS_LIST))
    `when`(alertViewModel.alerts).thenReturn(mutableStateOf(MY_ALERTS_LIST + PALS_ALERTS_LIST))

    `when`(locationViewModel.locationSuggestions)
        .thenReturn(MutableStateFlow(listOf(Location.DEFAULT_LOCATION)))
    `when`(locationViewModel.query).thenReturn(MutableStateFlow(Location.DEFAULT_LOCATION.name))
  }

  @Test
  fun sharedComponentsCorrectlyDisplayed() {
    composeTestRule.setContent {
      AlertListsScreen(
          userViewModel,
          alertViewModel,
          authenticationViewModel,
          locationViewModel,
          gpsService,
          navigationActions)
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
    composeTestRule.onNodeWithTag(TopAppBar.CHAT_BUTTON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.EDIT_BUTTON).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(BottomNavigationMenu.BOTTOM_NAVIGATION_MENU).assertIsDisplayed()
  }

  @Test
  fun myAlertsTabIsSelectedByDefault() {
    composeTestRule.setContent {
      AlertListsScreen(
          userViewModel,
          alertViewModel,
          authenticationViewModel,
          locationViewModel,
          gpsService,
          navigationActions)
    }
    composeTestRule.onNodeWithTag(AlertListsScreen.MY_ALERTS_TAB).assertIsSelected()
    composeTestRule.onNodeWithTag(AlertListsScreen.PALS_ALERTS_TAB).assertIsNotSelected()
  }

  @Test
  fun switchingTabWorks() {
    composeTestRule.setContent {
      AlertListsScreen(
          userViewModel,
          alertViewModel,
          authenticationViewModel,
          locationViewModel,
          gpsService,
          navigationActions)
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
      AlertListsScreen(
          userViewModel,
          alertViewModel,
          authenticationViewModel,
          locationViewModel,
          gpsService,
          navigationActions)
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
      AlertListsScreen(
          userViewModel,
          alertViewModel,
          authenticationViewModel,
          locationViewModel,
          gpsService,
          navigationActions)
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
      AlertListsScreen(
          userViewModel,
          alertViewModel,
          authenticationViewModel,
          locationViewModel,
          gpsService,
          navigationActions)
    }

    composeTestRule.onNodeWithTag(AlertListsScreen.MY_ALERTS_TAB).assertIsSelected()

    val alertId = MY_ALERTS_LIST.first().id
    composeTestRule.onNodeWithTag(MyAlertItem.MY_EDIT_BUTTON + alertId).performClick()

    verify(navigationActions).navigateTo(Screen.EDIT_ALERT)
  }

  @Test
  fun chatButtonNavigatesToChatScreen() {
    composeTestRule.setContent {
      AlertListsScreen(
          userViewModel,
          alertViewModel,
          authenticationViewModel,
          locationViewModel,
          gpsService,
          navigationActions)
    }

    composeTestRule.onNodeWithTag(TopAppBar.CHAT_BUTTON).performClick()

    verify(navigationActions).navigateTo(Screen.CHAT)
  }

  @Test
  fun palsAlertsEmptyIsCorrect() {
    `when`(alertViewModel.alerts).thenReturn(mutableStateOf(MY_ALERTS_LIST))
    `when`(alertViewModel.palAlerts).thenReturn(mutableStateOf(emptyList()))
    composeTestRule.setContent {
      AlertListsScreen(
          userViewModel,
          alertViewModel,
          authenticationViewModel,
          locationViewModel,
          gpsService,
          navigationActions)
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
      AlertListsScreen(
          userViewModel,
          alertViewModel,
          authenticationViewModel,
          locationViewModel,
          gpsService,
          navigationActions)
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
      AlertListsScreen(
          userViewModel,
          alertViewModel,
          authenticationViewModel,
          locationViewModel,
          gpsService,
          navigationActions)
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

  @Test
  fun filterFabDisplaysDialog() {
    composeTestRule.setContent {
      AlertListsScreen(
          userViewModel,
          alertViewModel,
          authenticationViewModel,
          locationViewModel,
          gpsService,
          navigationActions)
    }
    composeTestRule
        .onNodeWithTag(AlertListsScreen.PALS_ALERTS_TAB)
        .performClick()
        .assertIsSelected()
    composeTestRule.onNodeWithTag(AlertListsScreen.FILTER_FAB).assertIsDisplayed()
    composeTestRule.onNodeWithTag(AlertListsScreen.FILTER_FAB).performClick()
    composeTestRule.onNodeWithTag(AlertListsScreen.FILTER_DIALOG).assertIsDisplayed()
    composeTestRule.onNodeWithTag(AlertListsScreen.FILTER_DIALOG_TEXT).assertIsDisplayed()
    composeTestRule.onNodeWithTag(AlertInputs.LOCATION_FIELD).assertIsDisplayed()
    composeTestRule.onNodeWithTag(AlertListsScreen.FILTER_RADIUS_TEXT).assertIsDisplayed()
    composeTestRule.onNodeWithTag(AlertListsScreen.FILTER_RADIUS_SLIDER).assertIsDisplayed()
    composeTestRule.onNodeWithTag(AlertListsScreen.FILTER_APPLY_BUTTON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(AlertListsScreen.FILTER_RESET_BUTTON).assertIsDisplayed()
  }

  @Test
  fun filterFabCorrectlyFilters() {
    composeTestRule.setContent {
      AlertListsScreen(
          userViewModel,
          alertViewModel,
          authenticationViewModel,
          locationViewModel,
          gpsService,
          navigationActions)
    }
    composeTestRule
        .onNodeWithTag(AlertListsScreen.PALS_ALERTS_TAB)
        .performClick()
        .assertIsSelected()
    composeTestRule.onNodeWithTag(AlertListsScreen.FILTER_FAB).assertIsDisplayed().performClick()
    composeTestRule.onNodeWithTag(AlertInputs.LOCATION_FIELD).performTextInput(LOCATION)
    composeTestRule
        .onNodeWithTag(AlertInputs.DROPDOWN_ITEM + Location.DEFAULT_LOCATION.name)
        .performClick()
    composeTestRule.onNodeWithTag(AlertListsScreen.FILTER_RADIUS_SLIDER).performSemanticsAction(
        SemanticsActions.SetProgress) {
          it(200.0f)
        }
    composeTestRule.onNodeWithTag(AlertListsScreen.FILTER_APPLY_BUTTON).performClick()
    verify(alertViewModel)
        .fetchAlertsWithinRadius(eq(Location.DEFAULT_LOCATION), eq(200.0), any(), any())

    `when`(alertViewModel.palAlerts).thenReturn(mutableStateOf(listOf(PALS_ALERTS_LIST[0])))
    composeTestRule.runOnIdle {
      assert(alertViewModel.palAlerts.value == listOf(PALS_ALERTS_LIST[0]))
    }
    composeTestRule.onNodeWithTag(AlertListsScreen.SCREEN).assertIsDisplayed()
    composeTestRule.onNodeWithTag(AlertListsScreen.FILTER_FAB_BUBBLE).assertIsDisplayed()
  }

  @Test
  fun filterFabResetsFilters() {
    composeTestRule.setContent {
      AlertListsScreen(
          userViewModel,
          alertViewModel,
          authenticationViewModel,
          locationViewModel,
          gpsService,
          navigationActions)
    }
    composeTestRule
        .onNodeWithTag(AlertListsScreen.PALS_ALERTS_TAB)
        .performClick()
        .assertIsSelected()
    composeTestRule.onNodeWithTag(AlertListsScreen.FILTER_FAB).performClick()
    composeTestRule.onNodeWithTag(AlertListsScreen.FILTER_RESET_BUTTON).performClick()
    verify(alertViewModel).removeLocationFilter()
    composeTestRule.onNodeWithTag(AlertListsScreen.FILTER_FAB_BUBBLE).assertIsNotDisplayed()
    assert(alertViewModel.palAlerts.value == PALS_ALERTS_LIST)
  }
}
