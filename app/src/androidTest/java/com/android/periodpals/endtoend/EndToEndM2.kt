package com.android.periodpals.endtoend

import android.util.Log
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.android.periodpals.MainActivity
import com.android.periodpals.resources.C.Tag.AuthenticationScreens
import com.android.periodpals.resources.C.Tag.AuthenticationScreens.SignInScreen
import com.android.periodpals.resources.C.Tag.ProfileScreens
import com.android.periodpals.resources.C.Tag.ProfileScreens.EditProfileScreen
import com.android.periodpals.resources.C.Tag.ProfileScreens.ProfileScreen
import com.android.periodpals.resources.C.Tag.TopAppBar
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val TAG = "EndToEnd2"

@RunWith(AndroidJUnit4::class)
class EndToEndM2 : TestCase() {
  @get:Rule val composeTestRule = createComposeRule()
  @get:Rule val activityRule = ActivityTestRule(MainActivity::class.java)

  companion object {
    private val randomNumber = (1..9).random()
    private const val EMAIL = "end2end@test"
    private const val PASSWORD = "Secure!password123"
    private val name = "Mocknica$randomNumber"
    private val dob = "0$randomNumber/01/2000"
    private val description = "I'm a mathematician, my favourite number is $randomNumber!"
  }

  @Before
  fun setUp() {
    composeTestRule.setContent { MainActivity() }
  }

  @Test
  fun signInAndEditProfile() {
    // Sign in using existing account
    composeTestRule.waitForIdle()
    Log.d(TAG, "User arrives on SignIn Screen")
    composeTestRule.onNodeWithTag(SignInScreen.SCREEN).assertExists() // or is displayed?
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.EMAIL_FIELD)
        .assertIsDisplayed()
        .performTextInput(EMAIL)
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.PASSWORD_FIELD)
        .assertIsDisplayed()
        .performTextInput(PASSWORD)
    Espresso.closeSoftKeyboard()
    composeTestRule.waitUntil(6000) { true }
    composeTestRule.onNodeWithTag(SignInScreen.SIGN_IN_BUTTON).assertIsDisplayed().performClick()

    // Profile Screen is displayed
    composeTestRule.waitForIdle()
    Log.d(TAG, "User arrives on Profile Screen")
    composeTestRule.waitUntil(20000) {
      composeTestRule.onAllNodesWithTag(ProfileScreen.SCREEN).fetchSemanticsNodes().size == 1
    }
    Log.d(TAG, "User arrives on Edit Profile Screen")
    composeTestRule.waitUntil(6000) { true }
    composeTestRule.onNodeWithTag(TopAppBar.EDIT_BUTTON).assertIsDisplayed().performClick()

    // Edit Profile Screen is displayed, edit name, dob and description
    composeTestRule.waitForIdle()
    composeTestRule.waitUntil(20000) {
      composeTestRule.onAllNodesWithTag(EditProfileScreen.SCREEN).fetchSemanticsNodes().size == 1
    }
    composeTestRule
        .onNodeWithTag(ProfileScreens.NAME_INPUT_FIELD)
        .assertIsDisplayed()
        .performTextInput(name)
    composeTestRule
        .onNodeWithTag(ProfileScreens.DOB_INPUT_FIELD)
        .assertIsDisplayed()
        .performTextInput(dob)
    composeTestRule
        .onNodeWithTag(ProfileScreens.DESCRIPTION_INPUT_FIELD)
        .assertIsDisplayed()
        .performTextInput(description)
    Espresso.closeSoftKeyboard()
    composeTestRule.waitUntil(6000) { true }
    composeTestRule.onNodeWithTag(ProfileScreens.SAVE_BUTTON).assertIsDisplayed().performClick()

    // Profile Screen, check if the changes are saved
    composeTestRule.waitForIdle()
    composeTestRule.waitUntil(20000) {
      composeTestRule.onAllNodesWithTag(ProfileScreen.SCREEN).fetchSemanticsNodes().size == 1
    }
    Log.d(TAG, "User arrives on Profile Screen")
    composeTestRule.onNodeWithTag(ProfileScreen.NAME_FIELD).assertExists().assertTextEquals(name)
    composeTestRule
        .onNodeWithTag(ProfileScreen.DESCRIPTION_FIELD)
        .assertExists()
        .assertTextEquals(description)
  }
}
