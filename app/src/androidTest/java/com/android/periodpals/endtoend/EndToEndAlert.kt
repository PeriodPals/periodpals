package com.android.periodpals.endtoend

// import com.android.periodpals.BuildConfig
import android.Manifest
import android.util.Log
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
import com.android.periodpals.MainActivity
import com.android.periodpals.model.alert.AlertViewModel
import com.android.periodpals.model.alert.LIST_OF_PRODUCTS
import com.android.periodpals.model.alert.LIST_OF_URGENCIES
import com.android.periodpals.model.authentication.AuthenticationViewModel
import com.android.periodpals.model.location.Location
import com.android.periodpals.model.user.User
import com.android.periodpals.model.user.UserViewModel
import com.android.periodpals.resources.C
import com.android.periodpals.resources.C.Tag.AlertInputs
import com.android.periodpals.resources.C.Tag.AlertListsScreen
import com.android.periodpals.resources.C.Tag.AlertListsScreen.MY_ALERTS_TAB
import com.android.periodpals.resources.C.Tag.AlertListsScreen.MyAlertItem
import com.android.periodpals.resources.C.Tag.AuthenticationScreens
import com.android.periodpals.resources.C.Tag.BottomNavigationMenu
import com.android.periodpals.resources.C.Tag.CreateAlertScreen
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private const val TAG = "EndToEndAlert"

