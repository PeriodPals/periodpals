package com.android.periodpals.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.android.periodpals.model.alert.Alert
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
import com.android.periodpals.ui.theme.dimens

private const val EDIT_BUTTON_TEXT = "Edit"
private const val ACCEPT_BUTTON_TEXT = "Accept"
private const val RESOLVE_BUTTON_TEXT = "Resolve"

private const val TAG = "AlertComponents"

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
 * @param content Determines which buttons are displayed
 * @param onSheetDismissRequest Executed when the bottom sheet is dismissed
 * @param alertToDisplay Alert to be displayed in the bottom sheet
 * @param onEditClick Callback run when the edit button is pressed
 * @param onAcceptClick Callback run when the accept button is pressed
 * @param onResolveClick Callback run when the resolve button is pressed
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapBottomSheet(
    sheetState: SheetState,
    content: CONTENT,
    onSheetDismissRequest: () -> Unit,
    alertToDisplay: Alert?,
    onEditClick: () -> Unit,
    onAcceptClick: () -> Unit,
    onResolveClick: () -> Unit,
) {

  ModalBottomSheet(
      onDismissRequest = onSheetDismissRequest,
      sheetState = sheetState,
      modifier = Modifier.fillMaxWidth().wrapContentHeight().testTag(BOTTOM_SHEET),
  ) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.small2),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(MaterialTheme.dimens.small3),
    ) {
      alertToDisplay?.let { AlertInfo(it) } ?: Log.d(TAG, "Alert is null")

      InteractionButtons(
          content = content,
          onEditClick = onEditClick,
          onAccpetClick = { TODO("TO be implemented") },
          onResolveClick = { TODO("To be implemented") },
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
  Column(
      verticalArrangement =
          Arrangement.spacedBy(MaterialTheme.dimens.small2, Alignment.CenterVertically)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
          Row(
              horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.small2),
              verticalAlignment = Alignment.CenterVertically,
          ) {
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
                val locationText = trimLocationText(Location.fromString(alert.location).name)

                // Location
                Text(
                    text = locationText,
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
          }

          val periodPalsProduct = productToPeriodPalsIcon(alert.product)
          val periodPalsUrgency = urgencyToPeriodPalsIcon(alert.urgency)

          Row(
              horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.small2),
          ) {
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
        }

        Text(
            text = alert.message,
            modifier = Modifier.padding(MaterialTheme.dimens.small2).testTag(ALERT_MESSAGE),
            style = MaterialTheme.typography.bodyMedium,
        )
      }
}

/**
 * Depending on who posted the alert (the current user or a pal), it correspondingly displays:
 * - an edit button along with a resolve button
 * - an accept button
 *
 * @param content Determines which buttons are displayed
 * @param onEditClick Callback run whenever the user clicks on the edit button.
 */
@Composable
private fun InteractionButtons(
    content: CONTENT,
    onEditClick: () -> Unit,
    onAccpetClick: () -> Unit,
    onResolveClick: () -> Unit,
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
            onClick = onEditClick,
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

        // Accept
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
