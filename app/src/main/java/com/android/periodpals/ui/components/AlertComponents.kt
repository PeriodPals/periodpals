package com.android.periodpals.ui.components

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.android.periodpals.model.location.Location
import com.android.periodpals.model.location.LocationViewModel
import com.android.periodpals.resources.C.Tag.CreateAlertScreen
import com.android.periodpals.resources.ComponentColor.getMenuItemColors
import com.android.periodpals.resources.ComponentColor.getMenuOutlinedTextFieldColors
import com.android.periodpals.resources.ComponentColor.getMenuTextFieldColors
import com.android.periodpals.resources.ComponentColor.getOutlinedTextFieldColors
import com.android.periodpals.ui.theme.dimens

private val PRODUCT_DROPDOWN_CHOICES = listOf("Tampons", "Pads", "No Preference")
private const val PRODUCT_DROPDOWN_LABEL = "Product Needed"

private val URGENCY_DROPDOWN_CHOICES = listOf("!!! High", "!! Medium", "! Low")
private const val URGENCY_DROPDOWN_LABEL = "Urgency Level"

private const val LOCATION_FIELD_LABEL = "Location"
private const val LOCATION_FIELD_PLACEHOLDER = "Enter your location"

private const val MESSAGE_FIELD_LABEL = "Message"
private const val MESSAGE_FIELD_PLACEHOLDER = "Write a message for the other users"

private const val MAX_NAME_LEN = 30
private const val MAX_LOCATION_SUGGESTIONS = 3

private const val CURRENT_LOCATION_TEXT = "Current Location"

/**
 * Composable function for displaying a product selection dropdown menu.
 *
 * @param product The default product value to display in the dropdown menu.
 * @param onValueChange A callback function to handle the change in the selected product value.
 * @return A boolean indicating whether a product is selected.
 */
@Composable
fun productField(product: String, onValueChange: (String) -> Unit): Boolean {
  val (productIsSelected, setProductIsSelected) = remember { mutableStateOf(false) }
  ExposedDropdownMenuSample(
      itemsList = PRODUCT_DROPDOWN_CHOICES,
      label = PRODUCT_DROPDOWN_LABEL,
      defaultValue = product,
      setIsSelected = setProductIsSelected,
      onValueChange = onValueChange, // TODO: fill product value in alert
      testTag = CreateAlertScreen.PRODUCT_FIELD,
  )
  return productIsSelected
}

/**
 * Composable function for displaying an urgency selection dropdown menu.
 *
 * @param urgency The default urgency value to display in the dropdown menu.
 * @param onValueChange A callback function to handle the change in the selected urgency value.
 * @return A boolean indicating whether an urgency level is selected.
 */
@Composable
fun urgencyField(urgency: String, onValueChange: (String) -> Unit): Boolean {
  val (urgencyIsSelected, setUrgencyIsSelected) = remember { mutableStateOf(false) }
  ExposedDropdownMenuSample(
      itemsList = URGENCY_DROPDOWN_CHOICES,
      label = URGENCY_DROPDOWN_LABEL,
      defaultValue = urgency,
      setIsSelected = setUrgencyIsSelected,
      onValueChange = onValueChange, // TODO: fill urgency value in alert
      testTag = CreateAlertScreen.URGENCY_FIELD,
  )
  return urgencyIsSelected
}

/**
 * Composable function for displaying a location selection field with dropdown menu.
 *
 * @param location The selected location.
 * @param locationViewModel The view model for location suggestions.
 * @param onLocationSelected A callback function to handle the selected location.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationField(
    location: Location?,
    locationViewModel: LocationViewModel,
    onLocationSelected: (Location) -> Unit
) {
  val locationSuggestions by locationViewModel.locationSuggestions.collectAsState()
  var name by remember { mutableStateOf(location?.name ?: "") }

  // State for dropdown visibility
  var showDropdown by remember { mutableStateOf(false) }

  // Location Input with dropdown using ExposedDropdownMenuBox
  ExposedDropdownMenuBox(
      expanded = showDropdown && locationSuggestions.isNotEmpty(),
      onExpandedChange = { showDropdown = it }, // Toggle dropdown visibility
      modifier = Modifier.wrapContentSize(),
  ) {
    OutlinedTextField(
        modifier =
            Modifier.menuAnchor() // Anchor the dropdown to this text field
                .wrapContentHeight()
                .fillMaxWidth()
                .testTag(CreateAlertScreen.LOCATION_FIELD),
        textStyle = MaterialTheme.typography.labelLarge,
        value = name,
        onValueChange = {
          name = it
          locationViewModel.setQuery(it)
          showDropdown = true // Show dropdown when user starts typing
        },
        label = { Text(text = LOCATION_FIELD_LABEL, style = MaterialTheme.typography.labelMedium) },
        placeholder = {
          Text(text = LOCATION_FIELD_PLACEHOLDER, style = MaterialTheme.typography.labelMedium)
        },
        singleLine = true,
        colors = getMenuOutlinedTextFieldColors(),
    )

    // Dropdown menu for location suggestions
    ExposedDropdownMenu(
        expanded = showDropdown && locationSuggestions.isNotEmpty(),
        onDismissRequest = { showDropdown = false },
        modifier = Modifier.wrapContentSize(),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
    ) {
      DropdownMenuItem(
          text = { Text(CURRENT_LOCATION_TEXT) },
          onClick = {
            // TODO : Logic for fetching and setting current location
            showDropdown = false // For now close dropdown on selection
          },
          leadingIcon = {
            Icon(
                imageVector = Icons.Filled.GpsFixed,
                contentDescription = "GPS icon",
                modifier = Modifier.size(MaterialTheme.dimens.iconSize))
          },
          colors = getMenuItemColors(),
          contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
      )
      Log.d("CreateAlertScreen", "Location suggestions: $locationSuggestions")
      locationSuggestions.take(MAX_LOCATION_SUGGESTIONS).forEach { location ->
        DropdownMenuItem(
            text = {
              Text(
                  text =
                      location.name.take(MAX_NAME_LEN) +
                          if (location.name.length > MAX_NAME_LEN) "..."
                          else "", // Limit name length
                  maxLines = 1, // Ensure name doesn't overflow
                  style = MaterialTheme.typography.labelLarge)
            },
            onClick = {
              Log.d("CreateAlertScreen", "Selected location: ${location.name}")
              locationViewModel.setQuery(location.name)
              name = location.name
              onLocationSelected(location)
              showDropdown = false // Close dropdown on selection
            },
            modifier =
                Modifier.testTag(CreateAlertScreen.DROPDOWN_ITEM + location.name).semantics {
                  contentDescription = CreateAlertScreen.DROPDOWN_ITEM
                },
            colors = getMenuItemColors(),
            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
        )
      }

      if (locationSuggestions.size > MAX_LOCATION_SUGGESTIONS) {
        DropdownMenuItem(
            text = { Text(text = "More...", style = MaterialTheme.typography.labelLarge) },
            onClick = { /* TODO show more results */},
            colors = getMenuItemColors(),
            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
        )
      }
    }
  }
}

