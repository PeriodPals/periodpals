package com.android.periodpals.ui.alert

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.android.periodpals.ui.navigation.BottomNavigationMenu
import com.android.periodpals.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.TopAppBar

@Composable
fun AlertScreen(navigationActions: NavigationActions) {
  var location by remember { mutableStateOf("") }
  var message by remember { mutableStateOf("") }

  //    TODO("TOP APP BAR and BOTTOM NAVIGATION")
  Scaffold(
      modifier = Modifier.testTag("alertScreen"),
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationActions.currentRoute())
      },
      topBar = {
        TopAppBar(
            title = "Create Alert",
        )
      },
      content = { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(30.dp).padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly) {
              // Text Instruction
              Text(
                  "Push a notification to users near you! If they are available and have the products you need, they'll be able to help you!",
                  modifier = Modifier.testTag("alertInstruction"),
                  textAlign = TextAlign.Center,
                  style = MaterialTheme.typography.titleSmall)

              // Product
              ExposedDropdownMenuSample(
                  listOf("Tampons", "Pads", "No Preference"), "Product Needed", "alertProduct")

              // Urgency
              ExposedDropdownMenuSample(
                  listOf("!!! High", "!! Medium", "! Low"), "Urgency level", "alertUrgency")

              // Location
              OutlinedTextField(
                  value = location,
                  onValueChange = { location = it },
                  label = { Text("Location") },
                  placeholder = { Text("Enter your location") },
                  modifier = Modifier.fillMaxWidth().testTag("alertLocation"))

              // Message
              OutlinedTextField(
                  value = message,
                  onValueChange = { message = it },
                  label = { Text("Message") },
                  placeholder = { Text("Write a message for the other users") },
                  modifier = Modifier.fillMaxWidth().height(150.dp).testTag("alertMessage"))

              // Submit Button
              Button(
                  onClick = { navigationActions.navigateTo("alertList") },
                  modifier =
                      Modifier.width(300.dp).height(100.dp).testTag("alertSubmit").padding(16.dp),
              ) {
                Text("Ask for Help", style = MaterialTheme.typography.headlineMedium)
              }
            }
      })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownMenuSample(list: List<String>, label: String, testTag: String) {
  var options = list
  var expanded by remember { mutableStateOf(false) }
  var text by remember { mutableStateOf("Please choose one option") }

  ExposedDropdownMenuBox(
      modifier = Modifier.testTag(testTag),
      expanded = expanded,
      onExpandedChange = { expanded = it },
  ) {
    TextField(
        modifier = Modifier.menuAnchor(),
        value = text,
        onValueChange = {},
        readOnly = true,
        singleLine = true,
        label = { Text(label) },
        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        colors = ExposedDropdownMenuDefaults.textFieldColors(),
    )
    ExposedDropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
    ) {
      options.forEach { option ->
        DropdownMenuItem(
            modifier = Modifier.testTag(option),
            text = { Text(option) },
            onClick = {
              text = option
              expanded = false
            },
            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
        )
      }
    }
  }
}
