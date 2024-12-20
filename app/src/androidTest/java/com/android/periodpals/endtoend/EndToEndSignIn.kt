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
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.android.periodpals.BuildConfig
import com.android.periodpals.MainActivity
import com.android.periodpals.model.authentication.AuthenticationModelSupabase
import com.android.periodpals.model.authentication.AuthenticationViewModel
import com.android.periodpals.model.user.User
import com.android.periodpals.model.user.UserRepositorySupabase
import com.android.periodpals.model.user.UserViewModel
import com.android.periodpals.resources.C.Tag.AuthenticationScreens
import com.android.periodpals.resources.C.Tag.AuthenticationScreens.SignInScreen
import com.android.periodpals.resources.C.Tag.ProfileScreens.ProfileScreen
import com.android.periodpals.resources.C.Tag.SettingsScreen
import com.android.periodpals.resources.C.Tag.TopAppBar
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

private const val TAG = "EndToEndSignIn"
private const val TIMEOUT = 60_000L

@RunWith(AndroidJUnit4::class)
class EndToEndSignIn : TestCase() {

  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()
  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS)
  private lateinit var supabaseClient: SupabaseClient
  private lateinit var authenticationViewModel: AuthenticationViewModel
  private lateinit var userViewModel: UserViewModel

  companion object {
    private val randomNumber = (0..999).random()
    private val email = "e2e.signin.$randomNumber@test.ch"
    private const val PASSWORD = "iLoveSwent1234!"
    private val name = "E2E SignIn $randomNumber"
    private const val IMAGE_URL = ""
    private val description = "I'm test user $randomNumber for the sign-in end-to-end test"
    private const val DOB = "31/01/2000"
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
          Log.d(TAG, "setUp: user is already logged in")
          authenticationViewModel.logOut(
              onSuccess = { Log.d(TAG, "setUp: successfully logged out") },
              onFailure = { Log.d(TAG, "setUp: failed to log out: ${it.message}") },
          )
        },
        onFailure = { Log.d(TAG, "setUp: failed to check if user is logged in: ${it.message}") },
    )

    authenticationViewModel.signUpWithEmail(
        email,
        PASSWORD,
        onSuccess = {
          Log.d(TAG, "Successfully signed up with email and password $randomNumber")
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
   * End-to-end test for the sign-in flow.
   * * The "user" lands on the SignIn screen and (correctly) fills in the fields. They click on the
   *   "Sign In" button and get redirected to the Profile screen that displays their information.
   * * The user then navigates to the Settings screen to delete their account and is redirected back
   *   to the SignIn screen.
   */
  @Test
  fun test() = run {
    step("User signs in") {
      composeTestRule.waitForIdle()
      composeTestRule.onNodeWithTag(SignInScreen.SCREEN).assertIsDisplayed()

      Log.d(TAG, "User arrives on SignIn Screen")
      composeTestRule
          .onNodeWithTag(AuthenticationScreens.EMAIL_FIELD)
          .performScrollTo()
          .assertIsDisplayed()
          .performTextInput(email)
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

    step("User arrives on Profile Screen") {
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

      Log.d(TAG, "User arrives on Profile Screen")
      composeTestRule
          .onNodeWithTag(ProfileScreen.DESCRIPTION_FIELD)
          .performScrollTo()
          .assertIsDisplayed()
          .assertTextEquals(description)
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