/**
 * Composable function for displaying a message field.
 *
 * @param text The text to display in the message field.
 * @param onValueChange A callback function to handle the change in the message field.
 */
@Composable
fun MessageField(text: String, onValueChange: (String) -> Unit) {
  var isFocused by remember { mutableStateOf(false) }
  OutlinedTextField(
      modifier =
          Modifier.fillMaxWidth()
              .wrapContentHeight()
              .testTag(CreateAlertScreen.MESSAGE_FIELD)
              .onFocusEvent { focusState -> isFocused = focusState.isFocused },
      value = text,
      onValueChange = onValueChange,
      textStyle = MaterialTheme.typography.labelLarge,
      label = {
        Text(
            text = MESSAGE_FIELD_LABEL,
            style =
                if (isFocused || text.isNotEmpty()) MaterialTheme.typography.labelMedium
                else MaterialTheme.typography.labelLarge)
      },
      placeholder = {
        Text(text = MESSAGE_FIELD_PLACEHOLDER, style = MaterialTheme.typography.labelLarge)
      },
      minLines = 3,
      colors = getOutlinedTextFieldColors(),
  )
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
private fun ExposedDropdownMenuSample(
    itemsList: List<String>,
    label: String,
    defaultValue: String,
    setIsSelected: (Boolean) -> Unit,
    onValueChange: (String) -> Unit, // fill Alert values
    testTag: String,
) {
  var expanded by remember { mutableStateOf(false) }
  var text by remember { mutableStateOf(defaultValue) }

  ExposedDropdownMenuBox(
      modifier = Modifier.wrapContentSize().testTag(testTag),
      expanded = expanded,
      onExpandedChange = { expanded = it },
  ) {
    TextField(
        modifier = Modifier.fillMaxWidth().wrapContentHeight().menuAnchor(),
        textStyle = MaterialTheme.typography.labelLarge,
        label = { Text(text = label, style = MaterialTheme.typography.labelMedium) },
        value = text,
        onValueChange = onValueChange,
        singleLine = true,
        readOnly = true,
        trailingIcon = {
          ExposedDropdownMenuDefaults.TrailingIcon(
              expanded = expanded, Modifier.size(MaterialTheme.dimens.iconSize))
        },
        colors = getMenuTextFieldColors(),
    )
    ExposedDropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = Modifier.wrapContentSize(),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
    ) {
      itemsList.forEach { option ->
        DropdownMenuItem(
            modifier = Modifier.fillMaxWidth().testTag(CreateAlertScreen.DROPDOWN_ITEM + option),
            text = { Text(text = option, style = MaterialTheme.typography.labelLarge) },
            onClick = {
              text = option
              expanded = false
              setIsSelected(true)
            },
            colors = getMenuItemColors(),
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
 * @param selectedLocation The selected location.
 * @param message The message entered by the user.
 * @return A pair containing a boolean indicating whether the fields are valid and an error message
 *   if they are not.
 */
fun validateFields(
    productIsSelected: Boolean,
    urgencyIsSelected: Boolean,
    selectedLocation: Location?,
    message: String,
): Pair<Boolean, String> {
  return when {
    !productIsSelected -> Pair(false, "Please select a product")
    !urgencyIsSelected -> Pair(false, "Please select an urgency level")
    selectedLocation == null -> Pair(false, "Please select a location")
    message.isEmpty() -> Pair(false, "Please write your message")
    else -> Pair(true, "")
  }
}
