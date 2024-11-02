package com.android.periodpals.ui.alert

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.android.periodpals.resources.C.Tag.CreateAlertScreen
import com.android.periodpals.ui.navigation.BottomNavigationMenu
import com.android.periodpals.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import com.android.periodpals.ui.navigation.TopAppBar

@Composable
fun CreateAlertScreen(navigationActions: NavigationActions) {
  val context = LocalContext.current
  var location by remember { mutableStateOf("") }
  var message by remember { mutableStateOf("") }
  val (productIsSelected, setProductIsSelected) = remember { mutableStateOf(false) }
  val (urgencyIsSelected, setUrgencyIsSelected) = remember { mutableStateOf(false) }

  Scaffold(
      modifier = Modifier.testTag(CreateAlertScreen.SCREEN),
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationActions.currentRoute(),
        )
      },
      topBar = { TopAppBar(title = "Create Alert") },
      content = { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(30.dp).padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
          // Text Instruction
          Text(
              "Push a notification to users near you! If they are available and have the products you need, they'll be able to help you!",
              modifier = Modifier.testTag(CreateAlertScreen.INSTRUCTION_TEXT),
              textAlign = TextAlign.Center,
              style = MaterialTheme.typography.titleSmall,
          )

          // Product
          ExposedDropdownMenuSample(
              listOf("Tampons", "Pads", "No Preference"),
              "Product Needed",
              "Please choose a product",
              CreateAlertScreen.PRODUCT_FIELD,
              setProductIsSelected,
          )

          // Urgency
          ExposedDropdownMenuSample(
              listOf("!!! High", "!! Medium", "! Low"),
              "Urgency level",
              "Please choose an urgency level",
              CreateAlertScreen.URGENCY_FIELD,
              setUrgencyIsSelected,
          )

          // Location
          OutlinedTextField(
              value = location,
              onValueChange = { location = it },
              label = { Text("Location") },
              placeholder = { Text("Enter your location") },
              modifier = Modifier.fillMaxWidth().testTag(CreateAlertScreen.LOCATION_FIELD),
          )

          // Message
          OutlinedTextField(
              value = message,
              onValueChange = { message = it },
              label = { Text("Message") },
              placeholder = { Text("Write a message for the other users") },
              modifier =
                  Modifier.fillMaxWidth().height(150.dp).testTag(CreateAlertScreen.MESSAGE_FIELD),
          )

          // Submit Button
          Button(
              onClick = {
                val errorMessage =
                    validateFields(productIsSelected, urgencyIsSelected, location, message)
                if (errorMessage != null) {
                  Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                } else {
                  Toast.makeText(context, "Alert sent", Toast.LENGTH_SHORT).show()
                  navigationActions.navigateTo(Screen.ALERT_LIST)
                }
              },
              modifier =
                  Modifier.width(300.dp)
                      .height(100.dp)
                      .testTag(CreateAlertScreen.SUBMIT_BUTTON)
                      .padding(16.dp),
          ) {
            Text("Ask for Help", style = MaterialTheme.typography.headlineMedium)
          }
        }
      },
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownMenuSample(
    list: List<String>,
    label: String,
    instruction: String,
    testTag: String,
    setIsSelected: (Boolean) -> Unit,
) {
  var options = list
  var expanded by remember { mutableStateOf(false) }
  var text by remember { mutableStateOf(instruction) }

  ExposedDropdownMenuBox(
      modifier = Modifier.testTag(testTag),
      expanded = expanded,
      onExpandedChange = { expanded = it },
  ) {
    TextField(
        modifier = Modifier.menuAnchor(),
        value = text,
        onValueChange = {},
        singleLine = true,
        readOnly = true,
        label = { Text(label) },
        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        colors = ExposedDropdownMenuDefaults.textFieldColors(),
    )
    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
      options.forEach { option ->
        DropdownMenuItem(
            modifier = Modifier,
            text = { Text(option) },
            onClick = {
              text = option
              expanded = false
              setIsSelected(true)
            },
            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
        )
      }
    }
  }
}

/** Validates the fields of the alert screen. */
private fun validateFields(
    productIsSelected: Boolean,
    urgencyIsSelected: Boolean,
    location: String,
    message: String,
): String? {
  return when {
    !productIsSelected -> "Please select a product"
    !urgencyIsSelected -> "Please select an urgency level"
    location.isEmpty() -> "Please enter a location"
    message.isEmpty() -> "Please write your message"
    else -> null
  }
}
