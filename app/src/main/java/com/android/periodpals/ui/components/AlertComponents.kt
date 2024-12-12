package com.android.periodpals.ui.components

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.android.periodpals.model.alert.LIST_OF_PRODUCTS
import com.android.periodpals.model.alert.LIST_OF_URGENCIES
import com.android.periodpals.model.alert.PeriodPalsIcon
import com.android.periodpals.model.location.Location
import com.android.periodpals.model.location.LocationViewModel
import com.android.periodpals.resources.C.Tag.AlertInputs
import com.android.periodpals.resources.C.Tag.AlertListsScreen
import com.android.periodpals.resources.ComponentColor.getMenuItemColors
import com.android.periodpals.resources.ComponentColor.getMenuOutlinedTextFieldColors
import com.android.periodpals.resources.ComponentColor.getMenuTextFieldColors
import com.android.periodpals.resources.ComponentColor.getOutlinedTextFieldColors
import com.android.periodpals.services.GPSServiceImpl
import com.android.periodpals.ui.theme.dimens
import kotlin.math.roundToInt

private const val PRODUCT_DROPDOWN_LABEL = "Product Needed"
private const val URGENCY_DROPDOWN_LABEL = "Urgency Level"

private const val LOCATION_FIELD_LABEL = "Location"
private const val LOCATION_FIELD_PLACEHOLDER = "Enter your location"

private const val MESSAGE_FIELD_LABEL = "Message"
private const val MESSAGE_FIELD_PLACEHOLDER = "Write a message for the other users"

private const val MAX_NAME_LEN = 30
private const val MAX_LOCATION_SUGGESTIONS = 3

private const val LOCATION_FIELD_TAG = "AlertComponents: LocationField"

private const val FILTER_INSTRUCTION_TEXT =
    "Filter Pal's alerts by their location, product, or urgency level"
private const val ERROR_MESSAGE_INVALID_LOCATION = "Please select a valid location"
private const val APPLY_FILTER_BUTTON_TEXT = "Apply Filter"
private const val RESET_FILTER_BUTTON_TEXT = "Reset Filter"
private const val MIN_RADIUS = 100
private const val MAX_RADIUS = 1000
private const val KILOMETERS_IN_METERS = 1000

/**
 * Composable function for displaying a product selection dropdown menu.
 *
 * @param product The default product value to display in the dropdown menu.
 * @param onValueChange A callback function to handle the change in the selected product value.
 */
@Composable
fun ProductField(product: String, onValueChange: (String) -> Unit) {
  ExposedDropdownMenuSample(
      itemsList = LIST_OF_PRODUCTS,
      label = PRODUCT_DROPDOWN_LABEL,
      defaultValue = product,
      onValueChange = onValueChange, // TODO: fill product value in alert
      testTag = AlertInputs.PRODUCT_FIELD,
  )
}

/**
 * Composable function for displaying an urgency selection dropdown menu.
 *
 * @param urgency The default urgency value to display in the dropdown menu.
 * @param onValueChange A callback function to handle the change in the selected urgency value.
 */
@Composable
fun UrgencyField(urgency: String, onValueChange: (String) -> Unit) {
  ExposedDropdownMenuSample(
      itemsList = LIST_OF_URGENCIES,
      label = URGENCY_DROPDOWN_LABEL,
      defaultValue = urgency,
      onValueChange = onValueChange, // TODO: fill urgency value in alert
      testTag = AlertInputs.URGENCY_FIELD,
  )
}

