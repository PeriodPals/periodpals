package com.android.periodpals.endtoend

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.periodpals.MainActivity
import com.android.periodpals.screens.CreateProfileScreenScreen
import com.android.periodpals.screens.ProfileScreenScreen
import com.android.periodpals.screens.SignInScreenScreen
import com.android.periodpals.screens.SignUpScreenScreen
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This end-to-end test checks the user flow for signing up a new user. See "SignUp" user flow (M1:
 * User Flow 1)
 */
@RunWith(AndroidJUnit4::class)
class SignUpUserFlowTest {
  @get:Rule
  val composeTestRule = createAndroidComposeRule<MainActivity>()

  companion object {
    private const val email = "ada_lovelace@epfl.ch"
    private const val psswd = "iLoveSwent1234!"

    private const val name = "Ada Lovelace"
    private const val dob = "10/12/1852"
    private const val description = "I'm an English mathematician"

    // Define user view model
  }

  @Before
  fun setUp() {

  }

  @After
  fun tearDown() {
    // delete user from database
  }

  @Test
  fun signUpEndToEnd() {

    // User arrives on SignIn Screen and navs towards SignUp Screen
    ComposeScreen.onComposeScreen<SignInScreenScreen>(composeTestRule) {
      composeTestRule
        .onNodeWithTag("signInNotRegistered")
        .assertIsDisplayed()
        .performClick()
    }

    // User Signs Up and proceeds to create profile
    ComposeScreen.onComposeScreen<SignUpScreenScreen>(composeTestRule) {
      composeTestRule.onNodeWithTag("signUpEmail").assertIsDisplayed().performTextInput(email)
      composeTestRule.onNodeWithTag("signUpPassword").assertIsDisplayed().performTextInput(psswd)
      composeTestRule.onNodeWithTag("signUpConfirmText").assertIsDisplayed().performTextInput(psswd)
      composeTestRule
        .onNodeWithTag("signUpButton")
        .assertIsDisplayed()
        .performClick()
    }

    // Fill up profile
    ComposeScreen.onComposeScreen<CreateProfileScreenScreen>(composeTestRule) {
      composeTestRule.onNodeWithTag("email_field").assertIsDisplayed().performTextInput(email)
      composeTestRule.onNodeWithTag("name_field").assertIsDisplayed().performTextInput(name)
      composeTestRule.onNodeWithTag("dob_field").assertIsDisplayed().performTextInput(dob)
      composeTestRule.onNodeWithTag("description_field").assertIsDisplayed()
        .performTextInput(description)
      composeTestRule.onNodeWithTag("save_button").assertIsDisplayed().performClick()
    }

    // Profile Screen
    ComposeScreen.onComposeScreen<ProfileScreenScreen>(composeTestRule) {
      composeTestRule.onNodeWithTag("profileScreen").assertExists()
    }
  }
}
