package com.android.periodpals.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.android.periodpals.model.alert.Alert
import com.android.periodpals.model.alert.AlertViewModel
import com.android.periodpals.model.alert.productToPeriodPalsIcon
import com.android.periodpals.model.alert.urgencyToPeriodPalsIcon
import com.android.periodpals.model.location.Location
import com.android.periodpals.resources.C.Tag.MapScreen.ACCEPT_ALERT_BUTTON
import com.android.periodpals.resources.C.Tag.MapScreen.ALERT_LOCATION_TEXT
import com.android.periodpals.resources.C.Tag.MapScreen.ALERT_MESSAGE
import com.android.periodpals.resources.C.Tag.MapScreen.ALERT_PRODUCT_ICON
import com.android.periodpals.resources.C.Tag.MapScreen.ALERT_TIME_TEXT
import com.android.periodpals.resources.C.Tag.MapScreen.ALERT_URGENCY_ICON
import com.android.periodpals.resources.C.Tag.MapScreen.BOTTOM_SHEET
import com.android.periodpals.resources.C.Tag.MapScreen.EDIT_ALERT_BUTTON
import com.android.periodpals.resources.C.Tag.MapScreen.PROFILE_NAME
import com.android.periodpals.resources.C.Tag.MapScreen.PROFILE_PICTURE
import com.android.periodpals.resources.C.Tag.MapScreen.RESOLVE_ALERT_BUTTON
import com.android.periodpals.resources.ComponentColor.getFilledPrimaryContainerButtonColors
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import com.android.periodpals.ui.theme.dimens

private const val EDIT_BUTTON_TEXT = "Edit"
private const val ACCEPT_BUTTON_TEXT = "Accept"
private const val RESOLVE_BUTTON_TEXT = "Resolve"

private const val TAG = "MapComponents"

private const val TEXT_LENGTH_LIMIT = 30

enum class CONTENT {
  MY_ALERT,
  PAL_ALERT,
}

/**
 * Bottom sheet that appears when the user clicks on an alert in the map. It displays the basic
 * information of the alert and the user that posted on it.
 *
 * Note:
 * - dismiss means that the bottom sheet is completely removed from the composition.
 * - hide means that the bottom sheet is not visible but still in the composition.
 *
 * @param sheetState State of the bottom sheet
 * @param onDismissRequest Executed when the bottom sheet is dismissed
 * @param onHideRequest Executed when the bottom sheet is hidden
 * @param content Determines which buttons are displayed
 * @param alertViewModel Manages the alert data
 * @param navigationActions Manages the app navigation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapBottomSheet(
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    onHideRequest: () -> Unit,
    content: CONTENT,
    alertViewModel: AlertViewModel,
    navigationActions: NavigationActions
) {

  ModalBottomSheet(
      onDismissRequest = onDismissRequest,
      sheetState = sheetState,
      modifier = Modifier.testTag(BOTTOM_SHEET),
  ) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.small1),
        modifier =
            Modifier.padding(
                start = MaterialTheme.dimens.small3,
                end = MaterialTheme.dimens.small3,
                bottom = MaterialTheme.dimens.small3,
            ),
    ) {
      AlertInfo(alertViewModel.selectedAlert.value!!)
      InteractionButtons(
        content = content,
        alert = alertViewModel.selectedAlert.value!!,
        alertViewModel = alertViewModel,
        navigationActions = navigationActions,
      )
    }
  }
}

/**
 * Displays the alert info: name of user, location, time, product type and urgency.
 *
 * @param alert Alert whose info will be displayed
*/
@Composable
private fun AlertInfo(alert: Alert) {
  Column {
    Row(
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.small3),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()) {

          // Profile picture
          Icon(
              imageVector = Icons.Outlined.AccountCircle, // TODO fetch from database
              contentDescription = "Profile picture",
              modifier =
                  Modifier.size(MaterialTheme.dimens.iconSize)
                      .wrapContentSize()
                      .testTag(PROFILE_PICTURE),
          )

          Column(verticalArrangement = Arrangement.Center) {

            // Name
            Text(
                text = alert.name,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Left,
                modifier = Modifier.testTag(PROFILE_NAME),
            )

            Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.small1)) {
              val location = Location.fromString(alert.location).name
              val trimmedLocation =
                  if (location.length >= TEXT_LENGTH_LIMIT) location.take(TEXT_LENGTH_LIMIT) + "..."
                  else location

              // Location
              Text(
                  text = trimmedLocation,
                  style = MaterialTheme.typography.bodySmall,
                  textAlign = TextAlign.Left,
                  modifier = Modifier.testTag(ALERT_LOCATION_TEXT),
              )

              // Time
              Text(
                  text = formatAlertTime(alert.createdAt),
                  style = MaterialTheme.typography.bodySmall,
                  textAlign = TextAlign.Left,
                  modifier = Modifier.testTag(ALERT_TIME_TEXT),
              )
            }
          }

          val periodPalsProduct = productToPeriodPalsIcon(alert.product)
          val periodPalsUrgency = urgencyToPeriodPalsIcon(alert.urgency)

          // Product type
          Icon(
              painter = painterResource(periodPalsProduct.icon),
              contentDescription = periodPalsProduct.textId + " product",
              modifier = Modifier.testTag(ALERT_PRODUCT_ICON),
          )

          // Urgency level
          Icon(
              painter = painterResource(periodPalsUrgency.icon),
              contentDescription = periodPalsUrgency.textId + " urgency",
              modifier = Modifier.testTag(ALERT_URGENCY_ICON),
          )
        }
    OutlinedCard {
      Text(
          text = alert.message,
          modifier = Modifier.padding(MaterialTheme.dimens.small2).testTag(ALERT_MESSAGE),
          style = MaterialTheme.typography.bodyMedium,
      )
    }
  }
}

