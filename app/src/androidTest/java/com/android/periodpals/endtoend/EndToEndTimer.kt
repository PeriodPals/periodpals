package com.android.periodpals.endtoend

import android.Manifest
import android.os.SystemClock
import android.util.Log
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.android.periodpals.MainActivity
import com.android.periodpals.model.timer.HOUR
import com.android.periodpals.model.timer.MINUTE
import com.android.periodpals.resources.C.Tag.AuthenticationScreens
import com.android.periodpals.resources.C.Tag.AuthenticationScreens.SignInScreen
import com.android.periodpals.resources.C.Tag.AuthenticationScreens.SignUpScreen
import com.android.periodpals.resources.C.Tag.BottomNavigationMenu
import com.android.periodpals.resources.C.Tag.ProfileScreens
import com.android.periodpals.resources.C.Tag.ProfileScreens.CreateProfileScreen
import com.android.periodpals.resources.C.Tag.ProfileScreens.ProfileScreen
import com.android.periodpals.resources.C.Tag.TimerScreen
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val TAG = "EndToEndTimer"
private const val TIMEOUT = 60000L // 60 seconds, adjust for slower devices, networks and CI

@RunWith(AndroidJUnit4::class)
class EndToEndTimer : TestCase() {
  @get:Rule val composeTestRule = createComposeRule()
  @get:Rule val activityRule = ActivityTestRule(MainActivity::class.java)
  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS)

  companion object SignUpData {
    private val signUpName = ('a'..'z').map { it }.shuffled().subList(0, 8).joinToString("")
    private val signUpEmail = "$signUpName@example.com"
    private const val PASSWORD = "iLoveSwent1234!"
    private const val DOB = "31/01/2000"
    private val signUpDescription = "Short bio containing my name to identify me: $signUpName"
  }

  @Before
  fun setUp() {
    composeTestRule.setContent { MainActivity() }
  }

  /**
   * End-to-end test for the
   * [timer flow](https://www.figma.com/design/r6jgyWnwTQ6e5X1eLpeHwN/PeriodsPals?node-id=579-5989&node-type=canvas&m=dev).
   *
   * The "user" lands on the SignIn screen then navigates to the SignUp screen. They (correctly)
   * fill in the fields and click on the "Sign Up" button and get redirected to the CreateProfile
   * screen. They (correctly) fill in the fields and get redirected to the Profile screen that
   * displays the info they just entered. They navigate to the Timer screen and start the timer.
   * They stop the timer, start it again, and reset it.
   */
  @Test
  fun timerEndToEnd() {
    // Navigate to the sign-up screen
    composeTestRule.waitForIdle()
    Log.d(TAG, "User arrives on SignIn Screen")
    composeTestRule.onNodeWithTag(SignInScreen.SCREEN).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(SignInScreen.NOT_REGISTERED_NAV_LINK)
        .performScrollTo()
        .assertIsDisplayed()
        .performClick()
    composeTestRule.waitUntil(TIMEOUT) {
      composeTestRule.onAllNodesWithTag(SignUpScreen.SCREEN).fetchSemanticsNodes().size == 1
    }

    // Register a new user
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
        .performTextInput(PASSWORD)
    composeTestRule
        .onNodeWithTag(SignUpScreen.CONFIRM_PASSWORD_FIELD)
        .performScrollTo()
        .assertIsDisplayed()
        .performTextInput(PASSWORD)
    composeTestRule
        .onNodeWithTag(SignUpScreen.SIGN_UP_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
        .performClick()
    composeTestRule.waitUntil(TIMEOUT) {
      composeTestRule.onAllNodesWithTag(CreateProfileScreen.SCREEN).fetchSemanticsNodes().size == 1
    }

    // Create a new profile
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
        .performTextInput(DOB)
    composeTestRule
        .onNodeWithTag(ProfileScreens.DESCRIPTION_INPUT_FIELD)
        .performScrollTo()
        .assertIsDisplayed()
        .performTextInput(signUpDescription)
    composeTestRule
        .onNodeWithTag(ProfileScreens.SAVE_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
        .performClick()
    composeTestRule.waitUntil(TIMEOUT) {
      composeTestRule.onAllNodesWithTag(ProfileScreen.SCREEN).fetchSemanticsNodes().size == 1
    }

    // Check the profile screen
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

    // Navigate to the Timer screen
    Log.d(TAG, "User navigates to Timer Screen")
    composeTestRule
        .onNodeWithTag(BottomNavigationMenu.BOTTOM_NAVIGATION_MENU_ITEM + "Timer")
        .assertIsDisplayed()
        .performClick()
    composeTestRule.waitUntil(TIMEOUT) {
      composeTestRule.onAllNodesWithTag(TimerScreen.SCREEN).fetchSemanticsNodes().size == 1
    }

    // Start the timer
    composeTestRule.waitForIdle()
    Log.d(TAG, "User starts the timer")
    composeTestRule
        .onNodeWithTag(TimerScreen.START_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
        .performClick()

    // Stop the timer
    SystemClock.setCurrentTimeMillis(System.currentTimeMillis() + 6L * HOUR + 30L * MINUTE)
    Log.d(TAG, "User stops the timer")
    composeTestRule
        .onNodeWithTag(TimerScreen.STOP_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
        .performClick()

    // Start the timer again
    Log.d(TAG, "User starts the timer again")
    composeTestRule
        .onNodeWithTag(TimerScreen.START_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
        .performClick()

    // Reset the timer
    Log.d(TAG, "User resets the timer")
    composeTestRule
        .onNodeWithTag(TimerScreen.RESET_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
        .performClick()
  }
}
