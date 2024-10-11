package com.android.periodpals.ui.alertsList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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

/**
 * Displays the list of request under two distinct tabs: MyAlerts and PalsAlerts. MyAlerts
 * corresponds to the alerts that the user has published. PalsAlerts correspond to the alerts that
 * other users have published.
 */
@Composable
fun AlertListScreen(modifier: Modifier = Modifier) {

  // Controls which tab is selected (0 -> MyAlerts; 1 -> PalsAlerts)
  var selectedTabIndex by remember { mutableIntStateOf(0) }

  Scaffold(
      modifier = Modifier.testTag("alertListScreen"),
      topBar = {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
          Text(
              text = "Alerts List",
              style = MaterialTheme.typography.headlineMedium,
              modifier = Modifier.testTag("alertListTitle"))

          Spacer(modifier = Modifier.height(16.dp))

          TabRow(selectedTabIndex = selectedTabIndex, modifier = Modifier.testTag("tabRowAlert")) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = { Text("My Alerts") },
                modifier = Modifier.testTag("myAlertsTab"))
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = { Text("Pals Alerts") },
                modifier = Modifier.testTag("palsAlertsTab"))
          }
        }
      }) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {

          // Change the displayed list in function of the tab that the user selects
          when (selectedTabIndex) {
            0 -> MyAlerts()
            1 -> PalsAlerts()
          }
        }
      }
}

/** Displays the list of alerts published by the user. */
@Composable
fun MyAlerts() {
  Column(
      modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(20.dp)) {
        // TODO: Display the items in a LazyColum or the NoAlertDialog if there aren't any
        AlertItem()
      }
}

/** Displays the list of alerts published by other pals. */
@Composable
fun PalsAlerts() {
  // TODO: Display the items in a LazyColum or the NoAlertDialog if there aren't any
  NoAlertDialog()
}

/**
 * An alert item. It displays in a card the following information: Profile picture, name of
 * publisher, time of publish, location, menstrual product type and urgency level.
 */
@Composable
fun AlertItem() {
  Card(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp).testTag("alertItem"),
      elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
      onClick = { /* do something */}) {
        Row(
            modifier = Modifier.padding(7.dp).fillMaxWidth().testTag("alertItemRow"),
            verticalAlignment = Alignment.CenterVertically,
        ) {
          // For the moment, the card is using placeholder values.
          // TODO: Implement the model and viewmodel and link them with this screen.

          // Profile Image
          //            Image(
          //                painter = painterResource(id = R.drawable.profile_pic),
          //                imageVector = Icons.Default.AccountBox,
          //                contentDescription = "Profile Picture",
          //                contentScale = ContentScale.Crop,
          //                modifier = Modifier
          //                    .size(40.dp)
          //                    .clip(CircleShape)
          //                    .testTag("alertListItemImage")
          //            )

          // Placeholder item
          Icon(
              imageVector = Icons.Default.AccountBox,
              contentDescription = "Profile Picture",
              modifier = Modifier.testTag("alertProfilePicture"))

          // Info about the user. For the moment all of this are placeholder values
          Column(
              modifier = Modifier.weight(1f).padding(horizontal = 8.dp).testTag("alertItemText")) {
                Text(text = "Bruno Lazarini")
                Text(text = "7:00")
                Text(text = "EPFL")
              }

          // Spacer to push the remaining items to the right
          Spacer(modifier = Modifier.weight(1f))

          // Menstrual Product Type
          Icon(
              imageVector = Icons.Outlined.Call, // TODO: Design Icon
              contentDescription = "Menstrual Product Type",
              modifier = Modifier.padding(horizontal = 4.dp))

          // Urgency
          Icon(
              imageVector = Icons.Outlined.Warning, // TODO: Design Icon
              contentDescription = "Urgency of the Alert",
              modifier = Modifier.padding(horizontal = 4.dp))
        }
      }
}

/** Displays a message in a card indicating that there are no alerts published. */
@Composable
fun NoAlertDialog() {
  Column(
      modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center) {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier.testTag("noAlertsCard")) {
              Column(
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.spacedBy(10.dp),
                  modifier = Modifier.padding(7.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.Warning,
                        contentDescription = "No alerts posted",
                        modifier = Modifier.testTag("noAlertsIcon"))

                    Text(
                        text = "No alerts here for the moment...",
                        modifier = Modifier.testTag("noAlertsCardText"))
                  }
            }
      }
}
