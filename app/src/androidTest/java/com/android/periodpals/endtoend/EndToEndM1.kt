package com.android.periodpals.endtoend

import android.util.Log
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.android.periodpals.MainActivity
import com.android.periodpals.resources.C.Tag.AuthenticationScreens
import com.android.periodpals.resources.C.Tag.AuthenticationScreens.SignInScreen
import com.android.periodpals.resources.C.Tag.AuthenticationScreens.SignUpScreen
import com.android.periodpals.resources.C.Tag.ProfileScreens
import com.android.periodpals.resources.C.Tag.ProfileScreens.CreateProfileScreen
import com.android.periodpals.resources.C.Tag.ProfileScreens.ProfileScreen
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val TAG = "EndToEndM1"
private const val TIMEOUT = 60000L // 60 seconds, adjust for slower devices, networks and CI

@RunWith(AndroidJUnit4::class)
class EndToEndM1 : TestCase() {
  @get:Rule val composeTestRule = createComposeRule()
  @get:Rule val activityRule = ActivityTestRule(MainActivity::class.java)

  companion object SignUpData {
    private val signUpName = ('a'..'z').map { it }.shuffled().subList(0, 8).joinToString("")
    private const val dob = "01/01/2001"
    private val signUpDescription = "Short bio containing my name to identify me: $signUpName"
    private val signUpEmail = "$signUpName@example.com"
    private const val psswd = "iLoveSwent1234!"
    private const val signInName = "testUser"
    private const val signInEmail = "$signInName@example.com"
    private const val signInDescription = "Short bio containing my name to identify me: $signInName"
  }

  @Before fun setUp() {}

  @After
  fun tearDown() {
    // delete user from database
  }

