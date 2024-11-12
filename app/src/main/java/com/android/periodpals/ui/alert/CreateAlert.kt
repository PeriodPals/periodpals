package com.android.periodpals.ui.alert

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.periodpals.model.location.Location
import com.android.periodpals.model.location.LocationViewModel
import com.android.periodpals.resources.C.Tag.CreateAlertScreen
import com.android.periodpals.ui.navigation.BottomNavigationMenu
import com.android.periodpals.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import com.android.periodpals.ui.navigation.TopAppBar
import com.android.periodpals.ui.theme.dimens

private const val SCREEN_TITLE = "Create Alert"
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
 * @param locationViewModel The location view model that provides location-related data and
 *   functionality.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAlertScreen(
    navigationActions: NavigationActions,
    locationViewModel: LocationViewModel = viewModel(factory = LocationViewModel.Factory),
) {
  val context = LocalContext.current
  var message by remember { mutableStateOf(DEFAULT_MESSAGE) }
  val (productIsSelected, setProductIsSelected) = remember { mutableStateOf(false) }
  val (urgencyIsSelected, setUrgencyIsSelected) = remember { mutableStateOf(false) }

  var selectedLocation by remember { mutableStateOf<Location?>(null) }
  val locationQuery by locationViewModel.query.collectAsState()

  // State for dropdown visibility
  var showDropdown by remember { mutableStateOf(false) }
  val locationSuggestions by locationViewModel.locationSuggestions.collectAsState()

  // Screen
  Scaffold(
      modifier = Modifier.fillMaxSize().testTag(CreateAlertScreen.SCREEN),
      topBar = { TopAppBar(title = SCREEN_TITLE) },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationActions.currentRoute(),
        )
      },
  ) { paddingValues ->
    // By default scrollable
    LazyColumn(
        modifier =
            Modifier.fillMaxSize()
                .padding(paddingValues)
                .padding(
                    start = MaterialTheme.dimens.medium3,
                    top = MaterialTheme.dimens.small3,
                    end = MaterialTheme.dimens.medium3),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement =
            Arrangement.spacedBy(MaterialTheme.dimens.small2, Alignment.CenterVertically),
    ) {
      // Instruction text
      item {
        Text(
            text = INSTRUCTION_TEXT,
            modifier = Modifier.testTag(CreateAlertScreen.INSTRUCTION_TEXT),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
        )
      }

      // Product dropdown menu
      item {
        ExposedDropdownMenuSample(
            itemsList = PRODUCT_DROPDOWN_CHOICES,
            label = PRODUCT_DROPDOWN_LABEL,
            defaultValue = PRODUCT_DROPDOWN_DEFAULT_VALUE,
            setIsSelected = setProductIsSelected,
            testTag = CreateAlertScreen.PRODUCT_FIELD,
        )
      }

      // Urgency dropdown menu
      item {
        ExposedDropdownMenuSample(
            itemsList = EMERGENCY_DROPDOWN_CHOICES,
            label = EMERGENCY_DROPDOWN_LABEL,
            defaultValue = EMERGENCY_DROPDOWN_DEFAULT_VALUE,
            setIsSelected = setUrgencyIsSelected,
            testTag = CreateAlertScreen.URGENCY_FIELD,
        )
      }

      // Location Input with dropdown using ExposedDropdownMenuBox
      item {
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
              value = locationQuery,
              onValueChange = {
                locationViewModel.setQuery(it)
                showDropdown = true // Show dropdown when user starts typing
              },
              label = {
                Text(text = LOCATION_FIELD_LABEL, style = MaterialTheme.typography.labelMedium)
              },
              placeholder = {
                Text(
                    text = LOCATION_FIELD_PLACEHOLDER, style = MaterialTheme.typography.labelMedium)
              },
              singleLine = true,
              colors = ExposedDropdownMenuDefaults.textFieldColors(),
          )

          // Dropdown menu for location suggestions
          ExposedDropdownMenu(
              expanded = showDropdown && locationSuggestions.isNotEmpty(),
              onDismissRequest = { showDropdown = false },
              modifier = Modifier.wrapContentSize(),
          ) {
            Log.d("CreateAlertScreen", "Location suggestions: ${locationSuggestions}")
            locationSuggestions.take(3).forEach { location ->
              DropdownMenuItem(
                  text = {
                    Text(
                        text =
                            location.name.take(30) +
                                if (location.name.length > 30) "..." else "", // Limit name length
                        maxLines = 1, // Ensure name doesn't overflow
                        style = MaterialTheme.typography.labelLarge)
                  },
                  onClick = {
                    Log.d("CreateAlertScreen", "Selected location: ${location.name}")
                    locationViewModel.setQuery(location.name)
                    selectedLocation = location
                    showDropdown = false // Close dropdown on selection
                  },
                  modifier =
                      Modifier.testTag(CreateAlertScreen.DROPDOWN_ITEM + location.name).semantics {
                        contentDescription = CreateAlertScreen.DROPDOWN_ITEM
                      },
                  contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
              )
            }

            if (locationSuggestions.size > 3) {
              DropdownMenuItem(
                  text = { Text(text = "More...", style = MaterialTheme.typography.labelLarge) },
                  onClick = { /* TODO show more results */},
                  contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
              )
            }
          }
        }
      }

      // Message field
      item {
        var isFocused by remember { mutableStateOf(false) }
        OutlinedTextField(
            modifier =
                Modifier.fillMaxWidth()
                    .wrapContentHeight()
                    .testTag(CreateAlertScreen.MESSAGE_FIELD)
                    .onFocusEvent { focusState -> isFocused = focusState.isFocused },
            value = message,
            onValueChange = { message = it },
            textStyle = MaterialTheme.typography.labelLarge,
            label = {
              Text(
                  text = MESSAGE_FIELD_LABEL,
                  style =
                      if (isFocused || message.isNotEmpty()) MaterialTheme.typography.labelMedium
                      else MaterialTheme.typography.labelLarge)
            },
            placeholder = {
              Text(text = MESSAGE_FIELD_PLACEHOLDER, style = MaterialTheme.typography.labelLarge)
            },
            minLines = 3,
        )
      }

      // "Ask for Help" button
      item {
        Button(
            modifier = Modifier.wrapContentSize().testTag(CreateAlertScreen.SUBMIT_BUTTON),
            onClick = {
              val (isValid, errorMessage) =
                  validateFields(productIsSelected, urgencyIsSelected, selectedLocation, message)
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
      modifier = Modifier.wrapContentSize().testTag(testTag),
      expanded = expanded,
      onExpandedChange = { expanded = it },
  ) {
    TextField(
        modifier = Modifier.fillMaxWidth().wrapContentHeight().menuAnchor(),
        textStyle = MaterialTheme.typography.labelLarge,
        label = { Text(text = label, style = MaterialTheme.typography.labelMedium) },
        value = text,
        onValueChange = {},
        singleLine = true,
        readOnly = true,
        trailingIcon = {
          ExposedDropdownMenuDefaults.TrailingIcon(
              expanded = expanded, Modifier.size(MaterialTheme.dimens.iconSize))
        },
        colors = ExposedDropdownMenuDefaults.textFieldColors(),
    )
    ExposedDropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = Modifier.wrapContentSize(),
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
private fun validateFields(
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
