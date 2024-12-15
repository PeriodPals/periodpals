package com.android.periodpals.endtoend

import android.Manifest
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
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
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import java.util.concurrent.TimeUnit
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val TAG = "EndToEndSignIn"

@RunWith(AndroidJUnit4::class)
class EndToEndSignIn : TestCase() {

  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()
  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS)

  companion object {
    private var uid = mutableStateOf<String?>(null)
    private const val EMAIL = "end2end.signin@test.ch"
    private const val PASSWORD = "iLoveSwent1234!"
    private const val NAME = "End2EndSignIn"
    private const val IMAGE_URL = ""
    private const val DESCRIPTION = "I'm a test user for the sign-in end-to-end test"
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
  }

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
    uid = mutableStateOf(authenticationViewModel.authUserData.value?.uid)
    val userModel = UserRepositorySupabase(supabaseClient)
    userViewModel = UserViewModel(userModel)

    authenticationViewModel.signUpWithEmail(
        EMAIL,
        PASSWORD,
        onSuccess = {
          Log.d(TAG, "Successfully signed up with email and password")
          authenticationViewModel.loadAuthenticationUserData(
              onSuccess = {
                userViewModel.saveUser(
                    user,
                    onSuccess = { Log.d(TAG, "Successfully saved user") },
                    onFailure = { e: Exception -> Log.e(TAG, "Failed to save user: $e") },
                )
              },
              onFailure = { e: Exception -> Log.e(TAG, "Failed to load user data: $e") },
          )
        },
        onFailure = { e: Exception -> Log.e(TAG, "Failed to sign up with email and password: $e") },
    )
  }

  @After
  fun tearDown() {
    userViewModel.deleteUser(
        idUser = uid.value ?: "",
        onSuccess = { Log.d(TAG, "Successfully deleted user") },
        onFailure = { e: Exception -> Log.e(TAG, "Failed to delete user with exception: $e") },
    )
  }

  @Test
  fun signInEndToEnd() = run {
    step("User signs in") {
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

    //    composeTestRule.waitForIdle()
    //    Log.d(TAG, "User arrives on SignIn Screen")
    //    composeTestRule.onNodeWithTag(SignInScreen.SCREEN).assertIsDisplayed()
    //    composeTestRule
    //        .onNodeWithTag(AuthenticationScreens.EMAIL_FIELD)
    //        .performScrollTo()
    //        .assertIsDisplayed()
    //        .performTextInput(EMAIL)
    //    composeTestRule
    //        .onNodeWithTag(AuthenticationScreens.PASSWORD_FIELD)
    //        .performScrollTo()
    //        .assertIsDisplayed()
    //        .performTextInput(PASSWORD)
    //    composeTestRule
    //        .onNodeWithTag(SignInScreen.SIGN_IN_BUTTON)
    //        .performScrollTo()
    //        .assertIsDisplayed()
    //        .performClick()

    step("User arrives on Profile Screen") {
      composeTestRule.waitForIdle()
      while (userViewModel.user.value == null) {
        TimeUnit.SECONDS.sleep(1)
      }
      Log.d(TAG, "User arrives on Profile Screen")
      composeTestRule.onNodeWithTag(ProfileScreen.SCREEN).assertIsDisplayed()
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

    //    composeTestRule.waitForIdle()
    //    while (userViewModel.user.value == null) {
    //      TimeUnit.SECONDS.sleep(1)
    //    }
    //    Log.d(TAG, "User arrives on Profile Screen")
    //    composeTestRule.onNodeWithTag(ProfileScreen.SCREEN).assertIsDisplayed()
    //    composeTestRule
    //        .onNodeWithTag(ProfileScreen.NAME_FIELD)
    //        .performScrollTo()
    //        .assertIsDisplayed()
    //        .assertTextEquals(NAME)
    //    composeTestRule
    //        .onNodeWithTag(ProfileScreen.DESCRIPTION_FIELD)
    //        .performScrollTo()
    //        .assertIsDisplayed()
    //        .assertTextEquals(DESCRIPTION)
  }
}
