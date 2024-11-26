package com.android.periodpals.ui.alert

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.periodpals.model.location.Location
import com.android.periodpals.model.location.LocationViewModel
import com.android.periodpals.resources.C.Tag.CreateAlertScreen
import com.android.periodpals.resources.ComponentColor.getFilledPrimaryContainerButtonColors
import com.android.periodpals.services.GPSServiceImpl
import com.android.periodpals.ui.components.ActionButton
import com.android.periodpals.ui.components.LocationField
import com.android.periodpals.ui.components.MessageField
import com.android.periodpals.ui.components.productField
import com.android.periodpals.ui.components.urgencyField
import com.android.periodpals.ui.components.validateFields
import com.android.periodpals.ui.navigation.BottomNavigationMenu
import com.android.periodpals.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import com.android.periodpals.ui.navigation.TopAppBar
import com.android.periodpals.ui.theme.dimens

private const val SCREEN_TITLE = "Create Alert"
private const val INSTRUCTION_TEXT =
    "Push a notification to users near you! If they are available and have the products you need, they'll be able to help you!"

private const val PRODUCT_DROPDOWN_DEFAULT_VALUE = "Please choose a product"
private const val URGENCY_DROPDOWN_DEFAULT_VALUE = "Please choose an urgency level"
private const val DEFAULT_MESSAGE = ""

private const val SUCCESSFUL_SUBMISSION_TOAST_MESSAGE = "Alert sent"
private const val SUBMISSION_BUTTON_TEXT = "Ask for Help"

/**
 * Composable function for the CreateAlert screen.
 *
 * @param locationViewModel The location view model that provides location-related data and functionality.
 * @param gpsService The GPS service that provides the device's geographical coordinates.
 * @param navigationActions The navigation actions to handle navigation events.
 */
@Composable
fun CreateAlertScreen(
  locationViewModel: LocationViewModel = viewModel(factory = LocationViewModel.Factory),
  gpsService: GPSServiceImpl,
  navigationActions: NavigationActions
) {
  val context = LocalContext.current
  var message by remember { mutableStateOf(DEFAULT_MESSAGE) }
  var selectedLocation by remember { mutableStateOf<Location?>(null) }

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
      containerColor = MaterialTheme.colorScheme.surface,
      contentColor = MaterialTheme.colorScheme.onSurface,
  ) { paddingValues ->
    Column(
        modifier =
            Modifier.fillMaxSize()
                .padding(paddingValues)
                .padding(
                    horizontal = MaterialTheme.dimens.medium3,
                    vertical = MaterialTheme.dimens.small3,
                )
                .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement =
            Arrangement.spacedBy(MaterialTheme.dimens.small2, Alignment.CenterVertically),
    ) {
      // Instruction text
      Text(
          text = INSTRUCTION_TEXT,
          modifier = Modifier.testTag(CreateAlertScreen.INSTRUCTION_TEXT),
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.bodyMedium,
      )

      // Product dropdown menu
      val productIsSelected =
          productField(
              product = PRODUCT_DROPDOWN_DEFAULT_VALUE,
              onValueChange = {}) // TODO: onValueChange should fill the product parameter of the
      // alert

      // Urgency dropdown menu
      val urgencyIsSelected =
          urgencyField(
              urgency = URGENCY_DROPDOWN_DEFAULT_VALUE,
              onValueChange = {}) // TODO: onValueChange should fill the urgency parameter of the
      // alert

      // Location field
      LocationField(
          location = selectedLocation,
          locationViewModel = locationViewModel,
          onLocationSelected = { selectedLocation = it },
          gpsService)

      // Message field
      MessageField(text = message, onValueChange = { message = it })

      // "Ask for Help" button
      ActionButton(
          buttonText = SUBMISSION_BUTTON_TEXT,
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
          colors = getFilledPrimaryContainerButtonColors(),
          testTag = CreateAlertScreen.SUBMIT_BUTTON,
      )
    }
  }
}
