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

private const val SCREEN_TITLE = "Create Alert"
private const val DEFAULT_LOCATION = ""
private const val DEFAULT_MESSAGE = ""
private const val INSTRUCTION_TEXT =
    "Push a notification to users near you! If they are available and have the products you need, they'll be able to help you!"

private val PRODUCT_DROPDOWN_CHOICES = listOf("Tampons", "Pads", "No Preference")
private const val PRODUCT_DROPDOWN_LABEL = "Product Needed"
private const val PRODUCT_DROPDOWN_DEFAULT_VALUE = "Please choose a product"

private val EMERGENCY_DROPDOWN_CHOICES = listOf("!!! High", "!! Medium", "! Low")
private const val EMERGENCY_DROPDOWN_LABEL = "Urgency Level"
private const val EMERGENCY_DROPDOWN_DEFAULT_VALUE = "Please choose an urgency level"

private const val LOCATION_FIELD_LABEL = "Location"
private const val LOCATION_FIELD_PLACEHOLDER = "Enter your location"

private const val MESSAGE_FIELD_LABEL = "Message"
private const val MESSAGE_FIELD_PLACEHOLDER = "Write a message for the other users"

private const val SUCCESSFUL_SUBMISSION_TOAST_MESSAGE = "Alert sent"
private const val SUBMISSION_BUTTON_TEXT = "Ask for Help"

/**
 * Composable function for the CreateAlert screen.
 *
 * @param navigationActions The navigation actions to handle navigation events.
 */
@Composable
fun CreateAlertScreen(navigationActions: NavigationActions) {
  val context = LocalContext.current
  var location by remember { mutableStateOf(DEFAULT_LOCATION) }
  var message by remember { mutableStateOf(DEFAULT_MESSAGE) }
  val (productIsSelected, setProductIsSelected) = remember { mutableStateOf(false) }
  val (urgencyIsSelected, setUrgencyIsSelected) = remember { mutableStateOf(false) }

  Scaffold(
      modifier = Modifier.testTag(CreateAlertScreen.SCREEN),
      topBar = { TopAppBar(title = SCREEN_TITLE) },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationActions.currentRoute(),
        )
      },
  ) { paddingValues ->
    Column(
        modifier = Modifier.fillMaxSize().padding(30.dp).padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
         verticalArrangement = Arrangement.SpaceEvenly,
    ) {
      Text(
          text = INSTRUCTION_TEXT,
          modifier = Modifier.testTag(CreateAlertScreen.INSTRUCTION_TEXT),
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.titleSmall,
      )
      ExposedDropdownMenuSample(
          itemsList = PRODUCT_DROPDOWN_CHOICES,
          label = PRODUCT_DROPDOWN_LABEL,
          defaultValue = PRODUCT_DROPDOWN_DEFAULT_VALUE,
          setIsSelected = setProductIsSelected,
          testTag = CreateAlertScreen.PRODUCT_FIELD,
      )
      ExposedDropdownMenuSample(
          itemsList = EMERGENCY_DROPDOWN_CHOICES,
          label = EMERGENCY_DROPDOWN_LABEL,
          defaultValue = EMERGENCY_DROPDOWN_DEFAULT_VALUE,
          setIsSelected = setUrgencyIsSelected,
          testTag = CreateAlertScreen.URGENCY_FIELD,
      )
      OutlinedTextField(
          modifier = Modifier.fillMaxWidth().testTag(CreateAlertScreen.LOCATION_FIELD),
          value = location,
          onValueChange = { location = it },
          label = { Text(LOCATION_FIELD_LABEL) },
          placeholder = { Text(LOCATION_FIELD_PLACEHOLDER) },
      )
      OutlinedTextField(
          modifier =
              Modifier.fillMaxWidth().height(150.dp).testTag(CreateAlertScreen.MESSAGE_FIELD),
          value = message,
          onValueChange = { message = it },
          label = { Text(MESSAGE_FIELD_LABEL) },
          placeholder = { Text(MESSAGE_FIELD_PLACEHOLDER) },
      )
      Button(
          modifier =
              Modifier.width(300.dp)
                  .height(100.dp)
                  .testTag(CreateAlertScreen.SUBMIT_BUTTON)
                  .padding(16.dp),
          onClick = {
            val (isValid, errorMessage) =
                validateFields(productIsSelected, urgencyIsSelected, location, message)
            if (!isValid) {
              Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            } else {
              Toast.makeText(context, SUCCESSFUL_SUBMISSION_TOAST_MESSAGE, Toast.LENGTH_SHORT)
                  .show()
              navigationActions.navigateTo(Screen.ALERT_LIST)
            }
          },
      ) {
        Text(SUBMISSION_BUTTON_TEXT, style = MaterialTheme.typography.headlineMedium)
      }
    }
  }
}

/**
 * Composable function for an exposed dropdown menu.
 *
 * @param itemsList The list of items to display in the dropdown menu.
 * @param label The label for the dropdown menu.
 * @param defaultValue The default value to display in the dropdown menu.
 * @param setIsSelected A function to set the selection state.
 * @param testTag The test tag for the dropdown menu.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownMenuSample(
    itemsList: List<String>,
    label: String,
    defaultValue: String,
    setIsSelected: (Boolean) -> Unit,
    testTag: String,
) {
  var expanded by remember { mutableStateOf(false) }
  var text by remember { mutableStateOf(defaultValue) }

  ExposedDropdownMenuBox(
      modifier = Modifier.testTag(testTag),
      expanded = expanded,
      onExpandedChange = { expanded = it },
  ) {
    TextField(
        modifier = Modifier.menuAnchor(),
        label = { Text(label) },
        value = text,
        onValueChange = {},
        singleLine = true,
        readOnly = true,
        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        colors = ExposedDropdownMenuDefaults.textFieldColors(),
    )
    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
      itemsList.forEach { option ->
        DropdownMenuItem(
            modifier = Modifier.testTag(CreateAlertScreen.DROPDOWN_ITEM + option),
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

/**
 * Validates the fields of the CreateAlert screen.
 *
 * @param productIsSelected Whether a product is selected.
 * @param urgencyIsSelected Whether an urgency level is selected.
 * @param location The location entered by the user.
 * @param message The message entered by the user.
 * @return A pair containing a boolean indicating whether the fields are valid and an error message
 *   if they are not.
 */
private fun validateFields(
    productIsSelected: Boolean,
    urgencyIsSelected: Boolean,
    location: String,
    message: String,
): Pair<Boolean, String> {
  return when {
    !productIsSelected -> Pair(false, "Please select a product")
    !urgencyIsSelected -> Pair(false, "Please select an urgency level")
    location.isEmpty() -> Pair(false, "Please enter a location")
    message.isEmpty() -> Pair(false, "Please write your message")
    else -> Pair(true, "")
  }
}
