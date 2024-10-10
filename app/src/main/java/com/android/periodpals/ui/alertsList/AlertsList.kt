package com.android.periodpals.ui.alertsList

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.android.periodpals.R

/**
 * This screen displays the list of request under two distinct tabs: MyAlerts and PalsAlerts.
 * MyAlerts corresponds to the alerts that the user has published. PalsAlerts correspond to the
 * alerts that other users have published.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertListScreen(modifier: Modifier = Modifier) {
    // Index 0 -> MyAlerts
    // Index 1 -> Pals Alerts
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Scaffold (
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Alerts List",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                TabRow(
                    selectedTabIndex = selectedTabIndex,
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0},
                        text = { Text("My Alerts") }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = { Text("Pals Alerts") }
                    )
                }
            }
        },
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTabIndex) {
                0 -> MyAlerts()
                1 -> PalsAlerts()
            }
        }
    }
}

@Composable
fun MyAlerts(modifier: Modifier = Modifier) {
    Column (
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ){
        AlertItem()
        AlertItem()
        AlertItem()
        AlertItem()
    }
}

@Composable
fun PalsAlerts(modifier: Modifier = Modifier) {
    NoAlertDialog(1)
}

@Composable
fun AlertItem(modifier: Modifier = Modifier) {
    Card (
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        onClick = { /* do something */ }
    ){
        Row(
            modifier = Modifier
                .padding(7.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Profile Image
            Image(
                painter = painterResource(id = R.drawable.profile_pic),
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )

            // Info about the user
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ){
                Text("Bruno Lazarini")
                Text("7:00")
                Text("EPFL")
            }

            // Spacer to push the remaining items to the right
            Spacer(modifier = Modifier.weight(1f))

            // Menstrual Product Type
            Icon(
                imageVector = Icons.Outlined.Call,
                contentDescription = "Menstrual Product Type",
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            // Urgency
            Icon(
                imageVector = Icons.Outlined.Warning,
                contentDescription = "Urgency of the Alert",
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

@Composable
fun NoAlertDialog(whichTab: Int, modifier: Modifier = Modifier) {
    Column (
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card (
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(7.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Warning,
                    contentDescription = "No alerts posted"
                )

                // Change the text in function of the selected tab
                when (whichTab) {
                    0 -> Text("You haven't posted any alerts")
                    1 -> Text("No pals need help")
                }
            }
        }
    }
}