/**
 * Composable function for displaying a location selection field with dropdown menu.
 *
 * @param location The selected location.
 * @param locationViewModel The view model for location suggestions.
 * @param onLocationSelected A callback function to handle the selected location.
 * @param gpsService The GPS service that provides the device's geographical coordinates.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationField(
    location: Location?,
    locationViewModel: LocationViewModel,
    onLocationSelected: (Location) -> Unit,
    gpsService: GPSServiceImpl,
) {
  val locationSuggestions by locationViewModel.locationSuggestions.collectAsState()
  var name by remember { mutableStateOf(location?.name ?: "") }
  val gpsLocation by gpsService.location.collectAsState()

  // State for dropdown visibility
  var showDropdown by remember { mutableStateOf(false) }

  // Location Input with dropdown using ExposedDropdownMenuBox
  ExposedDropdownMenuBox(
      expanded = showDropdown,
      onExpandedChange = { showDropdown = it }, // Toggle dropdown visibility
      modifier = Modifier.wrapContentSize(),
  ) {
    OutlinedTextField(
        modifier =
            Modifier.menuAnchor() // Anchor the dropdown to this text field
                .wrapContentHeight()
                .fillMaxWidth()
                .testTag(AlertInputs.LOCATION_FIELD),
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
        expanded = showDropdown,
        onDismissRequest = { showDropdown = false },
        modifier = Modifier.wrapContentSize(),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
    ) {

      // Current location drop down item
      DropdownMenuItem(
          text = { Text(Location.CURRENT_LOCATION_NAME) },
          onClick = {
            Log.d(
                LOCATION_FIELD_TAG,
                "Selected current location: ${gpsLocation.name} at (${gpsLocation.latitude}, ${gpsLocation.longitude})",
            )
            name = Location.CURRENT_LOCATION_NAME
            onLocationSelected(gpsLocation)
            showDropdown = false
          },
          modifier =
              Modifier.testTag(AlertInputs.DROPDOWN_ITEM + AlertInputs.CURRENT_LOCATION).semantics {
                contentDescription = AlertInputs.DROPDOWN_ITEM
              },
          leadingIcon = {
            Icon(
                imageVector = Icons.Filled.GpsFixed,
                contentDescription = "GPS icon",
                modifier = Modifier.size(MaterialTheme.dimens.iconSize),
            )
          },
          colors = getMenuItemColors(),
          contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
      )

      // Location suggestion drop down items
      if (locationSuggestions.isNotEmpty()) {
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
                    style = MaterialTheme.typography.labelLarge,
                )
              },
              onClick = {
                Log.d(LOCATION_FIELD_TAG, "Selected location: ${location.name}")
                locationViewModel.setQuery(location.name)
                name = location.name
                onLocationSelected(location)
                showDropdown = false
              },
              modifier =
                  Modifier.testTag(AlertInputs.DROPDOWN_ITEM + location.name).semantics {
                    contentDescription = AlertInputs.DROPDOWN_ITEM
                  },
              colors = getMenuItemColors(),
              contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
          )
        }
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
              .testTag(AlertInputs.MESSAGE_FIELD)
              .onFocusEvent { focusState -> isFocused = focusState.isFocused },
      value = text,
      onValueChange = onValueChange,
      textStyle = MaterialTheme.typography.labelLarge,
      label = {
        Text(
            text = MESSAGE_FIELD_LABEL,
            style =
                if (isFocused || text.isNotEmpty()) MaterialTheme.typography.labelMedium
                else MaterialTheme.typography.labelLarge,
        )
      },
      placeholder = {
        Text(text = MESSAGE_FIELD_PLACEHOLDER, style = MaterialTheme.typography.labelLarge)
      },
      minLines = 3,
      colors = getOutlinedTextFieldColors(),
  )
}

/**
 * Composable function for displaying an action button.
 *
 * @param buttonText The text to display on the button.
 * @param onClick The callback function to handle button clicks.
 * @param colors The colors to apply to the button.
 * @param testTag The test tag for the button.
 */
@Composable
fun ActionButton(buttonText: String, onClick: () -> Unit, colors: ButtonColors, testTag: String) {
  Button(
      onClick = onClick,
      modifier = Modifier.wrapContentSize().testTag(testTag),
      colors = colors,
  ) {
    Text(text = buttonText, style = MaterialTheme.typography.headlineSmall)
  }
}

/**
 * Composable function for an exposed dropdown menu.
 *
 * @param itemsList The list of items to display in the dropdown menu.
 * @param label The label for the dropdown menu.
 * @param defaultValue The default value to display in the dropdown menu.
 * @param onValueChange A callback function to handle the change in the selected value.
 * @param testTag The test tag for the dropdown menu.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExposedDropdownMenuSample(
    itemsList: List<PeriodPalsIcon>,
    label: String,
    defaultValue: String,
    onValueChange: (String) -> Unit,
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
              expanded = expanded,
              Modifier.size(MaterialTheme.dimens.iconSize),
          )
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
            modifier = Modifier.fillMaxWidth().testTag(AlertInputs.DROPDOWN_ITEM + option.textId),
            text = { Text(text = option.textId, style = MaterialTheme.typography.labelLarge) },
            onClick = {
              onValueChange(option.textId)
              text = option.textId
              expanded = false
            },
            leadingIcon = {
              Icon(
                  painter = painterResource(id = option.icon),
                  contentDescription = option.textId,
                  modifier = Modifier.size(MaterialTheme.dimens.iconSize),
              )
            },
            colors = getMenuItemColors(),
            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
        )
      }
    }
  }
}

/**
 * Composable function for displaying a floating action button (FAB) to filter alerts. Displays
 * little bubble in the corner, if a filter is active.
 *
 * @param isFilterApplied A boolean indicating if the filter is applied.
 * @param onClick A callback function to handle the FAB click event.
 */
@Composable
fun FilterFab(isFilterApplied: Boolean, onClick: () -> Unit) {
  Box(contentAlignment = Alignment.TopEnd) {
    FloatingActionButton(
        onClick = { onClick() },
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        modifier = Modifier.testTag(AlertListsScreen.FILTER_FAB),
    ) {
      Icon(imageVector = Icons.Default.FilterAlt, contentDescription = "Filter Alerts")
    }

    if (isFilterApplied) {
      Badge(
          modifier =
              Modifier.size(MaterialTheme.dimens.iconSizeSmall)
                  .testTag(AlertListsScreen.FILTER_FAB_BUBBLE),
          containerColor = MaterialTheme.colorScheme.error,
      )
    }
  }
}

