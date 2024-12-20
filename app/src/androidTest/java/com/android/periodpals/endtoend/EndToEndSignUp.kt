package com.android.periodpals.endtoend

import android.Manifest
import android.util.Log
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.android.periodpals.BuildConfig
import com.android.periodpals.MainActivity
import com.android.periodpals.model.authentication.AuthenticationModelSupabase
import com.android.periodpals.model.authentication.AuthenticationViewModel
import com.android.periodpals.resources.C.Tag.AuthenticationScreens
import com.android.periodpals.resources.C.Tag.AuthenticationScreens.SignInScreen
import com.android.periodpals.resources.C.Tag.AuthenticationScreens.SignUpScreen
import com.android.periodpals.resources.C.Tag.ProfileScreens
import com.android.periodpals.resources.C.Tag.ProfileScreens.CreateProfileScreen
import com.android.periodpals.resources.C.Tag.ProfileScreens.ProfileScreen
import com.android.periodpals.resources.C.Tag.SettingsScreen
import com.android.periodpals.resources.C.Tag.TopAppBar
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val TAG = "EndToEndSignUp"
private const val TIMEOUT = 60_000L

@RunWith(AndroidJUnit4::class)
class EndToEndSignUp : TestCase() {

  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()
  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS)

  private lateinit var supabaseClient: SupabaseClient
  private lateinit var authenticationViewModel: AuthenticationViewModel

  companion object SignUpData {
    private val randomNumber = (0..999).random()
    private val EMAIL = "e2e.signup.$randomNumber@test.ch"
    private const val PASSWORD = "iLoveSwent1234!"
    private val NAME = "E2E SignUp $randomNumber"
    private val DESCRIPTION = "I'm test user $randomNumber for the sign-up end-to-end test"
    private const val DOB = "30/01/2001"
  }

  /**
   * Set up the Supabase client and the authentication view model. Check if the user is already
   * logged in and log them out if they are. Create a new account and its profile for the test.
   */
  @Before
  fun setUp() {

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
  }

  /**
   * End-to-end test for the sign-up flow.
   * * The "user" lands on the SignIn screen then navigates to the SignUp screen.
   * * They (correctly) fill in the fields and click on the "Sign Up" button and get redirected to
   *   the CreateProfile screen.
   * * They (correctly) fill in the fields and get redirected to the Profile screen that displays
   *   their information.
   * * The user then navigates to the Settings screen to delete their account and is redirected back
   *   to the SignIn screen.
   */
  @Test
  fun test() = run {
    step("User navigates to Sign Up Screen") {
      composeTestRule.waitForIdle()
      composeTestRule.onNodeWithTag(SignInScreen.SCREEN).assertIsDisplayed()

      Log.d(TAG, "User arrives on Sign In Screen")
      composeTestRule
          .onNodeWithTag(SignInScreen.NOT_REGISTERED_NAV_LINK)
          .performScrollTo()
          .assertIsDisplayed()
          .performClick()
    }

    step("User signs up") {
      composeTestRule.waitForIdle()
      composeTestRule.waitUntil(TIMEOUT) {
        try {
          composeTestRule.onAllNodesWithTag(SignUpScreen.SCREEN).fetchSemanticsNodes().size == 1
        } catch (e: AssertionError) {
          false
        }
      }

      Log.d(TAG, "User arrives on SignUp Screen")
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
          .onNodeWithTag(SignUpScreen.CONFIRM_PASSWORD_FIELD)
          .performScrollTo()
          .assertIsDisplayed()
          .performTextInput(PASSWORD)
      Espresso.closeSoftKeyboard()
      composeTestRule
          .onNodeWithTag(SignUpScreen.SIGN_UP_BUTTON)
          .performScrollTo()
          .assertIsDisplayed()
          .performClick()
    }

    step("User creates their profile") {
      composeTestRule.waitForIdle()
      composeTestRule.waitUntil(TIMEOUT) {
        try {
          composeTestRule
              .onAllNodesWithTag(CreateProfileScreen.SCREEN)
              .fetchSemanticsNodes()
              .size == 1
        } catch (e: AssertionError) {
          false
        }
      }
      composeTestRule.onNodeWithTag(CreateProfileScreen.SCREEN).assertIsDisplayed()

      Log.d(TAG, "User arrives on Create Profile Screen")
      composeTestRule
          .onNodeWithTag(ProfileScreens.NAME_INPUT_FIELD)
          .performScrollTo()
          .assertIsDisplayed()
          .performTextInput(NAME)
      composeTestRule
          .onNodeWithTag(ProfileScreens.DOB_INPUT_FIELD)
          .performScrollTo()
          .assertIsDisplayed()
          .performTextInput(DOB)
      composeTestRule
          .onNodeWithTag(ProfileScreens.DESCRIPTION_INPUT_FIELD)
          .performScrollTo()
          .assertIsDisplayed()
          .performTextInput(DESCRIPTION)
      // Keep default preferred radius for receiving pal's alerts
      Espresso.closeSoftKeyboard()
      composeTestRule
          .onNodeWithTag(ProfileScreens.SAVE_BUTTON)
          .performScrollTo()
          .assertIsDisplayed()
          .performClick()
    }

    step("User arrives on Profile Screen") {
      composeTestRule.waitForIdle()
      composeTestRule.waitUntil(TIMEOUT) {
        try {
          composeTestRule
              .onNodeWithTag(ProfileScreen.NAME_FIELD)
              .performScrollTo()
              .assertIsDisplayed()
              .assertTextEquals(NAME)
          true
        } catch (e: AssertionError) {
          false
        }
      }
      composeTestRule.onNodeWithTag(ProfileScreen.SCREEN).assertIsDisplayed()

      Log.d(TAG, "User arrives on Profile Screen")
      composeTestRule
          .onNodeWithTag(ProfileScreen.NAME_FIELD)
          .performScrollTo()
          .assertIsDisplayed()
          .assertTextEquals(NAME)
      composeTestRule
          .onNodeWithTag(ProfileScreen.DESCRIPTION_FIELD)
          .performScrollTo()
          .assertIsDisplayed()
          .assertTextEquals(DESCRIPTION)
    }

    step("User navigates to Settings Screen to delete their account") {
      composeTestRule.onNodeWithTag(TopAppBar.SETTINGS_BUTTON).assertIsDisplayed().performClick()

      composeTestRule.waitForIdle()
      composeTestRule.waitUntil(TIMEOUT) {
        try {
          composeTestRule.onAllNodesWithTag(SettingsScreen.SCREEN).fetchSemanticsNodes().size == 1
        } catch (e: AssertionError) {
          false
        }
      }
      composeTestRule.onNodeWithTag(SettingsScreen.SCREEN).assertIsDisplayed()

      Log.d(TAG, "User arrives on Settings Screen")
      composeTestRule
          .onNodeWithTag(SettingsScreen.DELETE_ACCOUNT_ICON)
          .performScrollTo()
          .assertIsDisplayed()
          .performClick()
      composeTestRule.onNodeWithTag(SettingsScreen.DELETE_BUTTON).assertIsDisplayed().performClick()
    }

    step("User is lead back to the Sign In Screen") {
      composeTestRule.waitForIdle()
      composeTestRule.waitUntil(TIMEOUT) {
        try {
          composeTestRule.onAllNodesWithTag(SignInScreen.SCREEN).fetchSemanticsNodes().size == 1
        } catch (e: AssertionError) {
          false
        }
      }
    }
  }
}
