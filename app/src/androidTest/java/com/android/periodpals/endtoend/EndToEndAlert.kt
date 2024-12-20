package com.android.periodpals.endtoend

import android.Manifest
import android.util.Log
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.rule.GrantPermissionRule
import com.android.periodpals.BuildConfig
import com.android.periodpals.MainActivity
import com.android.periodpals.model.alert.AlertModelSupabase
import com.android.periodpals.model.alert.AlertViewModel
import com.android.periodpals.model.alert.LIST_OF_PRODUCTS
import com.android.periodpals.model.alert.LIST_OF_URGENCIES
import com.android.periodpals.model.authentication.AuthenticationModelSupabase
import com.android.periodpals.model.authentication.AuthenticationViewModel
import com.android.periodpals.model.location.Location
import com.android.periodpals.model.user.User
import com.android.periodpals.model.user.UserRepositorySupabase
import com.android.periodpals.model.user.UserViewModel
import com.android.periodpals.resources.C.Tag.AlertInputs
import com.android.periodpals.resources.C.Tag.AlertListsScreen
import com.android.periodpals.resources.C.Tag.AlertListsScreen.MyAlertItem
import com.android.periodpals.resources.C.Tag.AuthenticationScreens
import com.android.periodpals.resources.C.Tag.AuthenticationScreens.SignInScreen
import com.android.periodpals.resources.C.Tag.BottomNavigationMenu
import com.android.periodpals.resources.C.Tag.CreateAlertScreen
import com.android.periodpals.resources.C.Tag.EditAlertScreen
import com.android.periodpals.resources.C.Tag.ProfileScreens.ProfileScreen
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private const val TAG = "EndToEndAlert"
private const val TIMEOUT = 60_000L

class EndToEndAlert : TestCase() {
  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()
  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(
          Manifest.permission.POST_NOTIFICATIONS,
          Manifest.permission.ACCESS_FINE_LOCATION,
          Manifest.permission.ACCESS_COARSE_LOCATION)

  companion object {
    private val randomNumber = (0..999).random()
    private val EMAIL = "e2e.alert.$randomNumber@test.ch"
    private const val PASSWORD = "iLoveSwent1234!"
    private val NAME = "E2E Alert $randomNumber"
    private const val IMAGE_URL = ""
    private val DESCRIPTION = "I'm a test user $randomNumber for the alert end-to-end test"
    private const val DOB = "31/01/2001"
    private const val PREFERRED_DISTANCE = 500
    private val user =
        User(
            name = NAME,
            imageUrl = IMAGE_URL,
            description = DESCRIPTION,
            dob = DOB,
            preferredDistance = PREFERRED_DISTANCE,
        )

    private lateinit var supabaseClient: SupabaseClient
    private lateinit var authenticationViewModel: AuthenticationViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var alertViewModel: AlertViewModel

    private const val EDIT_ALERT_INDEX = 0
    private val PRODUCT = LIST_OF_PRODUCTS[1].textId // Pad
    private val PRODUCT_EDIT = LIST_OF_PRODUCTS[2].textId // No preference
    private val URGENCY = LIST_OF_URGENCIES[0].textId // High
    private val URGENCY_EDIT = LIST_OF_URGENCIES[2].textId // Low
    private const val MESSAGE = "I need pads urgently"
    private const val MESSAGE_EDIT = "I need a tampon or pad, please!"
  }

  @Before
  fun setUp() = runBlocking {
    supabaseClient =
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_KEY,
        ) {
          install(Auth)
          install(Postgrest)
          install(Storage)
        }
    val authenticationModel = AuthenticationModelSupabase(supabaseClient)
    authenticationViewModel = AuthenticationViewModel(authenticationModel)

    val userModel = UserRepositorySupabase(supabaseClient)
    userViewModel = UserViewModel(userModel)

    val alertModel = AlertModelSupabase(supabaseClient)
    alertViewModel = AlertViewModel(alertModel)

    authenticationViewModel.isUserLoggedIn(
        onSuccess = {
          Log.d(TAG, "setUp: user is already logged in")
          authenticationViewModel.logOut(
              onSuccess = { Log.d(TAG, "setUp: successfully logged out") },
              onFailure = { Log.d(TAG, "setUp: failed to log out: ${it.message}") },
          )
        },
        onFailure = { Log.d(TAG, "setUp: failed to check if user is logged in: ${it.message}") },
    )

