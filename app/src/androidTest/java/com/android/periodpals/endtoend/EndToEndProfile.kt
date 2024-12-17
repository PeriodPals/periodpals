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
import com.android.periodpals.resources.C.Tag.TopAppBar
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

private const val TAG = "EndToEndProfile"
private const val TIMEOUT = 60000L // 60 seconds, adjust for slower devices, networks and CI

@RunWith(AndroidJUnit4::class)
class EndToEndProfile : TestCase() {

  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()
  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS)

  companion object {
    private val randomNumber = (0..999).random()
    private val EMAIL = "e2e2.profile.$randomNumber@test"
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

    private lateinit var supabaseClient: SupabaseClient
    private lateinit var authenticationViewModel: AuthenticationViewModel
    private lateinit var userViewModel: UserViewModel
  }

  /**
   * Set up the Supabase client, view models, and user data for the test. It creates a new auth
   * user, gets the uid, creates its profile, and logs out. Sets the content to the MainActivity.
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

    composeTestRule.setContent { MainActivity() }
  }

  @After
  fun tearDown() = runBlocking {
    composeTestRule.activityRule.scenario.onActivity { activity -> activity.finish() }

    authenticationViewModel.loadAuthenticationUserData(
        onSuccess = {
          Log.d(TAG, "Successfully loaded user data")
          userViewModel.deleteUser(
              idUser = authenticationViewModel.authUserData.value?.uid ?: "",
              onSuccess = { Log.d(TAG, "Successfully deleted user") },
              onFailure = { e: Exception -> Log.e(TAG, "Failed to delete user: $e") },
          )
        },
        onFailure = { e: Exception -> Log.e(TAG, "Failed to load user data: $e") },
    )
  }

  /**
   * End-to-end test for the
   * [edit profile flow](https://www.figma.com/design/r6jgyWnwTQ6e5X1eLpeHwN/PeriodsPals?node-id=579-5989&node-type=canvas&m=dev)
   *
   * The "user" lands on the SignIn screen then signs in to their existing account. They click on
   * the edit button in the top app bar and gets redirected to the EditProfile screen. They
   * (correctly) fill in the fields and click on the "Save" button and get redirected to the Profile
   * screen that displays the info they just entered.
   */
  @Test
  fun editProfileEndToEnd() {

    // SignIn Screen
    // User signs in using existing account
    composeTestRule.waitForIdle()
    Log.d(TAG, "User arrives on SignIn Screen")
    composeTestRule.onNodeWithTag(SignInScreen.SCREEN).assertExists()
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
    composeTestRule.waitUntil(TIMEOUT) {
      composeTestRule.onAllNodesWithTag(ProfileScreen.SCREEN).fetchSemanticsNodes().size == 1
    }

    // Profile Screen
    // User arrives on their profile
    composeTestRule.waitForIdle()
    Log.d(TAG, "User arrives on Profile Screen")
    composeTestRule.onNodeWithTag(TopAppBar.EDIT_BUTTON).assertIsDisplayed().performClick()
    composeTestRule.waitUntil(TIMEOUT) {
      composeTestRule.onAllNodesWithTag(EditProfileScreen.SCREEN).fetchSemanticsNodes().size == 1
    }

    // Edit Profile Screen
    // User edits their profile
    composeTestRule.waitForIdle()
    Log.d(TAG, "User arrives on Edit Profile Screen")
    composeTestRule
        .onNodeWithTag(ProfileScreens.NAME_INPUT_FIELD)
        .performScrollTo()
        .assertIsDisplayed()
        .performTextClearance()
    composeTestRule
        .onNodeWithTag(ProfileScreens.NAME_INPUT_FIELD)
        .performScrollTo()
        .assertIsDisplayed()
        .performTextInput(name)
    composeTestRule
        .onNodeWithTag(ProfileScreens.DOB_INPUT_FIELD)
        .performScrollTo()
        .assertIsDisplayed()
        .performTextClearance()
    composeTestRule
        .onNodeWithTag(ProfileScreens.DOB_INPUT_FIELD)
        .performScrollTo()
        .assertIsDisplayed()
        .performTextInput(DOB)
    composeTestRule
        .onNodeWithTag(ProfileScreens.DESCRIPTION_INPUT_FIELD)
        .performScrollTo()
        .assertIsDisplayed()
        .performTextClearance()
    composeTestRule
        .onNodeWithTag(ProfileScreens.DESCRIPTION_INPUT_FIELD)
        .performScrollTo()
        .assertIsDisplayed()
        .performTextInput(description)
    composeTestRule
        .onNodeWithTag(ProfileScreens.SAVE_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
        .performClick()
    composeTestRule.waitUntil(TIMEOUT) {
      composeTestRule.onAllNodesWithTag(ProfileScreen.SCREEN).fetchSemanticsNodes().size == 1
    }

    // Profile Screen
    // User arrives on Profile Screen and see their new data displayed
    composeTestRule.waitForIdle()
    composeTestRule.waitUntil(
        TIMEOUT) { // need to wait because very first recomposition has not fetched data yet
          try { // trick waitUntil into thinking this counts as a SemanticNodeInteraction
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
        .onNodeWithTag(ProfileScreen.NAME_FIELD)
        .performScrollTo()
        .assertExists()
        .assertTextEquals(name)
    composeTestRule
        .onNodeWithTag(ProfileScreen.DESCRIPTION_FIELD)
        .performScrollTo()
        .assertExists()
        .assertTextEquals(description)
    composeTestRule
        .onNodeWithTag(ProfileScreen.NO_REVIEWS_CARD)
        .performScrollTo()
        .assertExists() // TODO: change once implemented the reviews
    composeTestRule
        .onNodeWithTag(ProfileScreen.CONTRIBUTION_FIELD)
        .performScrollTo()
        .assertIsDisplayed()
        .assertTextEquals("New user") // TODO: change once implemented the statuses
  }
}
