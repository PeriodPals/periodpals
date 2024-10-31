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
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class BottomNavigationMenuTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun displaysAllTabs() {

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
  fun clickOnTabChangesSelection() {

    var selectedTab by mutableStateOf(Route.MAP)

    composeTestRule.setContent {
      BottomNavigationMenu(
          onTabSelect = { selectedTab = it.route },
          tabList = LIST_TOP_LEVEL_DESTINATION,
          selectedItem = selectedTab)
    }
    composeTestRule.onNodeWithTag("Map").assertIsSelected()
    composeTestRule.onNodeWithTag("Alert").performClick()
    composeTestRule.onNodeWithTag("Alert").assertIsSelected()
    composeTestRule.onNodeWithTag("Map").assertIsNotSelected()
  }

  @Test
  fun iconAndLabelAreCorrectlyDisplayed() {

    composeTestRule.setContent {
      BottomNavigationMenu(
          onTabSelect = {}, tabList = LIST_TOP_LEVEL_DESTINATION, selectedItem = "Profile")
    }
    LIST_TOP_LEVEL_DESTINATION.forEach { tab ->
      composeTestRule.onNodeWithTag(tab.textId).assertIsDisplayed()
      composeTestRule.onNodeWithText(tab.textId).assertIsDisplayed()
    }
  }

  @Test
  fun initialSelectionIsCorrect() {

    composeTestRule.setContent {
      BottomNavigationMenu(
          onTabSelect = {}, tabList = LIST_TOP_LEVEL_DESTINATION, selectedItem = "Timer")
    }
    composeTestRule.onNodeWithTag("Timer").assertIsSelected()
  }

  @Test
  fun selectingSameTabDoesNotCrash() {

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