/**
 * Depending on who posted the alert (the current user or a pal), it correspondingly displays:
 * - an edit button along with a resolve button
 * - an accept button
 *
 * @param content Determines which buttons are displayed
 * @param alert Alert whose info will be displayed
 * @param alertViewModel Manages the alert data
 * @param navigationActions Manages the app navigation
 */
@Composable
private fun InteractionButtons(
  content: CONTENT,
  alert: Alert,
  alertViewModel: AlertViewModel,
  navigationActions: NavigationActions
) {
  Row(
      horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.small2),
      verticalAlignment = Alignment.CenterVertically,
  ) {

    // Dynamically compose different buttons
    when (content) {
      CONTENT.MY_ALERT -> {

        // Edit
        Button(
            onClick = {
              alertViewModel.selectAlert(alert)
              navigationActions.navigateTo(Screen.EDIT_ALERT)
            },
            modifier = Modifier.wrapContentSize().testTag(EDIT_ALERT_BUTTON),
            colors = getFilledPrimaryContainerButtonColors(),
        ) {
          Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Edit alert")
          Spacer(modifier = Modifier.width(MaterialTheme.dimens.small2))
          Text(text = EDIT_BUTTON_TEXT, style = MaterialTheme.typography.bodyLarge)
        }

        // Resolve
        Button(
            onClick = {
              // TODO Implement alert resolution
            },
            modifier = Modifier.wrapContentSize().testTag(RESOLVE_ALERT_BUTTON),
            colors = getFilledPrimaryContainerButtonColors(),
        ) {
          Icon(imageVector = Icons.Outlined.Check, contentDescription = "Resolve alert")
          Spacer(modifier = Modifier.width(MaterialTheme.dimens.small2))
          Text(text = RESOLVE_BUTTON_TEXT, style = MaterialTheme.typography.bodyLarge)
        }
      }

      CONTENT.PAL_ALERT -> {

        // Edit
        Button(
            onClick = {
              // TODO Implement alert accept
            },
            modifier = Modifier.wrapContentSize().testTag(ACCEPT_ALERT_BUTTON),
            colors = getFilledPrimaryContainerButtonColors(),
        ) {
          Icon(imageVector = Icons.Outlined.Check, contentDescription = "Accept alert")
          Spacer(modifier = Modifier.width(MaterialTheme.dimens.small2))
          Text(text = ACCEPT_BUTTON_TEXT, style = MaterialTheme.typography.bodyLarge)
        }
      }
    }
  }
}
