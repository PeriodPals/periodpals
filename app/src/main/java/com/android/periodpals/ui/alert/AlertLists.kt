package com.android.periodpals.ui.alert

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.periodpals.resources.C.Tag.AlertListsScreen
import com.android.periodpals.ui.navigation.BottomNavigationMenu
import com.android.periodpals.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.TopAppBar

private val SELECTED_TAB_DEFAULT = AlertListsTab.MY_ALERTS
private const val SCREEN_TITLE = "Alerts List"
private const val MY_ALERTS_TAB_TITLE = "My Alerts"
private const val PALS_ALERTS_TAB_TITLE = "Pals Alerts"
private const val NO_ALERTS_DIALOG_TEXT = "No alerts here for the moment..."

/** Enum class representing the tabs in the AlertLists screen. */
private enum class AlertListsTab {
  MY_ALERTS,
  PALS_ALERTS,
}

/**
 * Composable function that displays the AlertLists screen. It includes a top app bar, tab row for
 * switching between "My Alerts" and "Pals Alerts" tabs, and a bottom navigation menu.
 *
 * @param navigationActions The navigation actions for handling navigation events.
 */
@Composable
fun AlertListsScreen(navigationActions: NavigationActions) {
  var selectedTab by remember { mutableStateOf(SELECTED_TAB_DEFAULT) }

  Scaffold(
      modifier = Modifier.testTag(AlertListsScreen.SCREEN),
      topBar = {
        Column(modifier = Modifier.fillMaxWidth()) {
          TopAppBar(title = SCREEN_TITLE)
          TabRow(
              selectedTabIndex = selectedTab.ordinal,
              modifier = Modifier.testTag(AlertListsScreen.TAB_ROW),
          ) {
            Tab(
                modifier = Modifier.testTag(AlertListsScreen.MY_ALERTS_TAB),
                text = { Text(MY_ALERTS_TAB_TITLE) },
                selected = selectedTab == AlertListsTab.MY_ALERTS,
                onClick = { selectedTab = AlertListsTab.MY_ALERTS },
            )
            Tab(
                modifier = Modifier.testTag(AlertListsScreen.PALS_ALERTS_TAB),
                text = { Text(PALS_ALERTS_TAB_TITLE) },
                selected = selectedTab == AlertListsTab.PALS_ALERTS,
                onClick = { selectedTab = AlertListsTab.PALS_ALERTS },
            )
          }
        }
      },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationActions.currentRoute(),
        )
      },
  ) { paddingValues ->
    Box(modifier = Modifier.padding(paddingValues).padding(top = 16.dp)) {
      when (selectedTab) {
        AlertListsTab.MY_ALERTS -> MyAlerts()
        AlertListsTab.PALS_ALERTS -> PalsAlerts()
      }
    }
  }
}

/**
 * Composable function that displays the content for the "My Alerts" tab. For now, it only displays
 * a single **placeholder** alert item.
 */
@Composable
fun MyAlerts() {
  Column(
      modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(20.dp),
  ) {
    // TODO: replace placeholder with actual alert items
    AlertItem()
  }
}

/**
 * Composable function that displays the content for the "Pals Alerts" tab. For now, it only
 * displays a **placeholder** dialog for when there are no alerts.
 */
@Composable
fun PalsAlerts() {
  // TODO: replace placeholder with actual alert items
  NoAlertDialog()
}

/**
 * Composable function that displays an individual alert item. It includes details such as profile
 * picture, name, time, location, product type, and urgency. For now, it only displays
 * **placeholder** details.
 */
@Composable
fun AlertItem() {
  Card(
      modifier =
          Modifier.fillMaxWidth().padding(horizontal = 14.dp).testTag(AlertListsScreen.ALERT),
      elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
      onClick = { /*TODO replace with actual onClick action */},
  ) {
    Row(
        modifier = Modifier.padding(7.dp).fillMaxWidth().testTag(AlertListsScreen.ALERT_ROW),
        verticalAlignment = Alignment.CenterVertically,
    ) {
      Icon(
          imageVector = Icons.Default.AccountBox,
          contentDescription = "Profile picture",
          modifier = Modifier.testTag(AlertListsScreen.ALERT_PROFILE_PICTURE),
      )
      // TODO: replace placeholder with actual alert details
      Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
        Text(text = "Bruno Lazarini", modifier = Modifier.testTag(AlertListsScreen.ALERT_NAME))
        Text(text = "7:00", modifier = Modifier.testTag(AlertListsScreen.ALERT_TIME))
        Text(text = "EPFL", modifier = Modifier.testTag(AlertListsScreen.ALERT_LOCATION))
      }
      Spacer(modifier = Modifier.weight(1f))
      Icon(
          imageVector = Icons.Outlined.Call,
          contentDescription = "Menstrual Product Type",
          modifier =
              Modifier.padding(horizontal = 4.dp).testTag(AlertListsScreen.ALERT_PRODUCT_TYPE),
      )
      Icon(
          imageVector = Icons.Outlined.Warning,
          contentDescription = "Urgency of the Alert",
          modifier = Modifier.padding(horizontal = 4.dp).testTag(AlertListsScreen.ALERT_URGENCY),
      )
    }
  }
}

/** Composable function that displays a dialog indicating that there are no alerts. */
@Composable
fun NoAlertDialog() {
  Column(
      modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
  ) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.testTag(AlertListsScreen.NO_ALERTS_CARD),
    ) {
      Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.padding(7.dp),
      ) {
        Icon(
            imageVector = Icons.Outlined.Warning,
            contentDescription = "No alerts posted",
            modifier = Modifier.testTag(AlertListsScreen.NO_ALERTS_ICON),
        )
        Text(
            text = NO_ALERTS_DIALOG_TEXT,
            modifier = Modifier.testTag(AlertListsScreen.NO_ALERTS_TEXT),
        )
      }
    }
  }
}
