package com.android.periodpals.ui.alert

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import com.android.periodpals.model.location.Location
import com.android.periodpals.model.location.LocationViewModel
import com.android.periodpals.resources.C.Tag.CreateAlertScreen
import com.android.periodpals.resources.C.Tag.EditAlertScreen
import com.android.periodpals.resources.C.Tag.EditAlertScreen.DELETE_BUTTON
import com.android.periodpals.resources.C.Tag.EditAlertScreen.RESOLVE_BUTTON
import com.android.periodpals.resources.C.Tag.EditAlertScreen.SAVE_BUTTON
import com.android.periodpals.resources.ComponentColor.getFilledPrimaryContainerButtonColors
import com.android.periodpals.ui.components.LocationField
import com.android.periodpals.ui.components.MessageField
import com.android.periodpals.ui.components.productField
import com.android.periodpals.ui.components.urgencyField
import com.android.periodpals.ui.components.validateFields
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import com.android.periodpals.ui.navigation.TopAppBar
import com.android.periodpals.ui.theme.dimens

private const val SCREEN_TITLE = "Edit Your Alert"
private const val INSTRUCTION_TEXT =
    "Edit, delete or resolve your push notification alert for nearby pals. You can leave a review for the sender when you resolve."

private const val DELETE_BUTTON_TEXT = "Delete"
private const val SAVE_BUTTON_TEXT = "Save"
private const val RESOLVE_BUTTON_TEXT = "Resolve"

private const val SUCCESSFUL_UPDATE_TOAST_MESSAGE = "Alert updated"

@Composable
fun EditAlertScreen(
    navigationActions: NavigationActions,
    locationViewModel: LocationViewModel,
    alert: Alert
) {
  val context = LocalContext.current
  var selectedLocation by remember {
    mutableStateOf<Location?>(null)
  } // TODO: replace `null` with mutableStateOf<Location>(alert.location) with parsed location
  var message by remember { mutableStateOf(alert.message) }

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag(EditAlertScreen.SCREEN),
      topBar = {
        TopAppBar(
            title = SCREEN_TITLE,
            backButton = true,
            onBackButtonClick = { navigationActions.navigateTo(Screen.ALERT) })
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
          modifier = Modifier.testTag(CreateAlertScreen.INSTRUCTION_TEXT),
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.bodyMedium,
      )

      // Product dropdown
      val productIsSelected =
          productField(
              product = alert.product.toString(),
              onValueChange = {}) // TODO: onValueChange should fill the product parameter of the
      // alert

      // Urgency dropdown
      val urgencyIsSelected =
          urgencyField(
              urgency = alert.urgency.toString(),
              onValueChange = {}) // TODO: onValueChange should fill the urgency parameter of the
      // alert

      // Location field
      LocationField(
          location = selectedLocation,
          locationViewModel = locationViewModel,
          onLocationSelected = { selectedLocation = it })

      // Message field
      MessageField(text = message, onValueChange = { message = it })

      Row(
          horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.small3),
          verticalAlignment = Alignment.CenterVertically,
      ) {
        Button(
            modifier = Modifier.wrapContentSize().testTag(DELETE_BUTTON),
            onClick = {
              // TODO: delete alert
              navigationActions.navigateTo(Screen.ALERT_LIST)
            },
            colors = getFilledPrimaryContainerButtonColors(),
        ) {
          Text(text = DELETE_BUTTON_TEXT, style = MaterialTheme.typography.headlineMedium)
        }
        Button(
            modifier = Modifier.wrapContentSize().testTag(SAVE_BUTTON),
            onClick = {
              val (isValid, errorMessage) =
                  validateFields(productIsSelected, urgencyIsSelected, selectedLocation, message)
              if (!isValid) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
              } else {
                Toast.makeText(context, SUCCESSFUL_UPDATE_TOAST_MESSAGE, Toast.LENGTH_SHORT).show()
                navigationActions.navigateTo(Screen.ALERT_LIST)
              }
              // TODO: update alert using view model
            },
            colors = getFilledPrimaryContainerButtonColors(),
        ) {
          Text(text = SAVE_BUTTON_TEXT, style = MaterialTheme.typography.headlineMedium)
        }
        Button(
            modifier = Modifier.wrapContentSize().testTag(RESOLVE_BUTTON),
            onClick = {
              // TODO: resolve alert
              navigationActions.navigateTo(Screen.ALERT_LIST)
            },
            colors = getFilledPrimaryContainerButtonColors(),
        ) {
          Text(text = RESOLVE_BUTTON_TEXT, style = MaterialTheme.typography.headlineMedium)
        }
      }
    }
  }
}
