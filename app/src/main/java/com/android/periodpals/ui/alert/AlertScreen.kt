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
import com.android.periodpals.ui.navigation.BottomNavigationMenu
import com.android.periodpals.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import com.android.periodpals.ui.navigation.TopAppBar

@Composable
fun AlertScreen(navigationActions: NavigationActions) {
  // TODO: Change the component of dropdown menu
  val context = LocalContext.current
  var product by remember { mutableStateOf("Please choose a product") }
  var urgency by remember { mutableStateOf("Please choose an urgency level") }
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
                  listOf("Tampons", "Pads", "No Preference"),
                  "Product Needed",
                  "alertProduct",
                  product) {
                    product = it
                  }

              // Urgency
              ExposedDropdownMenuSample(
                  listOf("!!! High", "!! Medium", "! Low"),
                  "Urgency level",
                  "alertUrgency",
                  urgency) {
                    urgency = it
                  }

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
                  onClick = {
                    val errorMessage = validateFields(product, urgency, location, message)
                    if (errorMessage != null) {
                      Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    } else {
                      navigationActions.navigateTo(Screen.ALERT_LIST)
                      Toast.makeText(context, "Alert sent!", Toast.LENGTH_SHORT).show()
                    }
                  },
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
fun ExposedDropdownMenuSample(
    options: List<String>,
    label: String,
    testTag: String,
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
  var expanded by remember { mutableStateOf(false) }

  ExposedDropdownMenuBox(
      modifier = Modifier.testTag(testTag),
      expanded = expanded,
      onExpandedChange = { expanded = it },
  ) {
    TextField(
        modifier = Modifier.menuAnchor(),
        value = selectedItem,
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
              onItemSelected(option)
              expanded = false
            },
            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
        )
      }
    }
  }
}

/** Validates the fields of the alert screen. */
private fun validateFields(
    product: String,
    urgency: String,
    location: String,
    message: String
): String? {
  return when {
    !validateProduct(product) -> "Please select a product"
    !validateUrgency(urgency) -> "Please select an urgency level"
    !validateOutlinedTextField(location) -> "Please enter a location"
    !validateOutlinedTextField(message) -> "Please write your message"
    else -> null
  }
}

/** Validates the product dropdown menu has chosen an option. */
private fun validateProduct(product: String): Boolean {
  return product.isNotEmpty() && product != "Please choose a product"
}

/** Validates the urgency dropdown menu has chosen an option. */
private fun validateUrgency(urgency: String): Boolean {
  return urgency.isNotEmpty() && urgency != "Please choose an urgency level"
}

/** Validates the location is not empty. */
private fun validateOutlinedTextField(text: String): Boolean {
  // TODO: change while implementing the location / map
  return text.isNotEmpty()
}