    authenticationViewModel.signUpWithEmail(
        EMAIL,
        PASSWORD,
        onSuccess = {
          Log.d(TAG, "Successfully signed up with email and password")
          userViewModel.saveUser(
              user,
              onSuccess = {
                Log.d(TAG, "Successfully saved user")
                authenticationViewModel.logOut()
              },
              onFailure = { e: Exception -> Log.e(TAG, "Failed to save user: $e") },
          )
        },
        onFailure = { e: Exception -> Log.e(TAG, "Failed to sign up with email and password: $e") },
    )
  }

  @Test
  fun test() = run {
    step("User signs in") {
      composeTestRule.waitForIdle()
      composeTestRule.onNodeWithTag(SignInScreen.SCREEN).assertIsDisplayed()

      composeTestRule
          .onNodeWithTag(AuthenticationScreens.EMAIL_FIELD)
          .performScrollTo()
          .assertIsDisplayed()
          .performTextInput(EMAIL)
      composeTestRule
          .onNodeWithTag(AuthenticationScreens.PASSWORD_FIELD)
          .performScrollTo()
          .assertIsDisplayed()
          .performTextInput(PASSWORD)
      composeTestRule
          .onNodeWithTag(SignInScreen.SIGN_IN_BUTTON)
          .performScrollTo()
          .assertIsDisplayed()
          .performClick()
    }

    step("User navigates to CreateAlert screen") {
      composeTestRule.waitForIdle()
      composeTestRule.waitUntil(TIMEOUT) {
        try {
          composeTestRule.onNodeWithTag(ProfileScreen.SCREEN).assertIsDisplayed()
          true
        } catch (e: AssertionError) {
          false
        }
      }
      composeTestRule
          .onNodeWithTag(BottomNavigationMenu.BOTTOM_NAVIGATION_MENU_ITEM + "Alert")
          .assertIsDisplayed()
          .performClick()
    }

    step("User creates an alert") {
      composeTestRule.waitForIdle()
      composeTestRule.waitUntil(TIMEOUT) {
        try {
          composeTestRule.onNodeWithTag(CreateAlertScreen.SCREEN).assertIsDisplayed()
          true
        } catch (e: AssertionError) {
          false
        }
      }
      composeTestRule
          .onNodeWithTag(AlertInputs.PRODUCT_FIELD)
          .performScrollTo()
          .assertIsDisplayed()
          .performClick()
      composeTestRule.onNodeWithText(PRODUCT).performScrollTo().assertIsDisplayed().performClick()

      composeTestRule
          .onNodeWithTag(AlertInputs.URGENCY_FIELD)
          .performScrollTo()
          .assertIsDisplayed()
          .performClick()
      composeTestRule.onNodeWithText(URGENCY).performScrollTo().assertIsDisplayed().performClick()

      composeTestRule
          .onNodeWithTag(AlertInputs.LOCATION_FIELD)
          .performScrollTo()
          .assertIsDisplayed()
          .performClick()
      composeTestRule
          .onNodeWithTag(AlertInputs.DROPDOWN_ITEM + AlertInputs.CURRENT_LOCATION)
          .performScrollTo()
          .assertIsDisplayed()
          .performClick()
      composeTestRule
          .onNodeWithTag(AlertInputs.LOCATION_FIELD)
          .performScrollTo()
          .assertIsDisplayed()
          .assertTextContains(Location.CURRENT_LOCATION_NAME)

      composeTestRule
          .onNodeWithTag(AlertInputs.MESSAGE_FIELD)
          .performScrollTo()
          .assertIsDisplayed()
          .performTextInput(MESSAGE)

      composeTestRule
          .onNodeWithTag(CreateAlertScreen.SUBMIT_BUTTON)
          .performScrollTo()
          .assertIsDisplayed()
          .performClick()
    }

    step("User arrives at AlertLists screen and edits the first alert") {
      composeTestRule.waitForIdle()
      composeTestRule.waitUntil(TIMEOUT) {
        try {
          composeTestRule.onNodeWithTag(AlertListsScreen.SCREEN).assertIsDisplayed()
          true
        } catch (e: AssertionError) {
          false
        }
      }

      composeTestRule.onNodeWithTag(AlertListsScreen.MY_ALERTS_TAB).assertIsSelected()
      composeTestRule.onNodeWithTag(AlertListsScreen.PALS_ALERTS_TAB).assertIsNotSelected()

      composeTestRule
          .onNodeWithTag(
              MyAlertItem.MY_ALERT + EDIT_ALERT_INDEX) // only 1 alert in myAlerts (just created)
          .performScrollTo()
          .assertIsDisplayed()
          .assertHasNoClickAction()
      composeTestRule
          .onNodeWithTag(
              AlertListsScreen.ALERT_PROFILE_PICTURE + EDIT_ALERT_INDEX, useUnmergedTree = true)
          .performScrollTo()
          .assertIsDisplayed()

      composeTestRule
          .onNodeWithTag(
              AlertListsScreen.ALERT_TIME_AND_LOCATION + EDIT_ALERT_INDEX, useUnmergedTree = true)
          .performScrollTo()
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag(
              AlertListsScreen.ALERT_PRODUCT_AND_URGENCY + EDIT_ALERT_INDEX, useUnmergedTree = true)
          .performScrollTo()
          .assertIsDisplayed()

      composeTestRule
          .onNodeWithTag(
              AlertListsScreen.ALERT_PRODUCT_TYPE + EDIT_ALERT_INDEX, useUnmergedTree = true)
          .performScrollTo()
          .assertIsDisplayed()
          .assertContentDescriptionEquals(PRODUCT)
      composeTestRule
          .onNodeWithTag(AlertListsScreen.ALERT_URGENCY + EDIT_ALERT_INDEX, useUnmergedTree = true)
          .performScrollTo()
          .assertIsDisplayed()
          .assertContentDescriptionEquals(URGENCY)

      composeTestRule
          .onNodeWithTag(MyAlertItem.MY_EDIT_BUTTON + EDIT_ALERT_INDEX, useUnmergedTree = true)
          .performScrollTo()
          .assertIsDisplayed()
          .assertHasClickAction()
          .performClick()
    }

    step("User edits the alert") {
      composeTestRule.waitForIdle()
      composeTestRule.waitUntil(TIMEOUT) {
        try {
          composeTestRule.onNodeWithTag(EditAlertScreen.SCREEN).assertIsDisplayed()
          true
        } catch (e: AssertionError) {
          false
        }
      }

      composeTestRule.onNodeWithTag(AlertInputs.PRODUCT_FIELD).performScrollTo().performClick()
      composeTestRule.onNodeWithText(PRODUCT_EDIT).performScrollTo().performClick()

      composeTestRule.onNodeWithTag(AlertInputs.URGENCY_FIELD).performScrollTo().performClick()
      composeTestRule.onNodeWithText(URGENCY_EDIT).performScrollTo().performClick()

      composeTestRule
          .onNodeWithTag(AlertInputs.MESSAGE_FIELD)
          .performScrollTo()
          .performTextInput(MESSAGE_EDIT)

      composeTestRule.onNodeWithTag(EditAlertScreen.SAVE_BUTTON).performScrollTo().performClick()
    }

    step("User is back at AlertLists screen and deletes the first alert") {
      composeTestRule.waitForIdle()
      composeTestRule.waitUntil(TIMEOUT) {
        try {
          composeTestRule.onNodeWithTag(AlertListsScreen.SCREEN).assertIsDisplayed()
          true
        } catch (e: AssertionError) {
          false
        }
      }
      composeTestRule.onNodeWithTag(AlertListsScreen.MY_ALERTS_TAB).assertIsSelected()
      composeTestRule.onNodeWithTag(AlertListsScreen.PALS_ALERTS_TAB).assertIsNotSelected()

      composeTestRule
          .onNodeWithTag(MyAlertItem.MY_ALERT + EDIT_ALERT_INDEX)
          .performScrollTo()
          .assertIsDisplayed()
          .assertHasNoClickAction()
      composeTestRule
          .onNodeWithTag(
              AlertListsScreen.ALERT_PROFILE_PICTURE + EDIT_ALERT_INDEX, useUnmergedTree = true)
          .performScrollTo()
          .assertIsDisplayed()

      composeTestRule
          .onNodeWithTag(
              AlertListsScreen.ALERT_TIME_AND_LOCATION + EDIT_ALERT_INDEX, useUnmergedTree = true)
          .performScrollTo()
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag(
              AlertListsScreen.ALERT_PRODUCT_AND_URGENCY + EDIT_ALERT_INDEX, useUnmergedTree = true)
          .performScrollTo()
          .assertIsDisplayed()

      composeTestRule
          .onNodeWithTag(
              AlertListsScreen.ALERT_PRODUCT_TYPE + EDIT_ALERT_INDEX, useUnmergedTree = true)
          .performScrollTo()
          .assertIsDisplayed()
          .assertContentDescriptionEquals(PRODUCT_EDIT)
      composeTestRule
          .onNodeWithTag(AlertListsScreen.ALERT_URGENCY + EDIT_ALERT_INDEX, useUnmergedTree = true)
          .performScrollTo()
          .assertIsDisplayed()
          .assertContentDescriptionEquals(URGENCY_EDIT)

      composeTestRule
          .onNodeWithTag(MyAlertItem.MY_EDIT_BUTTON + EDIT_ALERT_INDEX, useUnmergedTree = true)
          .performScrollTo()
          .assertIsDisplayed()
          .assertHasClickAction()
          .performClick()
    }

    step("User deletes the alert") {
      composeTestRule.waitForIdle()
      composeTestRule.waitUntil(TIMEOUT) {
        try {
          composeTestRule.onNodeWithTag(EditAlertScreen.SCREEN).assertIsDisplayed()
          true
        } catch (e: AssertionError) {
          false
        }
      }

      composeTestRule
          .onNodeWithTag(EditAlertScreen.DELETE_BUTTON)
          .performScrollTo()
          .assertIsDisplayed()
          .performClick()
    }

    step("User is back at AlertLists screen and the alert is deleted") {
      composeTestRule.waitForIdle()
      composeTestRule.waitUntil(TIMEOUT) {
        try {
          composeTestRule.onNodeWithTag(AlertListsScreen.SCREEN).assertIsDisplayed()
          true
        } catch (e: AssertionError) {
          false
        }
      }

      composeTestRule.onNodeWithTag(AlertListsScreen.MY_ALERTS_TAB).assertIsSelected()
      composeTestRule.onNodeWithTag(AlertListsScreen.PALS_ALERTS_TAB).assertIsNotSelected()

      composeTestRule.onNodeWithTag(MyAlertItem.MY_ALERT + EDIT_ALERT_INDEX).assertDoesNotExist()
      composeTestRule.onNodeWithTag(AlertListsScreen.NO_ALERTS_CARD).assertIsDisplayed()
    }


  }
}