/**
 * Composable function for displaying the filter dialog.
 *
 * @param context The context to use for displaying the dialog.
 * @param currentRadius The current radius value for filtering alerts.
 * @param location The selected location.
 * @param product The selected product.
 * @param urgency The selected urgency level.
 * @param onDismiss A callback function to handle the dialog dismiss event.
 * @param onLocationSelected A callback function to handle the selected location.
 * @param onSave A callback function to handle saving the filter settings.
 * @param onReset A callback function to handle resetting the filter settings.
 * @param locationViewModel The view model for location suggestions.
 * @param gpsService The GPS service that provides the device's geographical coordinates.
 */
@Composable
fun FilterDialog(
    context: android.content.Context,
    currentRadius: Double,
    location: Location?,
    product: String,
    urgency: String,
    onDismiss: () -> Unit,
    onLocationSelected: (Location) -> Unit,
    onSave: (Double, String, String) -> Unit,
    onReset: () -> Unit,
    locationViewModel: LocationViewModel,
    gpsService: GPSServiceImpl,
) {
  var sliderPosition by remember { mutableFloatStateOf(currentRadius.toFloat()) }
  var selectedProduct by remember { mutableStateOf(product) }
  var selectedUrgency by remember { mutableStateOf(urgency) }

  LaunchedEffect(Unit) {
    gpsService.askPermissionAndStartUpdates() // Permission to access location
  }
  Dialog(
      onDismissRequest = onDismiss,
      properties = DialogProperties(usePlatformDefaultWidth = false),
  ) {
    Card(
        modifier =
            Modifier.fillMaxWidth()
                .padding(
                    horizontal = MaterialTheme.dimens.medium3,
                    vertical = MaterialTheme.dimens.small3,
                )
                .testTag(AlertListsScreen.FILTER_DIALOG)
                .wrapContentHeight(),
        shape = RoundedCornerShape(size = MaterialTheme.dimens.cardRoundedSize),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        elevation =
            CardDefaults.cardElevation(defaultElevation = MaterialTheme.dimens.cardElevation),
    ) {
      Column(
          modifier = Modifier.wrapContentSize().padding(MaterialTheme.dimens.medium1),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement =
              Arrangement.spacedBy(MaterialTheme.dimens.small2, Alignment.CenterVertically),
      ) {
        // Instructions for Location
        Text(
            text = FILTER_INSTRUCTION_TEXT,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.testTag(AlertListsScreen.FILTER_DIALOG_TEXT),
            textAlign = TextAlign.Center,
        )

        Divider(
            modifier = Modifier.fillMaxWidth().padding(MaterialTheme.dimens.small2),
            color = MaterialTheme.colorScheme.onSurface,
        )

        // Location Input Field
        LocationField(
            location = location,
            onLocationSelected = onLocationSelected,
            locationViewModel = locationViewModel,
            gpsService = gpsService,
        )

        // Radius Text
        Text(
            text =
                if (sliderPosition < KILOMETERS_IN_METERS)
                    "Radius: $sliderPosition m from your position"
                else "Radius: ${sliderPosition / KILOMETERS_IN_METERS} km from your position",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.testTag(AlertListsScreen.FILTER_RADIUS_TEXT),
            textAlign = TextAlign.Center,
        )

        // Radius slider
        Slider(
            value = sliderPosition,
            onValueChange = { sliderPosition = (it / 100).roundToInt() * 100f }, // Round to 100
            valueRange = MIN_RADIUS.toFloat()..MAX_RADIUS.toFloat(),
            steps = (MAX_RADIUS - MIN_RADIUS) / 100 - 1,
            modifier = Modifier.fillMaxWidth().testTag(AlertListsScreen.FILTER_RADIUS_SLIDER),
        )

        // Product Filter
        ProductField(product) { selectedProduct = it }

        // Urgency Filter
        UrgencyField(urgency) { selectedUrgency = it }

        // Apply Filter button
        ActionButton(
            buttonText = APPLY_FILTER_BUTTON_TEXT,
            onClick = {
              if ((sliderPosition != 100f) &&
                  (location == null)) { // if the user selects a radius but not a location
                Toast.makeText(context, ERROR_MESSAGE_INVALID_LOCATION, Toast.LENGTH_SHORT).show()
              } else {
                onSave(sliderPosition.toDouble(), selectedProduct, selectedUrgency)
                onDismiss()
              }
            },
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            testTag = AlertListsScreen.FILTER_APPLY_BUTTON,
        )

        // Reset Filter button
        ActionButton(
            buttonText = RESET_FILTER_BUTTON_TEXT,
            onClick = {
              sliderPosition = 100f
              onReset()
              onDismiss()
            },
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError,
                ),
            testTag = AlertListsScreen.FILTER_RESET_BUTTON,
        )
      }
    }
  }
}

/**
 * Capitalizes the first character of the given string.
 *
 * @param s The string to be capitalized.
 * @return The capitalized string.
 */
fun capitalized(s: String): String = s.lowercase().replaceFirstChar { it.uppercase() }
