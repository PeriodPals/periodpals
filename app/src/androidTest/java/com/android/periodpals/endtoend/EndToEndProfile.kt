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
import androidx.compose.ui.test.performTextClearance
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
import com.android.periodpals.resources.C.Tag.ProfileScreens
import com.android.periodpals.resources.C.Tag.ProfileScreens.EditProfileScreen
import com.android.periodpals.resources.C.Tag.ProfileScreens.ProfileScreen
import com.android.periodpals.resources.C.Tag.SettingsScreen
import com.android.periodpals.resources.C.Tag.TopAppBar
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val TAG = "EndToEndProfile"
private const val TIMEOUT = 60_000L

@RunWith(AndroidJUnit4::class)
class EndToEndProfile : TestCase() {

  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()
  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS)
  private lateinit var supabaseClient: SupabaseClient
  private lateinit var authenticationViewModel: AuthenticationViewModel
  private lateinit var userViewModel: UserViewModel

  companion object {
    private val randomNumber = (0..999).random()
    private val EMAIL = "e2e.profile.$randomNumber@test.ch"
    private const val PASSWORD = "iLoveSwent1234!"
    private val name = "E2E Profile $randomNumber"
    private const val IMAGE_URL = ""
    private val description = "I'm test user $randomNumber for the profile end-to-end test"
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
    private const val EDIT_NAME = "E2E Profile Edit Prime"
    private const val EDIT_DESCRIPTION = "I'm test user Prime for the profile end-to-end test"
    private const val EDIT_DOB = "31/01/1999"
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
   * End-to-end test for the edit profile flow.
   * * The "user" lands on the SignIn screen and (correctly) fills in the fields. They click on the
   *   "Sign In" button and get redirected to the Profile screen that displays their information.
   * * The user then navigates to the Edit Profile screen and (correctly) fills in the fields. They
   *   click on the "Save" button and get redirected back to the Profile screen that displays their
   *   updated information.
   * * The user then navigates to the Settings screen to delete their account and is redirected back
   *   to the SignIn screen.
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

      composeTestRule.onNodeWithTag(TopAppBar.EDIT_BUTTON).assertIsDisplayed().performClick()
    }

    step("User edits their profile and saves") {
      composeTestRule.waitForIdle()
      while (composeTestRule
          .onAllNodesWithTag(EditProfileScreen.SCREEN)
          .fetchSemanticsNodes()
          .size != 1) {
        TimeUnit.SECONDS.sleep(1)
      }

      composeTestRule
          .onNodeWithTag(ProfileScreens.NAME_INPUT_FIELD)
          .performScrollTo()
          .assertIsDisplayed()
          .performTextClearance()
      composeTestRule
          .onNodeWithTag(ProfileScreens.NAME_INPUT_FIELD)
          .performScrollTo()
          .assertIsDisplayed()
          .performTextInput(EDIT_NAME)
      composeTestRule
          .onNodeWithTag(ProfileScreens.DOB_INPUT_FIELD)
          .performScrollTo()
          .assertIsDisplayed()
          .performTextClearance()
      composeTestRule
          .onNodeWithTag(ProfileScreens.DOB_INPUT_FIELD)
          .performScrollTo()
          .assertIsDisplayed()
          .performTextInput(EDIT_DOB)

      composeTestRule
          .onNodeWithTag(ProfileScreens.DESCRIPTION_INPUT_FIELD)
          .performScrollTo()
          .assertIsDisplayed()
          .performTextClearance()
      composeTestRule
          .onNodeWithTag(ProfileScreens.DESCRIPTION_INPUT_FIELD)
          .performScrollTo()
          .assertIsDisplayed()
          .performTextInput(EDIT_DESCRIPTION)

      composeTestRule
          .onNodeWithTag(ProfileScreens.SAVE_BUTTON)
          .performScrollTo()
          .assertIsDisplayed()
          .performClick()
    }

    step("User arrives back on Profile Screen") {
      composeTestRule.waitForIdle()
      composeTestRule.waitUntil(TIMEOUT) {
        try {
          composeTestRule
              .onNodeWithTag(ProfileScreen.NAME_FIELD)
              .performScrollTo()
              .assertIsDisplayed()
              .assertTextEquals(EDIT_NAME)
          true
        } catch (e: AssertionError) {
          false
        }
      }

      composeTestRule
          .onNodeWithTag(ProfileScreen.DESCRIPTION_FIELD)
          .performScrollTo()
          .assertIsDisplayed()
          .assertTextEquals(EDIT_DESCRIPTION)
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
