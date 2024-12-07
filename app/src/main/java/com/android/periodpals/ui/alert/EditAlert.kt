package com.android.periodpals.ui.alert

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
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
import com.android.periodpals.model.alert.Alert
import com.android.periodpals.model.alert.AlertViewModel
import com.android.periodpals.model.alert.Product
import com.android.periodpals.model.alert.Urgency
import com.android.periodpals.model.alert.productToPeriodPalsIcon
import com.android.periodpals.model.alert.stringToProduct
import com.android.periodpals.model.alert.stringToUrgency
import com.android.periodpals.model.alert.urgencyToPeriodPalsIcon
import com.android.periodpals.model.location.Location
import com.android.periodpals.model.location.LocationViewModel
import com.android.periodpals.resources.C.Tag.AlertInputs
import com.android.periodpals.resources.C.Tag.EditAlertScreen
import com.android.periodpals.resources.C.Tag.EditAlertScreen.DELETE_BUTTON
import com.android.periodpals.resources.C.Tag.EditAlertScreen.RESOLVE_BUTTON
import com.android.periodpals.resources.C.Tag.EditAlertScreen.SAVE_BUTTON
import com.android.periodpals.resources.ComponentColor.getFilledPrimaryContainerButtonColors
import com.android.periodpals.services.GPSServiceImpl
import com.android.periodpals.ui.components.ActionButton
import com.android.periodpals.ui.components.LocationField
import com.android.periodpals.ui.components.MessageField
import com.android.periodpals.ui.components.ProductField
import com.android.periodpals.ui.components.UrgencyField
import com.android.periodpals.ui.components.validateFields
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import com.android.periodpals.ui.navigation.TopAppBar
import com.android.periodpals.ui.theme.dimens

private const val SCREEN_TITLE = "Edit Your Alert"
private const val INSTRUCTION_TEXT =
    "Edit, delete or resolve your push notification alert for nearby pals." +
        " You can leave a review for the sender when you resolve."

private const val DELETE_BUTTON_TEXT = "Delete"
private const val SAVE_BUTTON_TEXT = "Save"
private const val RESOLVE_BUTTON_TEXT = "Resolve"

private const val SUCCESSFUL_UPDATE_TOAST_MESSAGE = "Alert updated"
private const val NOT_IMPLEMENTED_YET_TOAST_MESSAGE = "This feature is not implemented yet"

private const val TAG = "EditAlertScreen"

/**
 * Composable function to display the Edit Alert screen.
 *
 * @param locationViewModel ViewModel to manage location data.
 * @param gpsService The GPS service that provides the device's geographical coordinates.
 * @param alertViewModel ViewModel to manage alert data. Used to update the alert in the repository.
 * @param navigationActions The navigation actions to handle navigation events.
 */
@Composable
fun EditAlertScreen(
    locationViewModel: LocationViewModel,
    gpsService: GPSServiceImpl,
    alertViewModel: AlertViewModel,
    navigationActions: NavigationActions,
) {
  val context = LocalContext.current

  val alert =
      alertViewModel.selectedAlert.value
          ?: run {
            Log.e(TAG, "EditAlertScreen: selectedAlert is null")
            Toast.makeText(context, "No selected alert", Toast.LENGTH_SHORT).show()
            navigationActions.navigateTo(Screen.ALERT_LIST)
            return
          }

  var product by remember { mutableStateOf<Product?>(alert.product) }
  var urgency by remember { mutableStateOf<Urgency?>(alert.urgency) }
  var selectedLocation by remember {
    mutableStateOf<Location?>(Location.fromString(alert.location))
  }
  var message by remember { mutableStateOf(alert.message) }

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag(EditAlertScreen.SCREEN),
      topBar = {
        TopAppBar(
            title = SCREEN_TITLE,
            backButton = true,
            onBackButtonClick = { navigationActions.navigateTo(Screen.ALERT_LIST) })
      },
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
          modifier = Modifier.testTag(AlertInputs.INSTRUCTION_TEXT),
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.bodyMedium,
      )

      // Product dropdown
      ProductField(
          product = if (product == null) "" else productToPeriodPalsIcon(product!!).textId,
          onValueChange = { product = stringToProduct(it) },
      )

      // Urgency dropdown
      UrgencyField(
          urgency = if (urgency == null) "" else urgencyToPeriodPalsIcon(urgency!!).textId,
          onValueChange = { urgency = stringToUrgency(it) },
      )

      // Location field
      LocationField(
          location = selectedLocation,
          locationViewModel = locationViewModel,
          onLocationSelected = { selectedLocation = it },
          gpsService)

      // Message field
      MessageField(text = message, onValueChange = { message = it })

      // Delete, save, and resolve buttons
      Row(
          horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.small2),
          verticalAlignment = Alignment.CenterVertically,
      ) {
        ActionButton(
            buttonText = DELETE_BUTTON_TEXT,
            onClick = {
              alertViewModel.deleteAlert(
                  alert.id,
                  onSuccess = {
                    Toast.makeText(context, "Alert deleted", Toast.LENGTH_SHORT).show()
                    navigationActions.navigateTo(Screen.ALERT_LIST)
                  },
                  onFailure = { e ->
                    Log.e(TAG, "deleteAlert: fail to delete alert: ${e.message}")
                  },
              )
              navigationActions.navigateTo(Screen.ALERT_LIST)
            },
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError,
                ),
            testTag = DELETE_BUTTON)

        ActionButton(
            buttonText = SAVE_BUTTON_TEXT,
            onClick = {
              val (isValid, errorMessage) =
                  validateFields(product, urgency, selectedLocation, message)
              if (!isValid) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
              } else {
                Toast.makeText(context, SUCCESSFUL_UPDATE_TOAST_MESSAGE, Toast.LENGTH_SHORT).show()
                val newAlert =
                    Alert(
                        id = alert.id,
                        uid = alert.uid,
                        name = alert.name,
                        product = product!!,
                        urgency = urgency!!,
                        createdAt = alert.createdAt,
                        location = selectedLocation.toString(),
                        message = message,
                        status = alert.status, // TODO: handle this properly
                    )
                alertViewModel.updateAlert(
                    newAlert,
                    onSuccess = { Log.d(TAG, "Alert successfully updated") },
                    onFailure = { e ->
                      Log.e(TAG, "updateAlert: fail to update alert: ${e.message}")
                    })
                navigationActions.navigateTo(Screen.ALERT_LIST)
              }
            },
            colors = getFilledPrimaryContainerButtonColors(),
            testTag = SAVE_BUTTON)

        ActionButton(
            buttonText = RESOLVE_BUTTON_TEXT,
            onClick = {
              // TODO: resolve alert
              Toast.makeText(context, NOT_IMPLEMENTED_YET_TOAST_MESSAGE, Toast.LENGTH_SHORT).show()
              navigationActions.navigateTo(Screen.ALERT_LIST)
            },
            colors = getFilledPrimaryContainerButtonColors(),
            testTag = RESOLVE_BUTTON)
      }
    }
  }
}
