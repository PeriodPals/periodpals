package com.android.periodpals.ui.alert

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import com.android.periodpals.R
import com.android.periodpals.model.alert.Alert
import com.android.periodpals.model.alert.AlertViewModel
import com.android.periodpals.model.alert.AlertViewModel.Companion.LOCATION_STATE_NAME
import com.android.periodpals.model.alert.AlertViewModel.Companion.MESSAGE_STATE_NAME
import com.android.periodpals.model.alert.AlertViewModel.Companion.PRODUCT_STATE_NAME
import com.android.periodpals.model.alert.AlertViewModel.Companion.URGENCY_STATE_NAME
import com.android.periodpals.model.alert.LIST_OF_PRODUCTS
import com.android.periodpals.model.alert.LIST_OF_URGENCIES
import com.android.periodpals.model.alert.Product
import com.android.periodpals.model.alert.Status
import com.android.periodpals.model.alert.Urgency
import com.android.periodpals.model.authentication.AuthenticationViewModel
import com.android.periodpals.model.location.Location
import com.android.periodpals.model.location.LocationViewModel
import com.android.periodpals.model.user.AuthenticationUserData
import com.android.periodpals.model.user.User
import com.android.periodpals.model.user.UserViewModel
import com.android.periodpals.resources.C.Tag.AlertInputs
import com.android.periodpals.resources.C.Tag.BottomNavigationMenu
import com.android.periodpals.resources.C.Tag.EditAlertScreen
import com.android.periodpals.resources.C.Tag.TopAppBar
import com.android.periodpals.services.GPSServiceImpl
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Route
import com.android.periodpals.ui.navigation.Screen
import com.android.periodpals.ui.navigation.TopLevelDestination
import com.dsc.form_builder.FormState
import com.dsc.form_builder.TextFieldState
import com.dsc.form_builder.Validators
import io.github.kakaocup.kakao.common.utilities.getResourceString
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.never
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EditAlertScreenTest {
  private lateinit var navigationActions: NavigationActions
  private lateinit var locationViewModel: LocationViewModel
  private val mockAddress = MutableStateFlow("Some address")
  private lateinit var gpsService: GPSServiceImpl
  private val mockLocationFLow = MutableStateFlow(Location.DEFAULT_LOCATION)
  private lateinit var authenticationViewModel: AuthenticationViewModel
  private lateinit var userViewModel: UserViewModel
  private lateinit var alertViewModel: AlertViewModel
  @get:Rule val composeTestRule = createComposeRule()

  companion object {
    private val PRODUCT = LIST_OF_PRODUCTS[1].textId // Pad
    private val URGENCY = LIST_OF_URGENCIES[2].textId // High
    private const val LOCATION_TEXT_INPUT = "Lausanne"
    private val LOCATION_SUGGESTION1 =
        Location(46.5218269, 6.6327025, "Lausanne, District de Lausanne")
    private val LOCATION_SUGGESTION2 = Location(46.2017559, 6.1466014, "Geneva, Switzerland")
    private val LOCATION_SUGGESTION3 = Location(46.1683026, 5.9059776, "Farges, Gex, Ain")
    private const val MESSAGE = "I need help finding a tampon"

    private const val MAX_LOCATION_LENGTH = 512
    private const val MAX_MESSAGE_LENGTH = 512

    private const val ERROR_INVALID_PRODUCT = "Please select a product"
    private const val ERROR_INVALID_URGENCY = "Please select an urgency level"
    private const val ERROR_INVALID_LOCATION = "Please select a location"
    private const val ERROR_INVALID_MESSAGE = "Please write your message"
    private const val ERROR_LOCATION_TOO_LONG =
        "Location must be less than $MAX_LOCATION_LENGTH characters"
    private const val ERROR_MESSAGE_TOO_LONG =
        "Message must be less than $MAX_MESSAGE_LENGTH characters"

    private val productValidators =
        listOf(
            Validators.Custom(
                message = ERROR_INVALID_PRODUCT,
                function = {
                  it.toString() !=
                      getResourceString(R.string.create_alert_product_dropdown_default_value)
                },
            ))
    private val urgencyValidators =
        listOf(
            Validators.Custom(
                message = ERROR_INVALID_URGENCY,
                function = {
                  it.toString() !=
                      getResourceString(R.string.create_alert_urgency_dropdown_default_value)
                },
            ))
    private val locationValidators =
        listOf(
            Validators.Required(message = ERROR_INVALID_LOCATION),
            Validators.Max(message = ERROR_LOCATION_TOO_LONG, limit = MAX_LOCATION_LENGTH),
        )
    private val messageValidators =
        listOf(
            Validators.Required(message = ERROR_INVALID_MESSAGE),
            Validators.Max(message = ERROR_MESSAGE_TOO_LONG, limit = MAX_MESSAGE_LENGTH),
        )
  }

  private val name = "John Doe"
  private val imageUrl = "https://example.com"
  private val description = "A short description"
  private val dob = "01/01/2000"
  private val preferredDistance = 500
  private val userState =
      mutableStateOf(
          User(
              name = name,
              imageUrl = imageUrl,
              description = description,
              dob = dob,
              preferredDistance,
          ))

  private val uid = "12345"
  private val email = "john.doe@example.com"
  private val authUserData = mutableStateOf(AuthenticationUserData(uid, email))

  private val alert =
      Alert(
          uid = uid,
          name = name,
          product = Product.TAMPON,
          urgency = Urgency.MEDIUM,
          location = LOCATION_SUGGESTION2.toString(),
          message = "I'm in need of a tampon",
          status = Status.CREATED,
      )

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    locationViewModel = mock(LocationViewModel::class.java)
    gpsService = mock(GPSServiceImpl::class.java)
    authenticationViewModel = mock(AuthenticationViewModel::class.java)
    userViewModel = mock(UserViewModel::class.java)
    alertViewModel = mock(AlertViewModel::class.java)

    val formState =
        FormState(
            fields =
                listOf(
                    TextFieldState(name = PRODUCT_STATE_NAME, validators = productValidators),
                    TextFieldState(name = URGENCY_STATE_NAME, validators = urgencyValidators),
                    TextFieldState(
                        name = LOCATION_STATE_NAME,
                        validators = locationValidators,
                        transform = { Location.fromString(it) },
                    ),
                    TextFieldState(name = MESSAGE_STATE_NAME, validators = messageValidators),
                ))

    `when`(alertViewModel.formState).thenReturn(formState)
    `when`(gpsService.location).thenReturn(mockLocationFLow)
    `when`(userViewModel.user).thenReturn(userState)
    `when`(authenticationViewModel.authUserData).thenReturn(authUserData)
    `when`(navigationActions.currentRoute()).thenReturn(Route.ALERT)

    `when`(alertViewModel.selectedAlert).thenReturn(mutableStateOf(alert))

    `when`(locationViewModel.locationSuggestions)
        .thenReturn(
            MutableStateFlow(
                listOf(LOCATION_SUGGESTION1, LOCATION_SUGGESTION2, LOCATION_SUGGESTION3)))
    `when`(locationViewModel.query).thenReturn(MutableStateFlow(LOCATION_SUGGESTION1.name))

    `when`(locationViewModel.address).thenReturn(mockAddress)
  }

  @Test
  fun allComponentsAreDisplayed() {
    composeTestRule.setContent {
      EditAlertScreen(locationViewModel, gpsService, alertViewModel, navigationActions)
    }

    composeTestRule.onNodeWithTag(EditAlertScreen.SCREEN).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.TOP_BAR).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(TopAppBar.TITLE_TEXT)
        .assertIsDisplayed()
        .assertTextEquals(getResourceString(R.string.edit_alert_screen_title))
    composeTestRule.onNodeWithTag(TopAppBar.GO_BACK_BUTTON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.SETTINGS_BUTTON).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.CHAT_BUTTON).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.EDIT_BUTTON).assertIsNotDisplayed()
    composeTestRule
        .onNodeWithTag(BottomNavigationMenu.BOTTOM_NAVIGATION_MENU)
        .assertIsNotDisplayed()

    composeTestRule
        .onNodeWithTag(AlertInputs.INSTRUCTION_TEXT)
        .performScrollTo()
        .assertIsDisplayed()
        .assertTextEquals(getResourceString(R.string.edit_alert_instruction_text))
    composeTestRule.onNodeWithTag(AlertInputs.PRODUCT_FIELD).performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag(AlertInputs.URGENCY_FIELD).performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag(AlertInputs.LOCATION_FIELD).performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag(AlertInputs.MESSAGE_FIELD).performScrollTo().assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(EditAlertScreen.DELETE_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
        .assertTextEquals(getResourceString(R.string.edit_alert_delete_button_text))
    composeTestRule
        .onNodeWithTag(EditAlertScreen.SAVE_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
        .assertTextEquals(getResourceString(R.string.edit_alert_save_button_text))
    composeTestRule
        .onNodeWithTag(EditAlertScreen.RESOLVE_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
        .assertTextEquals(getResourceString(R.string.edit_alert_resolve_button_text))
  }

  @Test
  fun topAppBarNavigatesBack() {
    composeTestRule.setContent {
      EditAlertScreen(locationViewModel, gpsService, alertViewModel, navigationActions)
    }

    composeTestRule.onNodeWithTag(TopAppBar.GO_BACK_BUTTON).performClick()
    verify(navigationActions).navigateTo(Screen.ALERT_LIST)
  }

  @Test
  fun updateAlertNullNavigatesToAlertList() {
    `when`(alertViewModel.selectedAlert).thenReturn(mutableStateOf(null))
    composeTestRule.setContent {
      EditAlertScreen(locationViewModel, gpsService, alertViewModel, navigationActions)
    }

    verify(alertViewModel, never()).updateAlert(any(), any(), any())
    verify(navigationActions).navigateTo(Screen.ALERT_LIST)
  }

  @Test
  fun updateAlertAllFieldsSuccessful() {
    composeTestRule.setContent {
      EditAlertScreen(locationViewModel, gpsService, alertViewModel, navigationActions)
    }

    composeTestRule.onNodeWithTag(AlertInputs.PRODUCT_FIELD).performScrollTo().performClick()
    composeTestRule
        .onNodeWithTag(AlertInputs.DROPDOWN_ITEM + PRODUCT)
        .performScrollTo()
        .performClick()

    composeTestRule.onNodeWithTag(AlertInputs.URGENCY_FIELD).performScrollTo().performClick()
    composeTestRule
        .onNodeWithTag(AlertInputs.DROPDOWN_ITEM + URGENCY)
        .performScrollTo()
        .performClick()

    composeTestRule
        .onNodeWithTag(AlertInputs.LOCATION_FIELD)
        .performScrollTo()
        .performTextClearance()
    composeTestRule
        .onNodeWithTag(AlertInputs.LOCATION_FIELD)
        .performScrollTo()
        .performTextInput(LOCATION_TEXT_INPUT)
    composeTestRule
        .onNodeWithTag(AlertInputs.DROPDOWN_ITEM + LOCATION_SUGGESTION1.name)
        .performScrollTo()
        .performClick()
    composeTestRule
        .onNodeWithTag(AlertInputs.LOCATION_FIELD)
        .performScrollTo()
        .assertTextContains(LOCATION_SUGGESTION1.name)

    composeTestRule
        .onNodeWithTag(AlertInputs.MESSAGE_FIELD)
        .performScrollTo()
        .performTextClearance()
    composeTestRule
        .onNodeWithTag(AlertInputs.MESSAGE_FIELD)
        .performScrollTo()
        .performTextInput(MESSAGE)

    composeTestRule.onNodeWithTag(EditAlertScreen.SAVE_BUTTON).performScrollTo().performClick()

    verify(alertViewModel).updateAlert(any(), any(), any())
    verify(navigationActions).navigateTo(Screen.ALERT_LIST)
  }

  @Test
  fun updateAlertOnlyProduct() {
    composeTestRule.setContent {
      EditAlertScreen(locationViewModel, gpsService, alertViewModel, navigationActions)
    }

    composeTestRule.onNodeWithTag(AlertInputs.PRODUCT_FIELD).performScrollTo().performClick()
    composeTestRule
        .onNodeWithTag(AlertInputs.DROPDOWN_ITEM + PRODUCT)
        .performScrollTo()
        .performClick()

    composeTestRule.onNodeWithTag(EditAlertScreen.SAVE_BUTTON).performScrollTo().performClick()

    verify(alertViewModel).updateAlert(any(), any(), any())
    verify(navigationActions).navigateTo(Screen.ALERT_LIST)
  }

  @Test
  fun updateAlertOnlyUrgency() {
    composeTestRule.setContent {
      EditAlertScreen(locationViewModel, gpsService, alertViewModel, navigationActions)
    }

    composeTestRule.onNodeWithTag(AlertInputs.URGENCY_FIELD).performScrollTo().performClick()
    composeTestRule
        .onNodeWithTag(AlertInputs.DROPDOWN_ITEM + URGENCY)
        .performScrollTo()
        .performClick()

    composeTestRule.onNodeWithTag(EditAlertScreen.SAVE_BUTTON).performScrollTo().performClick()

    verify(alertViewModel).updateAlert(any(), any(), any())
    verify(navigationActions).navigateTo(Screen.ALERT_LIST)
  }

  @Test
  fun updateAlertOnlyCurrentLocation() {
    composeTestRule.setContent {
      EditAlertScreen(locationViewModel, gpsService, alertViewModel, navigationActions)
    }

    composeTestRule.onNodeWithTag(AlertInputs.LOCATION_FIELD).performScrollTo().performClick()
    composeTestRule
        .onNodeWithTag(AlertInputs.DROPDOWN_ITEM + AlertInputs.CURRENT_LOCATION)
        .performScrollTo()
        .performClick()

    composeTestRule.onNodeWithTag(EditAlertScreen.SAVE_BUTTON).performScrollTo().performClick()

    verify(alertViewModel).updateAlert(any(), any(), any())
    verify(navigationActions).navigateTo(Screen.ALERT_LIST)
  }

  @Test
  fun updateAlertOnlyOtherLocation() {
    composeTestRule.setContent {
      EditAlertScreen(locationViewModel, gpsService, alertViewModel, navigationActions)
    }

    composeTestRule.onNodeWithTag(AlertInputs.LOCATION_FIELD).performScrollTo().performClick()
    composeTestRule
        .onNodeWithTag(AlertInputs.DROPDOWN_ITEM + LOCATION_SUGGESTION3.name)
        .performScrollTo()
        .performClick()
    composeTestRule
        .onNodeWithTag(AlertInputs.LOCATION_FIELD)
        .performScrollTo()
        .assertTextContains(LOCATION_SUGGESTION3.name)

    composeTestRule.onNodeWithTag(EditAlertScreen.SAVE_BUTTON).performScrollTo().performClick()

    verify(alertViewModel).updateAlert(any(), any(), any())
    verify(navigationActions).navigateTo(Screen.ALERT_LIST)
  }

  // TODO: change this behavior eventually
  @Test
  fun updateAlertEmptyLocationDoesNotChangeLocation() {
    composeTestRule.setContent {
      EditAlertScreen(locationViewModel, gpsService, alertViewModel, navigationActions)
    }

    composeTestRule
        .onNodeWithTag(AlertInputs.LOCATION_FIELD)
        .performScrollTo()
        .performTextClearance()

    composeTestRule.onNodeWithTag(EditAlertScreen.SAVE_BUTTON).performScrollTo().performClick()

    verify(alertViewModel).updateAlert(any(), any(), any())
    verify(navigationActions).navigateTo(Screen.ALERT_LIST)
  }

  @Test
  fun updateAlertOnlyMessage() {
    composeTestRule.setContent {
      EditAlertScreen(locationViewModel, gpsService, alertViewModel, navigationActions)
    }

    composeTestRule
        .onNodeWithTag(AlertInputs.MESSAGE_FIELD)
        .performScrollTo()
        .performTextClearance()
    composeTestRule
        .onNodeWithTag(AlertInputs.MESSAGE_FIELD)
        .performScrollTo()
        .performTextInput(MESSAGE)

    composeTestRule
        .onNodeWithTag(EditAlertScreen.SAVE_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
        .performClick()

    verify(alertViewModel).updateAlert(any(), any(), any())
    verify(navigationActions).navigateTo(Screen.ALERT_LIST)
  }

  @Test
  fun updateAlertInvalidMessage() {
    composeTestRule.setContent {
      EditAlertScreen(locationViewModel, gpsService, alertViewModel, navigationActions)
    }

    composeTestRule
        .onNodeWithTag(AlertInputs.MESSAGE_FIELD)
        .performScrollTo()
        .performTextClearance()
    composeTestRule.onNodeWithTag(AlertInputs.MESSAGE_FIELD).performScrollTo().performTextInput("")

    composeTestRule
        .onNodeWithTag(EditAlertScreen.SAVE_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
        .performClick()

    verify(alertViewModel, never()).updateAlert(any(), any(), any())
    verify(navigationActions, never()).navigateTo(any<TopLevelDestination>())
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun deleteAlertSuccessfully() {
    composeTestRule.setContent {
      EditAlertScreen(locationViewModel, gpsService, alertViewModel, navigationActions)
    }

    composeTestRule.onNodeWithTag(EditAlertScreen.DELETE_BUTTON).performScrollTo().performClick()

    verify(alertViewModel).deleteAlert(eq(alert.id), any(), any())
    verify(navigationActions).navigateTo(Screen.ALERT_LIST)
  }

  @Test
  fun resolveAlertSuccessfully() {
    composeTestRule.setContent {
      EditAlertScreen(locationViewModel, gpsService, alertViewModel, navigationActions)
    }

    composeTestRule.onNodeWithTag(EditAlertScreen.RESOLVE_BUTTON).performScrollTo().performClick()

    verify(navigationActions).navigateTo(Screen.ALERT_LIST)
  }
}
