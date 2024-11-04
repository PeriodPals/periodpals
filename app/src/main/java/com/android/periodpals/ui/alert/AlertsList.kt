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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.periodpals.resources.C.Tag.AlertListScreen
import com.android.periodpals.ui.navigation.BottomNavigationMenu
import com.android.periodpals.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.TopAppBar

private const val SCREEN_TITLE = "Alert Lists"

@Composable
fun AlertListScreen(navigationActions: NavigationActions) {
  var selectedTabIndex by remember { mutableIntStateOf(0) }

  Scaffold(
      modifier = Modifier.testTag(AlertListScreen.SCREEN),
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationActions.currentRoute(),
        )
      },
      topBar = {
        Column(modifier = Modifier.fillMaxWidth()) {
          TopAppBar(title = SCREEN_TITLE)

          TabRow(
              selectedTabIndex = selectedTabIndex,
              modifier = Modifier.testTag(AlertListScreen.TAB_ROW),
          ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = { Text("My Alerts") },
                modifier = Modifier.testTag(AlertListScreen.MY_ALERTS_TAB),
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = { Text("Pals Alerts") },
                modifier = Modifier.testTag(AlertListScreen.PALS_ALERTS_TAB),
            )
          }
        }
      },
  ) { paddingValues ->
    Box(modifier = Modifier.padding(paddingValues).padding(top = 16.dp)) {
      when (selectedTabIndex) {
        0 -> MyAlerts()
        1 -> PalsAlerts()
        else -> MyAlerts()
      }
    }
  }
}

@Composable
fun MyAlerts() {
  Column(
      modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(20.dp),
  ) {
    AlertItem()
  }
}

@Composable
fun PalsAlerts() {
  NoAlertDialog()
}

@Composable
fun AlertItem() {
  Card(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp).testTag(AlertListScreen.ALERT),
      elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
      onClick = { /* do something */},
  ) {
    Row(
        modifier = Modifier.padding(7.dp).fillMaxWidth().testTag(AlertListScreen.ALERT_ROW),
        verticalAlignment = Alignment.CenterVertically,
    ) {
      Icon(
          imageVector = Icons.Default.AccountBox,
          contentDescription = "Profile Picture",
          modifier = Modifier.testTag(AlertListScreen.ALERT_PROFILE_PICTURE),
      )

      Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
        Text(text = "Bruno Lazarini", modifier = Modifier.testTag(AlertListScreen.ALERT_NAME))
        Text(text = "7:00", modifier = Modifier.testTag(AlertListScreen.ALERT_TIME))
        Text(text = "EPFL", modifier = Modifier.testTag(AlertListScreen.ALERT_LOCATION))
      }

      Spacer(modifier = Modifier.weight(1f))

      Icon(
          imageVector = Icons.Outlined.Call,
          contentDescription = "Menstrual Product Type",
          modifier =
              Modifier.padding(horizontal = 4.dp).testTag(AlertListScreen.ALERT_PRODUCT_TYPE),
      )

      Icon(
          imageVector = Icons.Outlined.Warning,
          contentDescription = "Urgency of the Alert",
          modifier = Modifier.padding(horizontal = 4.dp).testTag(AlertListScreen.ALERT_URGENCY),
      )
    }
  }
}

@Composable
fun NoAlertDialog() {
  Column(
      modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
  ) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.testTag(AlertListScreen.NO_ALERTS_CARD),
    ) {
      Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.padding(7.dp),
      ) {
        Icon(
            imageVector = Icons.Outlined.Warning,
            contentDescription = "No alerts posted",
            modifier = Modifier.testTag(AlertListScreen.NO_ALERTS_ICON),
        )

        Text(
            text = "No alerts here for the moment...",
            modifier = Modifier.testTag(AlertListScreen.NO_ALERTS_TEXT),
        )
      }
    }
  }
}
