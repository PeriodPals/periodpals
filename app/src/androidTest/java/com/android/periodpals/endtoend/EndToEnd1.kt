package com.android.periodpals.endtoend

import android.util.Log
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.android.periodpals.MainActivity
import com.android.periodpals.resources.C.Tag
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

private const val TAG = "EndToEnd1"

/**
 * This end-to-end test checks the user flow for signing up a new user. See "SignUp" user flow (M1:
 * User Flow 1)
 */
@RunWith(AndroidJUnit4::class)
class EndToEnd1 : TestCase() {
  @get:Rule val composeTestRule = createComposeRule()
  @get:Rule val activityRule = ActivityTestRule(MainActivity::class.java)

  companion object {
    private val email = UUID.randomUUID().toString() + "@example.com"
    private val psswd = "iLoveSwent1234!"

    private const val name = "Ada Lovelace"
    private const val dob = "10/12/1852"
    private const val description = "I'm an English mathematician"
  }

  @Before fun setUp() {}

  @After
  fun tearDown() {
    // delete user from database
  }

  @Test
  fun signUpEndToEnd() {

    composeTestRule.setContent { MainActivity() }

    // User arrives on SignIn Screen and navs towards SignUp Screen
    composeTestRule.waitForIdle()
    Log.d(TAG, "User arrives on SignIn Screen")
    composeTestRule.onNodeWithTag(Tag.SignInScreen.SCREEN).assertExists()
    composeTestRule
      .onNodeWithTag(Tag.SignInScreen.NOT_REGISTERED_BUTTON)
      .assertIsDisplayed()
      .performClick()

    // User Signs Up and proceeds to create profile
    composeTestRule.waitForIdle()
    Log.d(TAG, "User arrives on SignUp Screen")
    composeTestRule
      .onNodeWithTag(Tag.SignUpScreen.EMAIL_FIELD)
      .assertIsDisplayed()
      .performTextInput(email)
    composeTestRule
      .onNodeWithTag(Tag.SignUpScreen.PASSWORD_FIELD)
      .assertIsDisplayed()
      .performTextInput(psswd)
    composeTestRule
      .onNodeWithTag(Tag.SignUpScreen.CONFIRM_PASSWORD_FIELD)
      .assertIsDisplayed()
      .performTextInput(psswd)
    composeTestRule
      .onNodeWithTag(Tag.SignUpScreen.SIGN_UP_BUTTON)
      .assertIsDisplayed()
      .performClick()

    // Fill up profile
    composeTestRule.waitForIdle()
    Log.d(TAG, "User arrives on Create Profile Screen")
    composeTestRule
      .onNodeWithTag(Tag.CreateProfileScreen.EMAIL_FIELD)
      .assertIsDisplayed()
      .performTextInput(email)
    composeTestRule
      .onNodeWithTag(Tag.CreateProfileScreen.NAME_FIELD)
      .assertIsDisplayed()
      .performTextInput(name)
    composeTestRule
      .onNodeWithTag(Tag.CreateProfileScreen.DOB_FIELD)
      .assertIsDisplayed()
      .performTextInput(dob)
    composeTestRule
      .onNodeWithTag(Tag.CreateProfileScreen.DESCRIPTION_FIELD)
      .assertIsDisplayed()
      .performTextInput(description)
    composeTestRule
      .onNodeWithTag(Tag.CreateProfileScreen.SAVE_BUTTON)
      .assertIsDisplayed()
      .performClick()

    // Profile Screen
    composeTestRule.waitForIdle()
    Log.d(TAG, "User arrives on Profile Screen")
    composeTestRule.onNodeWithTag(Tag.ProfileScreen.SCREEN).assertExists()
  }
}
