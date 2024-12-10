package com.android.periodpals.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.android.periodpals.model.alert.LIST_OF_PRODUCTS
import com.android.periodpals.model.alert.LIST_OF_URGENCIES
import com.android.periodpals.resources.C.Tag.MapScreen.ALERT_LOCATION_TEXT
import com.android.periodpals.resources.C.Tag.MapScreen.ALERT_MESSAGE
import com.android.periodpals.resources.C.Tag.MapScreen.ALERT_PRODUCT_ICON
import com.android.periodpals.resources.C.Tag.MapScreen.ALERT_TIME_TEXT
import com.android.periodpals.resources.C.Tag.MapScreen.ALERT_URGENCY_ICON
import com.android.periodpals.resources.C.Tag.MapScreen.BOTTOM_SHEET
import com.android.periodpals.resources.C.Tag.MapScreen.PROFILE_NAME
import com.android.periodpals.resources.C.Tag.MapScreen.PROFILE_PICTURE
import com.android.periodpals.resources.ComponentColor.getFilledPrimaryContainerButtonColors
import com.android.periodpals.ui.theme.dimens

private const val EDIT_BUTTON_TEXT = "Edit"
private const val ACCEPT_BUTTON_TEXT = "Accept"
private const val RESOLVE_BUTTON_TEXT = "Resolve"

/**
 * Bottom sheet that appears when the user clicks on an alert in the map. It displays the basic
 * information of the alert and the user that posted on it.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapBottomSheet(
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    onHideRequest: () -> Unit
) {

  ModalBottomSheet(
      onDismissRequest = onDismissRequest,
      sheetState = sheetState,
      modifier = Modifier.testTag(BOTTOM_SHEET)) {
        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.small1),
            modifier =
                Modifier.padding(
                    start = MaterialTheme.dimens.small3,
                    end = MaterialTheme.dimens.small3,
                    bottom = MaterialTheme.dimens.small3)) {
              AlertInfo()

              OutlinedCard {
                Text(
                    text = "", // TODO fetch from database
                    modifier = Modifier.padding(MaterialTheme.dimens.small2).testTag(ALERT_MESSAGE))
              }

              InteractionButtons()
            }
      }
}

/**
 * Displays:
 * - the profile picture and name of the user that posted the alert
 * - the location, time, product type and urgency level of the alert
 */
@Composable
private fun AlertInfo(modifier: Modifier = Modifier) {
  Row(
      horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.small2),
      verticalAlignment = Alignment.CenterVertically) {

        // Profile picture
        Icon(
            imageVector = Icons.Outlined.AccountCircle, // TODO fetch from database
            contentDescription = "Profile picture",
            modifier =
                Modifier.size(MaterialTheme.dimens.iconSize)
                    .wrapContentSize()
                    .testTag(PROFILE_PICTURE))

        Column {

          // Name
          Text(
              text = "Bruno Lazarini", // TODO fetch from database
              style = MaterialTheme.typography.bodyLarge,
              textAlign = TextAlign.Left,
              modifier = Modifier.testTag(PROFILE_NAME))

          Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.small1)) {

            // Location
            Text(
                text = "EPFL", // TODO fetch from database
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Left,
                modifier = Modifier.testTag(ALERT_LOCATION_TEXT))

            // Time
            Text(
                text = "17:00",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Left,
                modifier = Modifier.testTag(ALERT_TIME_TEXT))
          }
        }

        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Top) {

              // Product type
              Icon(
                  painter = painterResource(LIST_OF_PRODUCTS[0].icon),
                  contentDescription = "Product type icon",
                  modifier = Modifier.testTag(ALERT_PRODUCT_ICON))

              // Urgency level
              Icon(
                  painter = painterResource(LIST_OF_URGENCIES[0].icon),
                  contentDescription = "Urgency level icon",
                  modifier = Modifier.testTag(ALERT_URGENCY_ICON))
            }
      }
}

/**
 * Depending on who posted the alert (the current user or a pal), it correspondingly displays:
 * - an edit button along with a resolve button
 * - an accept button
 */
@Composable
private fun InteractionButtons(modifier: Modifier = Modifier) {
  Row(
      horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.small2),
      verticalAlignment = Alignment.CenterVertically) {
        Button(
            onClick = {},
            modifier = Modifier.wrapContentSize().testTag(""),
            colors = getFilledPrimaryContainerButtonColors()) {
              Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Edit alert")
              Spacer(modifier = Modifier.width(MaterialTheme.dimens.small2))
              Text(text = EDIT_BUTTON_TEXT, style = MaterialTheme.typography.bodyLarge)
            }

        Button(
            onClick = {},
            modifier = Modifier.wrapContentSize().testTag(""),
            colors = getFilledPrimaryContainerButtonColors()) {
              Icon(imageVector = Icons.Outlined.Check, contentDescription = "Edit alert")
              Spacer(modifier = Modifier.width(MaterialTheme.dimens.small2))
              Text(text = "Resolve", style = MaterialTheme.typography.bodyLarge)
            }
      }
}
