package com.android.periodpals.ui.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test

class TopAppBarTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun topAppBar_displaysTitle() {
    composeTestRule.setContent { TopAppBar(title = "Tampon Timer") }

    composeTestRule.onNodeWithTag("topBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("screenTitle").assertIsDisplayed()
  }

  @Test
  fun topAppBar_displaysBackButton() {
    composeTestRule.setContent { TopAppBar(title = "Tampon Timer", backButton = true) }

    composeTestRule.onNodeWithTag("topBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("goBackButton").assertIsDisplayed()
  }

  @Test
  fun topAppBar_backButtonClick() {
    var backButtonClicked = false

    composeTestRule.setContent {
      TopAppBar(
          title = "Tampon Timer",
          backButton = true,
          onBackButtonClick = { backButtonClicked = true })
    }

    composeTestRule.onNodeWithTag("goBackButton").performClick()
    assert(backButtonClicked)
  }

  @Test
  fun topAppBar_noBackButton() {
    composeTestRule.setContent { TopAppBar(title = "Tampon Timer", backButton = false) }

    composeTestRule.onNodeWithTag("topBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("goBackButton").assertDoesNotExist()
  }

  @Test
  fun topAppBar_backButtonTrue_onBackButtonClickNull_throwsException() {
    val exception =
        assertThrows(IllegalArgumentException::class.java) {
          composeTestRule.setContent {
            TopAppBar(title = "Test Title", backButton = true, onBackButtonClick = null)
          }
        }
    assert(exception.message == "onBackButtonClick must be provided when backButton is true")
  }

  @Test
  fun topAppBar_backButtonTrue_onBackButtonClickNotNull_doesNotThrowException() {
    composeTestRule.setContent {
      TopAppBar(title = "Test Title", backButton = true, onBackButtonClick = { /* Do nothing */})
    }
    composeTestRule.onNodeWithTag("topBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("goBackButton").assertIsDisplayed()
  }

  @Test
  fun topAppBar_backButtonFalse_onBackButtonClickNull_doesNotThrowException() {
    composeTestRule.setContent {
      TopAppBar(title = "Test Title", backButton = false, onBackButtonClick = null)
    }
    composeTestRule.onNodeWithTag("topBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("goBackButton").assertIsNotDisplayed()
  }

  @Test
  fun topAppBar_backButtonFalse_onBackButtonClickNotNull_doesNotThrowException() {
    composeTestRule.setContent {
      TopAppBar(title = "Test Title", backButton = false, onBackButtonClick = { /* Do nothing */})
    }
    composeTestRule.onNodeWithTag("topBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("goBackButton").assertIsNotDisplayed()
  }
}