class EndToEndAlert : TestCase() {
  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()
  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS)

  companion object {
    private const val EMAIL = "end2end.signin@test.ch"
    private const val PASSWORD = "iLoveSwent1234!"
    private const val NAME = "End2EndSignIn"
    private const val IMAGE_URL = ""
    private const val DESCRIPTION = "I'm a test user"
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

    private val PRODUCT = LIST_OF_PRODUCTS[1].textId // Pad
    private val PRODUCT_EDIT = LIST_OF_PRODUCTS[2].textId // No preference
    private val URGENCY = LIST_OF_URGENCIES[0].textId // High
    private val URGENCY_EDIT = LIST_OF_URGENCIES[2].textId // Low
    private const val MESSAGE = "I need pads urgently"
    private const val MESSAGE_EDIT = "I need a tampon or pad, please!"
  }

  @Before
  fun setUp() =
      runBlocking {
        //        supabaseClient =
        //            createSupabaseClient(
        //                supabaseUrl = BuildConfig.SUPABASE_URL,
        //                supabaseKey = BuildConfig.SUPABASE_KEY,
        //            ) {
        //                install(Auth)
        //                install(Postgrest)
        //                install(Storage)
        //            }
        //        val authenticationModel = AuthenticationModelSupabase(supabaseClient)
        //        authenticationViewModel = AuthenticationViewModel(authenticationModel)
        //        val userModel = UserRepositorySupabase(supabaseClient)
        //        userViewModel = UserViewModel(userModel)
        //
        //        authenticationViewModel.signUpWithEmail(
        //            EMAIL,
        //            PASSWORD,
        //            onSuccess = {
        //                Log.d(TAG, "Successfully signed up with email and password")
        //                userViewModel.saveUser(
        //                    user,
        //                    onSuccess = { Log.d(TAG, "Successfully saved user") },
        //                    onFailure = { e: Exception -> Log.e(TAG, "Failed to save user: $e") },
        //                )
        //            },
        //            onFailure = { e: Exception -> Log.e(TAG, "Failed to sign up with email and
        // password: $e") },
        //        )
      }

  @Test
  fun test() = run {
    step("User signs in") {
      composeTestRule.waitForIdle()
      Log.d(TAG, "User arrives on Sign In Screen")
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
          .onNodeWithTag(AuthenticationScreens.SignInScreen.SIGN_IN_BUTTON)
          .performScrollTo()
          .assertIsDisplayed()
          .performClick()
    }
    step("User navigates to CreateAlert screen") {
      composeTestRule.waitForIdle()
      composeTestRule
          .onNodeWithTag(BottomNavigationMenu.BOTTOM_NAVIGATION_MENU_ITEM + "Alert")
          .assertIsDisplayed()
          .performClick()
    }

    step("User creates an alert") {
      composeTestRule.waitForIdle()
      composeTestRule.onNodeWithTag(CreateAlertScreen.SCREEN).assertIsDisplayed()

      composeTestRule.onNodeWithTag(AlertInputs.PRODUCT_FIELD).performScrollTo().performClick()
      composeTestRule.onNodeWithText(PRODUCT).performScrollTo().performClick()

      composeTestRule.onNodeWithTag(AlertInputs.URGENCY_FIELD).performScrollTo().performClick()
      composeTestRule.onNodeWithText(URGENCY).performScrollTo().performClick()

      composeTestRule.onNodeWithTag(AlertInputs.LOCATION_FIELD).performScrollTo().performClick()
      composeTestRule
          .onNodeWithTag(AlertInputs.DROPDOWN_ITEM + AlertInputs.CURRENT_LOCATION)
          .performScrollTo()
          .performClick()
      composeTestRule
          .onNodeWithTag(AlertInputs.LOCATION_FIELD)
          .performScrollTo()
          .assertTextContains(Location.CURRENT_LOCATION_NAME)

      composeTestRule
          .onNodeWithTag(AlertInputs.MESSAGE_FIELD)
          .performScrollTo()
          .performTextInput(MESSAGE)

      composeTestRule
          .onNodeWithTag(CreateAlertScreen.SUBMIT_BUTTON)
          .performScrollTo()
          .performClick()

      composeTestRule.waitForIdle()
    }

    val alertId = alertViewModel.myAlerts.value[0].id

    step("User arrives at AlertLists screen") {
      composeTestRule.waitForIdle()
      composeTestRule.onNodeWithTag(AlertListsScreen.SCREEN).assertIsDisplayed()
      composeTestRule.onNodeWithTag(AlertListsScreen.MY_ALERTS_TAB).assertIsSelected()
      composeTestRule.onNodeWithTag(AlertListsScreen.PALS_ALERTS_TAB).assertIsNotSelected()

      composeTestRule
          .onNodeWithTag(MyAlertItem.MY_ALERT + alertId) // only 1 alert in myAlerts (just created)
          .performScrollTo()
          .assertIsDisplayed()
          .assertHasNoClickAction()

      // TODO: assert all the components of the my alert card are CORRECT?
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

      composeTestRule
          .onNodeWithTag(MyAlertItem.MY_EDIT_BUTTON + alertId)
          .performScrollTo()
          .performClick()
    }

    step("User edits the alert") {
      composeTestRule.waitForIdle()
      composeTestRule.onNodeWithTag(C.Tag.EditAlertScreen.SCREEN).assertIsDisplayed()

      composeTestRule.onNodeWithTag(AlertInputs.PRODUCT_FIELD).performScrollTo().performClick()
      composeTestRule.onNodeWithText(PRODUCT_EDIT).performScrollTo().performClick()

      composeTestRule.onNodeWithTag(AlertInputs.URGENCY_FIELD).performScrollTo().performClick()
      composeTestRule.onNodeWithText(URGENCY_EDIT).performScrollTo().performClick()

      composeTestRule
          .onNodeWithTag(AlertInputs.MESSAGE_FIELD)
          .performScrollTo()
          .performTextInput(MESSAGE_EDIT)

      composeTestRule
          .onNodeWithTag(C.Tag.EditAlertScreen.SAVE_BUTTON)
          .performScrollTo()
          .performClick()

      composeTestRule.waitForIdle()
      composeTestRule.onNodeWithTag(AlertListsScreen.SCREEN).assertIsDisplayed()
      composeTestRule.onNodeWithTag(MY_ALERTS_TAB).assertIsSelected()

      composeTestRule.onNodeWithTag(MyAlertItem.MY_ALERT + alertId).assertIsDisplayed()

      // TODO: assert that the new information is displayed in my alert card
    }

    step("User deletes the alert") {
      composeTestRule
          .onNodeWithTag(MyAlertItem.MY_EDIT_BUTTON + alertId)
          .performScrollTo()
          .assertIsDisplayed()
          .performClick()

      composeTestRule
          .onNodeWithTag(C.Tag.EditAlertScreen.DELETE_BUTTON)
          .performScrollTo()
          .performClick()

      composeTestRule.waitForIdle()
      composeTestRule.onNodeWithTag(AlertListsScreen.SCREEN).assertIsDisplayed()
      composeTestRule.onNodeWithTag(MY_ALERTS_TAB).assertIsSelected()

      composeTestRule.onNodeWithTag(MyAlertItem.MY_ALERT + alertId).assertDoesNotExist()
    }
  }
}
