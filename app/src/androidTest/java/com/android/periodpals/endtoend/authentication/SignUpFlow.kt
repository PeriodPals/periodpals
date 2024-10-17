package com.android.periodpals.endtoend.authentication

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.android.periodpals.ui.authentication.RegisterScreen
import com.android.periodpals.ui.authentication.SignInScreen
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import com.android.periodpals.ui.profile.CreateProfileScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.verify

class SignUpFlow {
    private lateinit var navigationActions: NavigationActions
    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        navigationActions = mock(NavigationActions::class.java)
        // Mock the current route to the Alert List screen
        `when`(navigationActions.currentRoute()).thenReturn(Screen.ALERT_LIST)
    }

    @Test
    fun signInToSignUpTest(){
        composeTestRule.setContent { SignInScreen(navigationActions) }

        composeTestRule.onNodeWithTag("signInScreen").assertExists()

        composeTestRule.onNodeWithTag("signInNotRegistered").assertIsDisplayed()
        composeTestRule.onNodeWithTag("signInNotRegistered").performClick()

        verify(navigationActions).navigateTo(screen = Screen.REGISTER)
    }

    @Test
    fun signUpToCreateProfileTest(){
        composeTestRule.setContent { RegisterScreen(navigationActions) }

        composeTestRule.onNodeWithTag("signUpScreen").assertExists()

        // Input valid data and perform sign up
        composeTestRule.onNodeWithTag("signUpEmail").performTextInput("test@example.com")
        composeTestRule.onNodeWithTag("signUpPassword").performTextInput("ValidPassword123!")
        composeTestRule.onNodeWithTag("signUpConfirmPassword").performTextInput("ValidPassword123!")

        composeTestRule.onNodeWithTag("signUpButton").assertIsDisplayed()
        composeTestRule.onNodeWithTag("signUpButton").performClick()

        verify(navigationActions).navigateTo(screen = Screen.CREATE_PROFILE)
    }

    @Test
    fun createProfileToProfileTest(){
        composeTestRule.setContent { CreateProfileScreen(navigationActions) }

        composeTestRule.onNodeWithTag("createProfileScreen").assertExists()

        //TODO: delete this when email field is removed
        composeTestRule.onNodeWithTag("email_field").performTextInput("test@example.com")

        composeTestRule.onNodeWithTag("name_field").performTextInput("Jane Doe")
        composeTestRule.onNodeWithTag("description_field").performTextInput("I am a test user")
        composeTestRule.onNodeWithTag("dob_field").performTextInput("01/01/2000")

        composeTestRule.onNodeWithTag("save_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("signUpButton").performClick()

        verify(navigationActions).navigateTo(screen = Screen.PROFILE)
    }
}