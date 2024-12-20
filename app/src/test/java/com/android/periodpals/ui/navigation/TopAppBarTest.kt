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
    composeTestRule.onNodeWithTag(TopAppBar.SETTINGS_BUTTON).assertDoesNotExist()
    composeTestRule.onNodeWithTag(TopAppBar.CHAT_BUTTON).assertDoesNotExist()
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
    composeTestRule.onNodeWithTag(TopAppBar.SETTINGS_BUTTON).assertDoesNotExist()
    composeTestRule.onNodeWithTag(TopAppBar.CHAT_BUTTON).assertDoesNotExist()
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
    composeTestRule.onNodeWithTag(TopAppBar.SETTINGS_BUTTON).assertDoesNotExist()
    composeTestRule.onNodeWithTag(TopAppBar.CHAT_BUTTON).assertDoesNotExist()
    composeTestRule.onNodeWithTag(TopAppBar.GO_BACK_BUTTON).assertDoesNotExist()
  }

  @Test
  fun settingsButtonIsDisplayed() {
    composeTestRule.setContent {
      TopAppBar(
          title = "Tampon Timer",
          settingsButton = true,
          onSettingsButtonClick = { /* Do nothing */},
      )
    }

    composeTestRule.onNodeWithTag(TopAppBar.TOP_BAR).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.TITLE_TEXT).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.SETTINGS_BUTTON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.CHAT_BUTTON).assertDoesNotExist()
    composeTestRule.onNodeWithTag(TopAppBar.GO_BACK_BUTTON).assertDoesNotExist()
    composeTestRule.onNodeWithTag(TopAppBar.EDIT_BUTTON).assertDoesNotExist()
  }

  @Test
  fun chatButtonIsDisplayed() {
    composeTestRule.setContent {
      TopAppBar(
          title = "Tampon Timer",
          chatButton = true,
          onChatButtonClick = { /* Do nothing */},
      )
    }

    composeTestRule.onNodeWithTag(TopAppBar.TOP_BAR).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.TITLE_TEXT).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.CHAT_BUTTON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.SETTINGS_BUTTON).assertDoesNotExist()
    composeTestRule.onNodeWithTag(TopAppBar.GO_BACK_BUTTON).assertDoesNotExist()
    composeTestRule.onNodeWithTag(TopAppBar.EDIT_BUTTON).assertDoesNotExist()
  }

  @Test
  fun allButtonsAreDisplayed() {
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
  fun settingsButtonClickWorks() {
    var settingsButtonClicked = false

    composeTestRule.setContent {
      TopAppBar(
          title = "Tampon Timer",
          settingsButton = true,
          onSettingsButtonClick = { settingsButtonClicked = true },
      )
    }

    composeTestRule.onNodeWithTag(TopAppBar.SETTINGS_BUTTON).performClick()
    assert(settingsButtonClicked)
  }

  @Test
  fun chatButtonClickWorks() {
    var chatButtonClicked = false

    composeTestRule.setContent {
      TopAppBar(
          title = "Tampon Timer",
          chatButton = true,
          onChatButtonClick = { chatButtonClicked = true },
      )
    }

    composeTestRule.onNodeWithTag(TopAppBar.CHAT_BUTTON).performClick()
    assert(chatButtonClicked)
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

  @Test
  fun settingsButtonInvalidFunction() {
    val exception =
        assertThrows(IllegalArgumentException::class.java) {
          composeTestRule.setContent {
            TopAppBar(title = "Test Title", settingsButton = true, onSettingsButtonClick = null)
          }
        }
    assert(
        exception.message == "onSettingsButtonClick must be provided when settingsButton is true")
  }

  @Test
  fun chatButtonInvalidFunction() {
    val exception =
        assertThrows(IllegalArgumentException::class.java) {
          composeTestRule.setContent {
            TopAppBar(title = "Test Title", chatButton = true, onChatButtonClick = null)
          }
        }
    assert(exception.message == "onChatButtonClick must be provided when chatButton is true")
  }

  @Test
  fun cannotHaveBothBackAndSettingsButtons() {
    val exception =
        assertThrows(IllegalArgumentException::class.java) {
          composeTestRule.setContent {
            TopAppBar(
                title = "Test Title",
                backButton = true,
                onBackButtonClick = { /* Do nothing */},
                settingsButton = true,
                onSettingsButtonClick = { /* Do nothing */},
            )
          }
        }
    assert(exception.message == "Either backButton or settingsButton must be true, but not both")
  }
}
