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

@RunWith(AndroidJUnit4::class)
class EndToEnd1 : TestCase() {
  @get:Rule val composeTestRule = createComposeRule()
  @get:Rule val activityRule = ActivityTestRule(MainActivity::class.java)

  companion object SignUpData {
    private val signUpName = ('a'..'z').map { it }.shuffled().subList(0, 8).joinToString("")
    private const val dob = "01/01/2001"
    private val signUpDescription = "Short bio containing my name to identify me: $signUpName"
    private val signUpEmail = signUpName + "@example.com"
    private val psswd = "iLoveSwent1234!"
    private val signInName = "testUser"
    private val signInEmail = signInName + "@example.com"
    private val signInDescription = "Short bio containing my name to identify me: $signInName"
  }

  @Before fun setUp() {}

  @After
  fun tearDown() {
    // delete user from database
  }

  @Test
  fun signUpEndToEnd() {

    composeTestRule.setContent { MainActivity() }

    // SignIn Screen
    composeTestRule.waitForIdle()
    Log.d(TAG, "User arrives on SignIn Screen")
    composeTestRule.onNodeWithTag(SignInScreen.SCREEN).assertExists()
    composeTestRule
        .onNodeWithTag(SignInScreen.NOT_REGISTERED_BUTTON)
        .assertIsDisplayed()
        .performClick()

    // SignUp Screen
    composeTestRule.waitForIdle()
    Log.d(TAG, "User arrives on SignUp Screen")
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.EMAIL_FIELD)
        .assertIsDisplayed()
        .performTextInput(signUpEmail)
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.PASSWORD_FIELD)
        .assertIsDisplayed()
        .performTextInput(psswd)
    composeTestRule
        .onNodeWithTag(SignUpScreen.CONFIRM_PASSWORD_FIELD)
        .assertIsDisplayed()
        .performTextInput(psswd)
    Espresso.closeSoftKeyboard()
    composeTestRule.onNodeWithTag(SignUpScreen.SIGN_UP_BUTTON).assertIsDisplayed().performClick()
    composeTestRule.waitUntil(1000) {
      composeTestRule.onAllNodesWithTag(CreateProfileScreen.SCREEN).fetchSemanticsNodes().size == 1
    }

    // Create Profile Screen
    composeTestRule.waitForIdle()
    Log.d(TAG, "User arrives on Create Profile Screen")
    composeTestRule
        .onNodeWithTag(ProfileScreens.NAME_INPUT_FIELD)
        .assertIsDisplayed()
        .performTextInput(signUpName)
    composeTestRule
        .onNodeWithTag(ProfileScreens.DOB_INPUT_FIELD)
        .assertIsDisplayed()
        .performTextInput(dob)
    composeTestRule
        .onNodeWithTag(ProfileScreens.DESCRIPTION_INPUT_FIELD)
        .assertIsDisplayed()
        .performTextInput(signUpDescription)
    Espresso.closeSoftKeyboard()
    composeTestRule.onNodeWithTag(ProfileScreens.SAVE_BUTTON).assertIsDisplayed().performClick()
    composeTestRule.waitUntil(1000) {
      composeTestRule.onAllNodesWithTag(ProfileScreen.SCREEN).fetchSemanticsNodes().size == 1
    }

    // Profile Screen
    composeTestRule.waitForIdle()
    Log.d(TAG, "User arrives on Profile Screen")
    composeTestRule
        .onNodeWithTag(ProfileScreen.NAME_FIELD)
        .assertIsDisplayed()
        .assertTextEquals(signUpName)
    composeTestRule
        .onNodeWithTag(ProfileScreen.DESCRIPTION_FIELD)
        .assertIsDisplayed()
        .assertTextEquals(signUpDescription)
    composeTestRule.onNodeWithTag(ProfileScreen.NO_REVIEWS_CARD).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(ProfileScreen.CONTRIBUTION_FIELD)
        .assertIsDisplayed()
        .assertTextEquals("New user")
  }

  @Test
  fun signInEndToEnd() {

    composeTestRule.setContent { MainActivity() }

    // SignIn Screen
    composeTestRule.waitForIdle()
    Log.d(TAG, "User arrives on SignIn Screen")
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.EMAIL_FIELD)
        .assertIsDisplayed()
        .performTextInput(signInEmail)
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.PASSWORD_FIELD)
        .assertIsDisplayed()
        .performTextInput(psswd)
    Espresso.closeSoftKeyboard()
    composeTestRule.onNodeWithTag(SignInScreen.SIGN_IN_BUTTON).assertIsDisplayed().performClick()
    composeTestRule.waitUntil(1000) {
      composeTestRule.onAllNodesWithTag(ProfileScreen.SCREEN).fetchSemanticsNodes().size == 1
    }

    // Profile Screen
    composeTestRule.waitForIdle()
    composeTestRule.waitUntil(
        1000) { // need to wait because very first recomposition has not fetched data yet
          try { // trick waitUntil into thinking this counts as a SemanticNodeInteraction
            composeTestRule
                .onNodeWithTag(ProfileScreen.NAME_FIELD)
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
        .assertIsDisplayed()
        .assertTextEquals(signInName)
    composeTestRule
        .onNodeWithTag(ProfileScreen.DESCRIPTION_FIELD)
        .assertIsDisplayed()
        .assertTextEquals(signInDescription)
    composeTestRule
        .onNodeWithTag(ProfileScreen.NO_REVIEWS_CARD)
        .assertIsDisplayed() // TODO: change once implemented the reviews
    composeTestRule
        .onNodeWithTag(ProfileScreen.CONTRIBUTION_FIELD)
        .assertIsDisplayed()
        .assertTextEquals("New user") // TODO: change once implemented the statuses
  }
}
