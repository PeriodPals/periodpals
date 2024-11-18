package com.android.periodpals.ui.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.android.periodpals.resources.C.Tag.TopAppBar
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TopAppBarTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun onlyTitleIsDisplayed() {
    composeTestRule.setContent { TopAppBar(title = "Tampon Timer") }

    composeTestRule.onNodeWithTag(TopAppBar.TOP_BAR).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.TITLE_TEXT).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.GO_BACK_BUTTON).assertDoesNotExist()
    composeTestRule.onNodeWithTag(TopAppBar.EDIT_BUTTON).assertDoesNotExist()
  }

  @Test
  fun backButtonIsDisplayed() {
    composeTestRule.setContent {
      TopAppBar(title = "Tampon Timer", backButton = true, onBackButtonClick = { /* Do nothing */})
    }

    composeTestRule.onNodeWithTag(TopAppBar.TOP_BAR).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.TITLE_TEXT).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.GO_BACK_BUTTON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.EDIT_BUTTON).assertDoesNotExist()
  }

  @Test
  fun editButtonIsDisplayed() {
    composeTestRule.setContent {
      TopAppBar(title = "Tampon Timer", editButton = true, onEditButtonClick = { /* Do nothing */})
    }

    composeTestRule.onNodeWithTag(TopAppBar.TOP_BAR).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.TITLE_TEXT).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.EDIT_BUTTON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.GO_BACK_BUTTON).assertDoesNotExist()
  }

  @Test
  fun backAndEditButtonsAreDisplayed() {
    composeTestRule.setContent {
      TopAppBar(
          title = "Tampon Timer",
          backButton = true,
          onBackButtonClick = { /* Do nothing */},
          editButton = true,
          onEditButtonClick = { /* Do nothing */},
      )
    }

    composeTestRule.onNodeWithTag(TopAppBar.TOP_BAR).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.TITLE_TEXT).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.GO_BACK_BUTTON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.EDIT_BUTTON).assertIsDisplayed()
  }

  @Test
  fun backButtonClickWorks() {
    var backButtonClicked = false

    composeTestRule.setContent {
      TopAppBar(
          title = "Tampon Timer",
          backButton = true,
          onBackButtonClick = { backButtonClicked = true },
      )
    }

    composeTestRule.onNodeWithTag(TopAppBar.GO_BACK_BUTTON).performClick()
    assert(backButtonClicked)
  }

  @Test
  fun editButtonClickWorks() {
    var editButtonClicked = false

    composeTestRule.setContent {
      TopAppBar(
          title = "Tampon Timer",
          editButton = true,
          onEditButtonClick = { editButtonClicked = true },
      )
    }

    composeTestRule.onNodeWithTag(TopAppBar.EDIT_BUTTON).performClick()
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
