package com.android.periodpals.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class BottomNavigationMenuTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun bottomNavigationMenu_displaysAllTabs() {
    composeTestRule.setContent {
      BottomNavigationMenu(
          onTabSelect = {}, tabList = LIST_TOP_LEVEL_DESTINATION, selectedItem = "Map")
    }

    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
    LIST_TOP_LEVEL_DESTINATION.forEach { tab ->
      composeTestRule.onNodeWithTag(tab.textId).assertIsDisplayed()
    }
  }

  @SuppressLint("UnrememberedMutableState")
  @Test
  fun bottomNavigationMenu_clickOnTab_changesSelection() {
    var selectedTab by mutableStateOf(Route.MAP) // Initially selected tab is "MAP"

    // Set the composable content with an initial selected tab
    composeTestRule.setContent {
      BottomNavigationMenu(
          onTabSelect = { selectedTab = it.route },
          tabList = LIST_TOP_LEVEL_DESTINATION,
          selectedItem = selectedTab)
    }

    // Initially, verify that "MAP" is selected
    composeTestRule.onNodeWithTag("Map").assertIsSelected()

    // Perform a click on the "Alert" tab
    composeTestRule.onNodeWithTag("Alert").performClick()

    // Now check that the "Alert" tab is selected
    composeTestRule.onNodeWithTag("Alert").assertIsSelected()

    // Optionally, check that the previously selected "Map" tab is no longer selected
    composeTestRule.onNodeWithTag("Map").assertIsNotSelected()
  }

  @Test
  fun bottomNavigationMenu_iconAndLabelAreDisplayedCorrectly() {
    composeTestRule.setContent {
      BottomNavigationMenu(
          onTabSelect = {}, tabList = LIST_TOP_LEVEL_DESTINATION, selectedItem = "Profile")
    }

    LIST_TOP_LEVEL_DESTINATION.forEach { tab ->
      composeTestRule.onNodeWithTag(tab.textId).assertIsDisplayed()
      composeTestRule.onNodeWithTag(tab.textId).assertIsDisplayed()
    }
  }

  @Test
  fun bottomNavigationMenu_initialSelectionIsCorrect() {
    composeTestRule.setContent {
      BottomNavigationMenu(
          onTabSelect = {}, tabList = LIST_TOP_LEVEL_DESTINATION, selectedItem = "Timer")
    }

    composeTestRule.onNodeWithTag("Timer").assertIsSelected()
  }

  @Test
  fun bottomNavigationMenu_selectingSameTabDoesNotCrash() {
    var selectedTab = Route.ALERT_LIST

    composeTestRule.setContent {
      BottomNavigationMenu(
          onTabSelect = { selectedTab = it.route },
          tabList = LIST_TOP_LEVEL_DESTINATION,
          selectedItem = selectedTab)
    }

    composeTestRule.onNodeWithTag("Alert List").performClick()
    composeTestRule.onNodeWithTag("Alert List").assertIsSelected()
  }
}
