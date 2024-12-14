package com.android.periodpals.endtoend

import android.Manifest
import androidx.compose.ui.test.assertIsDisplayed
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

  @Test
  fun timerEndToEnd() {
    // Navigate to the sign-up screen
    composeTestRule.waitForIdle()
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

    // Navigate to the timer screen
    composeTestRule.waitForIdle()
    composeTestRule
        .onNodeWithTag(BottomNavigationMenu.BOTTOM_NAVIGATION_MENU_ITEM + "Timer")
        .assertIsDisplayed()
        .performClick()
    composeTestRule.waitUntil(TIMEOUT) {
      composeTestRule.onAllNodesWithTag(TimerScreen.SCREEN).fetchSemanticsNodes().size == 1
    }
  }
}
