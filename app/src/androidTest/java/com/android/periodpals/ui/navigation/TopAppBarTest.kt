package com.android.periodpals.ui.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test

class TopAppBarTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun onlyTitleIsDisplayed() {
    composeTestRule.setContent { TopAppBar(title = "Tampon Timer") }

    composeTestRule.onNodeWithTag("topBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("screenTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("goBackButton").assertDoesNotExist()
    composeTestRule.onNodeWithTag("editButton").assertDoesNotExist()
  }

  @Test
  fun backButtonIsDisplayed() {
    composeTestRule.setContent {
      TopAppBar(title = "Tampon Timer", backButton = true, onBackButtonClick = { /* Do nothing */})
    }

    composeTestRule.onNodeWithTag("topBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("screenTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("goBackButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("editButton").assertDoesNotExist()
  }

  @Test
  fun editButtonIsDisplayed() {
    composeTestRule.setContent {
      TopAppBar(title = "Tampon Timer", editButton = true, onEditButtonClick = { /* Do nothing */})
    }

    composeTestRule.onNodeWithTag("topBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("screenTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("editButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("goBackButton").assertDoesNotExist()
  }

  @Test
  fun backAndEditButtonsAreDisplayed() {
    composeTestRule.setContent {
      TopAppBar(
          title = "Tampon Timer",
          backButton = true,
          onBackButtonClick = { /* Do nothing */},
          editButton = true,
          onEditButtonClick = { /* Do nothing */})
    }

    composeTestRule.onNodeWithTag("topBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("screenTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("goBackButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("editButton").assertIsDisplayed()
  }

  @Test
  fun backButtonClickWorks() {
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
  fun editButtonClickWorks() {
    var editButtonClicked = false

    composeTestRule.setContent {
      TopAppBar(
          title = "Tampon Timer",
          editButton = true,
          onEditButtonClick = { editButtonClicked = true })
    }

    composeTestRule.onNodeWithTag("editButton").performClick()
    assert(editButtonClicked)
  }

  @Test
  fun backButtonInvalidFunction() {
    val exception =
        assertThrows(IllegalArgumentException::class.java) {
          composeTestRule.setContent { TopAppBar(title = "Test Title", backButton = true) }
        }
    assert(exception.message == "onBackButtonClick must be provided when backButton is true")
  }

  @Test
  fun editButtonInvalidFunction() {
    val exception =
        assertThrows(IllegalArgumentException::class.java) {
          composeTestRule.setContent {
            TopAppBar(title = "Test Title", editButton = true, onEditButtonClick = null)
          }
        }
    assert(exception.message == "onEditButtonClick must be provided when editButton is true")
  }
}
