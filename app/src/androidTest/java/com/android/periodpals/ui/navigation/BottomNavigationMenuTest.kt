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
import com.android.periodpals.resources.C
import org.junit.Rule
import org.junit.Test

class BottomNavigationMenuTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun bottomNavigationMenu_displaysAllTabs() {
    composeTestRule.setContent {
      BottomNavigationMenu(
          onTabSelect = {},
          tabList = LIST_TOP_LEVEL_DESTINATION,
          selectedItem = "Map",
      )
    }

    composeTestRule
        .onNodeWithTag(C.Tag.BottomNavigationMenu.BOTTOM_NAVIGATION_MENU)
        .assertIsDisplayed()
    LIST_TOP_LEVEL_DESTINATION.forEach { tab ->
      composeTestRule
          .onNodeWithTag(C.Tag.BottomNavigationMenu.BOTTOM_NAVIGATION_MENU_ITEM + tab.textId)
          .assertIsDisplayed()
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
          selectedItem = selectedTab,
      )
    }

    // Initially, verify that "MAP" is selected
    composeTestRule
        .onNodeWithTag(C.Tag.BottomNavigationMenu.BOTTOM_NAVIGATION_MENU_ITEM + "Map")
        .assertIsSelected()

    // Perform a click on the "Alert" tab
    composeTestRule
        .onNodeWithTag(C.Tag.BottomNavigationMenu.BOTTOM_NAVIGATION_MENU_ITEM + "Alert")
        .performClick()

    // Now check that the "Alert" tab is selected
    composeTestRule
        .onNodeWithTag(C.Tag.BottomNavigationMenu.BOTTOM_NAVIGATION_MENU_ITEM + "Alert")
        .assertIsSelected()

    // Optionally, check that the previously selected "Map" tab is no longer selected
    composeTestRule
        .onNodeWithTag(C.Tag.BottomNavigationMenu.BOTTOM_NAVIGATION_MENU_ITEM + "Map")
        .assertIsNotSelected()
  }

  @Test
  fun bottomNavigationMenu_iconAndLabelAreDisplayedCorrectly() {
    composeTestRule.setContent {
      BottomNavigationMenu(
          onTabSelect = {},
          tabList = LIST_TOP_LEVEL_DESTINATION,
          selectedItem = "Profile",
      )
    }

    LIST_TOP_LEVEL_DESTINATION.forEach { tab ->
      composeTestRule
          .onNodeWithTag(C.Tag.BottomNavigationMenu.BOTTOM_NAVIGATION_MENU_ITEM + tab.textId)
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag(C.Tag.BottomNavigationMenu.BOTTOM_NAVIGATION_MENU_ITEM + tab.textId)
          .assertIsDisplayed()
    }
  }

  @Test
  fun bottomNavigationMenu_initialSelectionIsCorrect() {
    composeTestRule.setContent {
      BottomNavigationMenu(
          onTabSelect = {},
          tabList = LIST_TOP_LEVEL_DESTINATION,
          selectedItem = "Timer",
      )
    }

    composeTestRule
        .onNodeWithTag(C.Tag.BottomNavigationMenu.BOTTOM_NAVIGATION_MENU_ITEM + "Timer")
        .assertIsSelected()
  }

  @Test
  fun bottomNavigationMenu_selectingSameTabDoesNotCrash() {
    var selectedTab = Route.ALERT_LIST

    composeTestRule.setContent {
      BottomNavigationMenu(
          onTabSelect = { selectedTab = it.route },
          tabList = LIST_TOP_LEVEL_DESTINATION,
          selectedItem = selectedTab,
      )
    }

    composeTestRule
        .onNodeWithTag(C.Tag.BottomNavigationMenu.BOTTOM_NAVIGATION_MENU_ITEM + "Alert List")
        .performClick()
    composeTestRule
        .onNodeWithTag(C.Tag.BottomNavigationMenu.BOTTOM_NAVIGATION_MENU_ITEM + "Alert List")
        .assertIsSelected()
  }
}