  /**
   * End-to-end test for the
   * [sign-up flow](https://www.figma.com/design/r6jgyWnwTQ6e5X1eLpeHwN/PeriodsPals?node-id=579-5989&node-type=canvas&m=dev)
   *
   * The "user" lands on the SignIn screen then navigates to the SignUp screen. They (correctly)
   * fill in the fields and click on the "Sign Up" button and get redirected to the CreateProfile
   * screen. They (correctly) fill in the fields and get redirected to the Profile screen that
   * displays the info they just entered.
   */
  @Test
  fun signUpEndToEnd() {

    composeTestRule.setContent { MainActivity() }

    // SignIn Screen
    // User navigates to SignUp Screen
    composeTestRule.waitForIdle()
    Log.d(TAG, "User arrives on SignIn Screen")
    composeTestRule.onNodeWithTag(SignInScreen.SCREEN).assertExists()
    composeTestRule
        .onNodeWithTag(SignInScreen.NOT_REGISTERED_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
        .performClick()

    // SignUp Screen
    // User fills out the form and submits
    composeTestRule.waitForIdle()
    Log.d(TAG, "User arrives on SignUp Screen")
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.EMAIL_FIELD)
        .performScrollTo()
        .assertIsDisplayed()
        .performTextInput(signUpEmail)
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.PASSWORD_FIELD)
        .performScrollTo()
        .assertIsDisplayed()
        .performTextInput(psswd)
    composeTestRule
        .onNodeWithTag(SignUpScreen.CONFIRM_PASSWORD_FIELD)
        .performScrollTo()
        .assertIsDisplayed()
        .performTextInput(psswd)
    Espresso.closeSoftKeyboard()
    composeTestRule
        .onNodeWithTag(SignUpScreen.SIGN_UP_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
        .performClick()
    composeTestRule.waitUntil(TIMEOUT) {
      composeTestRule.onAllNodesWithTag(CreateProfileScreen.SCREEN).fetchSemanticsNodes().size == 1
    }

    // Create Profile Screen
    // User fills out the form and submits
    composeTestRule.waitForIdle()
    Log.d(TAG, "User arrives on Create Profile Screen")
    composeTestRule
        .onNodeWithTag(ProfileScreens.NAME_INPUT_FIELD)
        .performScrollTo()
        .assertIsDisplayed()
        .performTextInput(signUpName)
    composeTestRule
        .onNodeWithTag(ProfileScreens.DOB_INPUT_FIELD)
        .performScrollTo()
        .assertIsDisplayed()
        .performTextInput(dob)
    composeTestRule
        .onNodeWithTag(ProfileScreens.DESCRIPTION_INPUT_FIELD)
        .performScrollTo()
        .assertIsDisplayed()
        .performTextInput(signUpDescription)
    Espresso.closeSoftKeyboard()
    composeTestRule
        .onNodeWithTag(ProfileScreens.SAVE_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
        .performClick()
    composeTestRule.waitUntil(TIMEOUT) {
      composeTestRule.onAllNodesWithTag(ProfileScreen.SCREEN).fetchSemanticsNodes().size == 1
    }

    // Profile Screen
    // User arrives on Profile Screen and see their data displayed
    composeTestRule.waitForIdle()
    Log.d(TAG, "User arrives on Profile Screen")
    composeTestRule
        .onNodeWithTag(ProfileScreen.NAME_FIELD)
        .performScrollTo()
        .assertIsDisplayed()
        .assertTextEquals(signUpName)
    composeTestRule
        .onNodeWithTag(ProfileScreen.DESCRIPTION_FIELD)
        .performScrollTo()
        .assertIsDisplayed()
        .assertTextEquals(signUpDescription)
    composeTestRule
        .onNodeWithTag(ProfileScreen.NO_REVIEWS_CARD)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(ProfileScreen.CONTRIBUTION_FIELD)
        .performScrollTo()
        .assertIsDisplayed()
        .assertTextEquals("New user")
  }

  /**
   * End-to-end test for the
   * [sign-in flow](https://www.figma.com/design/r6jgyWnwTQ6e5X1eLpeHwN/PeriodsPals?node-id=579-5989&node-type=canvas&m=dev).
   *
   * The "user" lands on the SignIn screen and (correctly) fill in their info. They click on the
   * "Sign In" button and get redirected to the Profile screen that displays their information.
   */
  @Test
  fun signInEndToEnd() {

    composeTestRule.setContent { MainActivity() }

    // SignIn Screen
    // User fills out the form and submits
    composeTestRule.waitForIdle()
    Log.d(TAG, "User arrives on SignIn Screen")
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.EMAIL_FIELD)
        .performScrollTo()
        .assertIsDisplayed()
        .performTextInput(signInEmail)
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.PASSWORD_FIELD)
        .performScrollTo()
        .assertIsDisplayed()
        .performTextInput(psswd)
    Espresso.closeSoftKeyboard()
    composeTestRule
        .onNodeWithTag(SignInScreen.SIGN_IN_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
        .performClick()
    composeTestRule.waitUntil(TIMEOUT) {
      composeTestRule.onAllNodesWithTag(ProfileScreen.SCREEN).fetchSemanticsNodes().size == 1
    }

    // Profile Screen
    // User arrives on Profile Screen and see their data displayed
    composeTestRule.waitForIdle()
    composeTestRule.waitUntil(
        TIMEOUT) { // need to wait because very first recomposition has not fetched data yet
          try { // trick waitUntil into thinking this counts as a SemanticNodeInteraction
            composeTestRule
                .onNodeWithTag(ProfileScreen.NAME_FIELD)
                .performScrollTo()
                .assertIsDisplayed()
                .assertTextEquals(signInName)
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
        .assertTextEquals(signInName)
    composeTestRule
        .onNodeWithTag(ProfileScreen.DESCRIPTION_FIELD)
        .performScrollTo()
        .assertIsDisplayed()
        .assertTextEquals(signInDescription)
    composeTestRule
        .onNodeWithTag(ProfileScreen.NO_REVIEWS_CARD)
        .performScrollTo()
        .assertIsDisplayed() // TODO: change once implemented the reviews
    composeTestRule
        .onNodeWithTag(ProfileScreen.CONTRIBUTION_FIELD)
        .performScrollTo()
        .assertIsDisplayed()
        .assertTextEquals("New user") // TODO: change once implemented the statuses
  }
}
