package com.android.periodpals.endtoend

import android.Manifest
import android.os.SystemClock
import android.util.Log
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.android.periodpals.BuildConfig
import com.android.periodpals.MainActivity
import com.android.periodpals.model.authentication.AuthenticationModelSupabase
import com.android.periodpals.model.authentication.AuthenticationViewModel
import com.android.periodpals.model.timer.HOUR
import com.android.periodpals.model.timer.MINUTE
import com.android.periodpals.model.timer.SECOND
import com.android.periodpals.model.user.User
import com.android.periodpals.model.user.UserRepositorySupabase
import com.android.periodpals.model.user.UserViewModel
import com.android.periodpals.resources.C.Tag.AuthenticationScreens
import com.android.periodpals.resources.C.Tag.AuthenticationScreens.SignInScreen
import com.android.periodpals.resources.C.Tag.BottomNavigationMenu
import com.android.periodpals.resources.C.Tag.ProfileScreens.ProfileScreen
import com.android.periodpals.resources.C.Tag.SettingsScreen
import com.android.periodpals.resources.C.Tag.TimerScreen
import com.android.periodpals.resources.C.Tag.TopAppBar
import com.android.periodpals.ui.navigation.TopLevelDestinations.PROFILE
import com.android.periodpals.ui.navigation.TopLevelDestinations.TIMER
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
import org.junit.runner.RunWith

private const val TAG = "EndToEndTimer"
private const val TIMEOUT = 60_000L

@RunWith(AndroidJUnit4::class)
class EndToEndTimer : TestCase() {
  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()
  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS)
  private lateinit var supabaseClient: SupabaseClient
  private lateinit var authenticationViewModel: AuthenticationViewModel
  private lateinit var userViewModel: UserViewModel

  companion object {
    private val randomNumber = (0..999).random()
    private val EMAIL = "e2e.timer.$randomNumber@test.ch"
    private const val PASSWORD = "iLoveSwent1234!"
    private val name = "E2E Timer $randomNumber"
    private const val IMAGE_URL = ""
    private val description = "I am a test user $randomNumber for the timer end-to-end test"
    private const val DOB = "31/01/1998"
    private const val PREFERRED_DISTANCE = 500
    private val user =
        User(
            name = name,
            imageUrl = IMAGE_URL,
            description = description,
            dob = DOB,
            preferredDistance = PREFERRED_DISTANCE,
        )
  }

  /**
   * Set up the Supabase client and the authentication view model. Check if the user is already
   * logged in and log them out if they are. Create a new account and its profile for the test.
   */
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

    authenticationViewModel.isUserLoggedIn(
        onSuccess = {
          Log.d(TAG, "User is already logged in")
          authenticationViewModel.logOut(
              onSuccess = { Log.d(TAG, "Successfully logged out previous user") },
              onFailure = { e: Exception -> Log.e(TAG, "Failed to log out previous user: $e") },
          )
        },
        onFailure = { e: Exception -> Log.e(TAG, "Failed to check if user is logged in: $e") },
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

  /**
   * End-to-end test for the timer flow.
   *
   * The "user" lands on the SignIn screen then navigates to the SignUp screen. They (correctly)
   * fill in the fields and click on the "Sign Up" button and get redirected to the CreateProfile
   * screen. They (correctly) fill in the fields and get redirected to the Profile screen that
   * displays the info they just entered. They navigate to the Timer screen and start the timer.
   * They stop the timer, start it again, and reset it.
   */
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

    step("User arrives on Profile Screen and navigates to Edit Profile Screen") {
      composeTestRule.waitForIdle()
      composeTestRule.waitUntil(TIMEOUT) {
        try {
          composeTestRule
              .onNodeWithTag(ProfileScreen.NAME_FIELD)
              .performScrollTo()
              .assertIsDisplayed()
              .assertTextEquals(name)
          true
        } catch (e: AssertionError) {
          false
        }
      }

      composeTestRule
          .onNodeWithTag(ProfileScreen.DESCRIPTION_FIELD)
          .performScrollTo()
          .assertIsDisplayed()
          .assertTextEquals(description)

      composeTestRule
          .onNodeWithTag(BottomNavigationMenu.BOTTOM_NAVIGATION_MENU_ITEM + TIMER.textId)
          .assertIsDisplayed()
          .performClick()
      composeTestRule.waitUntil(TIMEOUT) {
        composeTestRule.onAllNodesWithTag(TimerScreen.SCREEN).fetchSemanticsNodes().size == 1
      }
    }

    step("User starts the timer, stops it, starts it again, and resets it") {
      composeTestRule.waitForIdle()
      composeTestRule
          .onNodeWithTag(TimerScreen.START_BUTTON)
          .performScrollTo()
          .assertIsDisplayed()
          .performClick()
      SystemClock.setCurrentTimeMillis(System.currentTimeMillis() + 6L * HOUR + 30L * MINUTE)
      composeTestRule
          .onNodeWithTag(TimerScreen.STOP_BUTTON)
          .performScrollTo()
          .assertIsDisplayed()
          .performClick()

      composeTestRule.waitForIdle()
      composeTestRule
          .onNodeWithTag(TimerScreen.START_BUTTON)
          .performScrollTo()
          .assertIsDisplayed()
          .performClick()
      SystemClock.setCurrentTimeMillis(System.currentTimeMillis() + 5 * SECOND)
      composeTestRule
          .onNodeWithTag(TimerScreen.RESET_BUTTON)
          .performScrollTo()
          .assertIsDisplayed()
          .performClick()
    }

    step("Navigate back to Profile Screen") {
      composeTestRule.waitForIdle()
      composeTestRule
          .onNodeWithTag(BottomNavigationMenu.BOTTOM_NAVIGATION_MENU_ITEM + PROFILE.textId)
          .assertIsDisplayed()
          .performClick()
      composeTestRule.waitUntil(TIMEOUT) {
        try {
          composeTestRule
              .onNodeWithTag(ProfileScreen.NAME_FIELD)
              .performScrollTo()
              .assertIsDisplayed()
              .assertTextEquals(name)
          true
        } catch (e: AssertionError) {
          false
        }
      }
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
