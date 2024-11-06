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
import com.android.periodpals.resources.C.Tag.BottomNavigationMenu
import org.junit.Rule
import org.junit.Test

class BottomNavigationMenuTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun displaysAllTabs() {

    composeTestRule.setContent {
      BottomNavigationMenu(
          onTabSelect = {},
          tabList = LIST_TOP_LEVEL_DESTINATION,
          selectedItem = "Map",
      )
    }

    composeTestRule.onNodeWithTag(BottomNavigationMenu.BOTTOM_NAVIGATION_MENU).assertIsDisplayed()
    LIST_TOP_LEVEL_DESTINATION.forEach { tab ->
      composeTestRule
          .onNodeWithTag(BottomNavigationMenu.BOTTOM_NAVIGATION_MENU_ITEM + tab.textId)
          .assertIsDisplayed()
    }
  }

  @SuppressLint("UnrememberedMutableState")
  @Test
  fun clickOnTabChangesSelection() {

    var selectedTab by mutableStateOf(Route.MAP)

    composeTestRule.setContent {
      BottomNavigationMenu(
          onTabSelect = { selectedTab = it.route },
          tabList = LIST_TOP_LEVEL_DESTINATION,
          selectedItem = selectedTab,
      )
    }
    composeTestRule
        .onNodeWithTag(BottomNavigationMenu.BOTTOM_NAVIGATION_MENU_ITEM + "Map")
        .assertIsSelected()
    composeTestRule
        .onNodeWithTag(BottomNavigationMenu.BOTTOM_NAVIGATION_MENU_ITEM + "Alert")
        .performClick()
    composeTestRule
        .onNodeWithTag(BottomNavigationMenu.BOTTOM_NAVIGATION_MENU_ITEM + "Alert")
        .assertIsSelected()
    composeTestRule
        .onNodeWithTag(BottomNavigationMenu.BOTTOM_NAVIGATION_MENU_ITEM + "Map")
        .assertIsNotSelected()
  }

  @Test
  fun iconAndLabelAreCorrectlyDisplayed() {

    composeTestRule.setContent {
      BottomNavigationMenu(
          onTabSelect = {},
          tabList = LIST_TOP_LEVEL_DESTINATION,
          selectedItem = "Profile",
      )
    }
    LIST_TOP_LEVEL_DESTINATION.forEach { tab ->
      composeTestRule
          .onNodeWithTag(BottomNavigationMenu.BOTTOM_NAVIGATION_MENU_ITEM + tab.textId)
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag(BottomNavigationMenu.BOTTOM_NAVIGATION_MENU_ITEM + tab.textId)
          .assertIsDisplayed()
    }
  }

  @Test
  fun initialSelectionIsCorrect() {

    composeTestRule.setContent {
      BottomNavigationMenu(
          onTabSelect = {},
          tabList = LIST_TOP_LEVEL_DESTINATION,
          selectedItem = "Timer",
      )
    }

    composeTestRule
        .onNodeWithTag(BottomNavigationMenu.BOTTOM_NAVIGATION_MENU_ITEM + "Timer")
        .assertIsSelected()
  }

  @Test
  fun selectingSameTabDoesNotCrash() {

    var selectedTab = Route.ALERT_LIST

    composeTestRule.setContent {
      BottomNavigationMenu(
          onTabSelect = { selectedTab = it.route },
          tabList = LIST_TOP_LEVEL_DESTINATION,
          selectedItem = selectedTab,
      )
    }

    composeTestRule
        .onNodeWithTag(BottomNavigationMenu.BOTTOM_NAVIGATION_MENU_ITEM + "Alert List")
        .performClick()
    composeTestRule
        .onNodeWithTag(BottomNavigationMenu.BOTTOM_NAVIGATION_MENU_ITEM + "Alert List")
        .assertIsSelected()
  }
}
