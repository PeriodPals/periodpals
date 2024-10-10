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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun AlertScreen() {
  var location by remember { mutableStateOf("") }
  var message by remember { mutableStateOf("") }

  //    TODO("TOP APP BAR and BOTTOM NAVIGATION")
  Scaffold(
      modifier = Modifier.testTag("alertScreen"),
      content = { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp).padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly) {
              // Text Instruction
              Text(
                  "Push a notification that will be received by users around you so that they can help you find the period product that you need",
                  modifier = Modifier.testTag("alertInstruction"),
                  textAlign = TextAlign.Center,
                  style = MaterialTheme.typography.titleSmall)

              // Product selection
              ExposedDropdownMenuSample(
                  listOf("Tampons", "Pads", "Either"), "Product Needed", "alertProduct")

              // Urgency indicator
              ExposedDropdownMenuSample(
                  listOf("!!! High", "!! Medium", "! Low"), "Urgency level", "alertUrgency")

              // Location
              OutlinedTextField(
                  value = location,
                  onValueChange = { location = it },
                  label = { Text("Location") },
                  placeholder = { Text("Enter your location") },
                  modifier = Modifier.fillMaxWidth().testTag("alertLocation"))

              // Message Box
              OutlinedTextField(
                  value = message,
                  onValueChange = { message = it },
                  label = { Text("Message") },
                  placeholder = { Text("Write a message for the other users") },
                  modifier = Modifier.fillMaxWidth().height(150.dp).testTag("alertMessage"))

              // Submit Button
              Button(
                  onClick = {
                    //                      TODO("Save alert on supabase + navigation to
                    // AlertListScreen")
                  },
                  modifier =
                      Modifier.width(300.dp).height(100.dp).testTag("alertSubmit").padding(16.dp),
                  // colors
              ) {
                Text("Ask for help", style = MaterialTheme.typography.headlineMedium)
              }
            }
      })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownMenuSample(list: List<String>, m: String, testTag: String) {
  var options = list
  var expanded by remember { mutableStateOf(false) }
  var text by remember { mutableStateOf(m) }

  ExposedDropdownMenuBox(
      modifier = Modifier.testTag(testTag),
      expanded = expanded,
      onExpandedChange = { expanded = it },
  ) {
    TextField(
        // The `menuAnchor` modifier must be passed to the text field to handle
        // expanding/collapsing the menu on click. A read-only text field has
        // the anchor type `PrimaryNotEditable`.
        modifier = Modifier.menuAnchor(),
        value = text,
        onValueChange = {},
        readOnly = true,
        singleLine = true,
        label = { Text("Label") },
        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        colors = ExposedDropdownMenuDefaults.textFieldColors(),
    )
    ExposedDropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
    ) {
      options.forEach { option ->
        DropdownMenuItem(
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
