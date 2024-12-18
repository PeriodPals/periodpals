package com.android.periodpals.endtoend

import android.Manifest
import android.util.Log
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
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
import com.android.periodpals.ui.authentication.SignInScreen
import com.android.periodpals.ui.navigation.NavigationActions
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock

private const val TAG = "EndToEndSignIn"
private const val TIMEOUT = 10_000L

@RunWith(AndroidJUnit4::class)
class EndToEndSignIn : TestCase() {

  @get:Rule val composeTestRule = createComposeRule()
  @get:Rule val activityRule = ActivityTestRule(MainActivity::class.java)
  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS)
  private lateinit var supabaseClient: SupabaseClient
  private lateinit var authenticationViewModel: AuthenticationViewModel
  private lateinit var userViewModel: UserViewModel
  private lateinit var navigationActions: NavigationActions

  companion object {
    private val randomNumber = (0..999).random()
    private val EMAIL = "e2e.signin.$randomNumber@test.ch"
    private const val PASSWORD = "iLoveSwent1234!"
    private val NAME = "E2E SignIn $randomNumber"
    private const val IMAGE_URL = ""
    private val DESCRIPTION = "I'm test user $randomNumber for the sign-in end-to-end test"
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
  }

  /**
   * Set up the Supabase client, view models, and user data for the test. It creates a new auth
   * user, gets the uid, and creates its profile.
   */
  @Before
  fun setUp() = runBlocking {
    navigationActions = mock(NavigationActions::class.java)

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

    authenticationViewModel.signUpWithEmail(
        EMAIL,
        PASSWORD,
        onSuccess = {
          Log.d(TAG, "Successfully signed up with email and password")
          userViewModel.saveUser(user)
          authenticationViewModel.loadAuthenticationUserData()
          authenticationViewModel.logOut()
        },
        onFailure = { e: Exception -> Log.e(TAG, "Failed to sign up with email and password: $e") },
    )
  }

  /**
   * Tear down the test by deleting the user profile from the database. Thanks to the
   * `delete_auth_users` edge function, it will also delete the auth user and its associated data.
   */
  @After
  fun tearDown() = runBlocking {
    userViewModel.deleteUser(idUser = authenticationViewModel.authUserData.value?.uid ?: "")
  }

  /**
   * End-to-end test for the
   * [sign-in flow](https://www.figma.com/design/r6jgyWnwTQ6e5X1eLpeHwN/PeriodsPals?node-id=579-5989&node-type=canvas&m=dev).
   *
   * The "user" lands on the Sign In Screen and (correctly) fill in their info. They click on the
   * "Sign In" button and get redirected to the Profile Screen that displays their information.
   */
  @Test
  fun test() = run {
    step("Set up Sign In Screen") {
      Log.d(TAG, "Setting up Sign In Screen")
      composeTestRule.setContent { SignInScreen(authenticationViewModel, navigationActions) }
    }

    step("User signs in") {
      composeTestRule.waitForIdle()
      composeTestRule.onNodeWithTag(SignInScreen.SCREEN).assertExists()

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
              .assertTextEquals(NAME)
          true
        } catch (e: AssertionError) {
          false
        }
      }

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
  }
}
