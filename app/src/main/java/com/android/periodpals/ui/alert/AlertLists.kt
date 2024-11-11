package com.android.periodpals.ui.alert

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.SentimentVerySatisfied
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.android.periodpals.model.alert.Alert
import com.android.periodpals.model.alert.Status
import com.android.periodpals.resources.C.Tag.AlertListsScreen
import com.android.periodpals.resources.C.Tag.AlertListsScreen.MyAlertItem
import com.android.periodpals.resources.C.Tag.AlertListsScreen.PalsAlertItem
import com.android.periodpals.ui.navigation.BottomNavigationMenu
import com.android.periodpals.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.TopAppBar
import com.android.periodpals.ui.theme.dimens
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val SELECTED_TAB_DEFAULT = AlertListsTab.MY_ALERTS
private const val SCREEN_TITLE = "Alert Lists"
private const val MY_ALERTS_TAB_TITLE = "My Alerts"
private const val PALS_ALERTS_TAB_TITLE = "Pals Alerts"
private const val NO_MY_ALERTS_DIALOG = "You haven't asked for help yet !"
private const val NO_PAL_ALERTS_DIALOG = "No pal needs help yet !"
private const val MY_ALERT_EDIT_TEXT = "Edit"
private const val PAL_ALERT_ACCEPT_TEXT = "Accept"
private const val PAL_ALERT_DECLINE_TEXT = "Decline"
private const val TO_IMPLEMENT_EDIT_ALERT = "To implement edit alert screen"
private const val TO_IMPLEMENT_ACCEPT_ALERT = "To implement accept alert action"
private const val TO_IMPLEMENT_DECLINE_ALERT = "To implement decline alert action"

/** Enum class representing the tabs in the AlertLists screen. */
private enum class AlertListsTab {
  MY_ALERTS,
  PALS_ALERTS,
}

/**
 * Composable function that displays the AlertLists screen. It includes a top app bar, tab row for
 * switching between "My Alerts" and "Pals Alerts" tabs, and a bottom navigation menu.
 *
 * @param navigationActions The navigation actions for handling navigation events.
 */
@Composable
fun AlertListsScreen(
    navigationActions: NavigationActions,
    myAlertsList: List<Alert> = emptyList(),
    palsAlertsList: List<Alert> = emptyList(),
) {
  val context = LocalContext.current
  var selectedTab by remember { mutableStateOf(SELECTED_TAB_DEFAULT) }
  var clickedStates by remember { mutableStateOf(palsAlertsList.associate { it.id to false }) }

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag(AlertListsScreen.SCREEN),
      topBar = {
        Column(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
          TopAppBar(title = SCREEN_TITLE)
          TabRow(
              selectedTabIndex = selectedTab.ordinal,
              modifier =
                  Modifier.fillMaxWidth().wrapContentHeight().testTag(AlertListsScreen.TAB_ROW),
          ) {
            Tab(
                modifier = Modifier.wrapContentSize().testTag(AlertListsScreen.MY_ALERTS_TAB),
                text = {
                  Text(text = MY_ALERTS_TAB_TITLE, style = MaterialTheme.typography.headlineSmall)
                },
                selected = selectedTab == AlertListsTab.MY_ALERTS,
                onClick = { selectedTab = AlertListsTab.MY_ALERTS },
            )
            Tab(
                modifier = Modifier.wrapContentSize().testTag(AlertListsScreen.PALS_ALERTS_TAB),
                text = {
                  Text(text = PALS_ALERTS_TAB_TITLE, style = MaterialTheme.typography.headlineSmall)
                },
                selected = selectedTab == AlertListsTab.PALS_ALERTS,
                onClick = {
                  selectedTab = AlertListsTab.PALS_ALERTS
                  clickedStates = palsAlertsList.associate { it.id to false }
                },
            )
          }
        }
      },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationActions.currentRoute(),
        )
      },
  ) { paddingValues ->
    if (selectedTab == AlertListsTab.MY_ALERTS && myAlertsList.isEmpty()) {
      NoAlertDialog(NO_MY_ALERTS_DIALOG)
    } else if (selectedTab == AlertListsTab.PALS_ALERTS && palsAlertsList.isEmpty()) {
      NoAlertDialog(NO_PAL_ALERTS_DIALOG)
    } else {
      LazyColumn(
          modifier =
              Modifier.fillMaxSize()
                  .padding(paddingValues)
                  .padding(
                      horizontal = MaterialTheme.dimens.medium3,
                      vertical = MaterialTheme.dimens.small3),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.small2, Alignment.Top),
      ) {
        when (selectedTab) {
          AlertListsTab.MY_ALERTS -> items(myAlertsList) { alert -> MyAlertItem(alert, context) }
          AlertListsTab.PALS_ALERTS ->
              items(palsAlertsList) { alert ->
                PalAlertItem(
                    alert = alert,
                    context = context,
                    isClicked = clickedStates[alert.id] ?: false,
                    onClick = { isClicked ->
                      clickedStates =
                          clickedStates.toMutableMap().apply { put(alert.id, isClicked) }
                    })
              }
        }
      }
    }
  }
}

/**
 * Composable function that displays an individual alert item. It includes details such as profile
 * picture, time, location, product type, urgency and edit button.
 *
 * @param alert The alert to be displayed.
 * @param context The context to display the toast messages.
 */
@Composable
private fun MyAlertItem(alert: Alert, context: Context) {
  val alertId = alert.id.toString()
  Card(
      modifier =
          Modifier.fillMaxWidth().wrapContentHeight().testTag(MyAlertItem.MY_ALERT + alertId),
      shape = RoundedCornerShape(size = MaterialTheme.dimens.cardRounded),
      colors = CardDefaults.elevatedCardColors(),
      elevation = CardDefaults.cardElevation(MaterialTheme.dimens.small1),
  ) {
    Row(
        modifier =
            Modifier.fillMaxWidth()
                .wrapContentHeight()
                .padding(
                    horizontal = MaterialTheme.dimens.small3,
                    vertical = MaterialTheme.dimens.small1),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.small3, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically,
    ) {
      // My profile picture
      AlertProfilePicture(alertId)

      Column(
          modifier = Modifier.fillMaxWidth().wrapContentHeight().weight(1f),
          horizontalAlignment = Alignment.Start,
          verticalArrangement =
              Arrangement.spacedBy(MaterialTheme.dimens.small1, Alignment.CenterVertically),
      ) {
        // Time, location
        AlertTimeAndLocation(alert, alertId)

        // Product type and urgency
        AlertProductAndUrgency(alertId)
      }

      // Edit alert button
      Button(
          onClick = { Toast.makeText(context, TO_IMPLEMENT_EDIT_ALERT, Toast.LENGTH_SHORT).show() },
          enabled = true,
          modifier = Modifier.wrapContentSize().testTag(MyAlertItem.MY_EDIT_BUTTON + alertId),
      ) {
        Row(
            modifier = Modifier.wrapContentSize(),
            horizontalArrangement =
                Arrangement.spacedBy(MaterialTheme.dimens.small1, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
          // Edit alert icon
          Icon(
              imageVector = Icons.Outlined.Edit,
              contentDescription = "Edit Alert",
              modifier = Modifier.size(MaterialTheme.dimens.iconSizeSmall),
          )
          // Edit alert text
          Text(
              text = MY_ALERT_EDIT_TEXT,
              style = MaterialTheme.typography.labelMedium,
              modifier = Modifier.wrapContentSize(),
          )
        }
      }
    }
  }
}

/**
 * Composable function that displays an individual alert item. It includes details such as profile
 * picture, time, location, name, message, product type, and urgency.
 */
@Composable
fun PalAlertItem(alert: Alert, context: Context, isClicked: Boolean, onClick: (Boolean) -> Unit) {
  val alertId = alert.id.toString()
  Card(
      modifier =
          Modifier.fillMaxWidth().wrapContentHeight().testTag(PalsAlertItem.PAL_ALERT + alertId),
      onClick = { onClick(!isClicked) },
      shape = RoundedCornerShape(size = MaterialTheme.dimens.cardRounded),
      colors = CardDefaults.elevatedCardColors(),
      elevation = CardDefaults.cardElevation(MaterialTheme.dimens.small1),
  ) {
    Column(
        modifier =
            Modifier.fillMaxWidth()
                .wrapContentHeight()
                .padding(
                    horizontal = MaterialTheme.dimens.small3,
                    vertical = MaterialTheme.dimens.small1),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement =
            Arrangement.spacedBy(MaterialTheme.dimens.small1, Alignment.CenterVertically),
    ) {
      Row(
          modifier = Modifier.fillMaxWidth().wrapContentHeight(),
          horizontalArrangement =
              Arrangement.spacedBy(MaterialTheme.dimens.small3, Alignment.Start),
          verticalAlignment = Alignment.CenterVertically) {
            // Pal's profile picture
            AlertProfilePicture(alertId)

            Column(
                modifier = Modifier.fillMaxWidth().wrapContentHeight().weight(1f),
                horizontalAlignment = Alignment.Start,
                verticalArrangement =
                    Arrangement.spacedBy(MaterialTheme.dimens.small1, Alignment.CenterVertically),
            ) {
              // Pal's time, location
              AlertTimeAndLocation(alert, alertId)

              // Pal's name
              Text(
                  text = alert.name,
                  textAlign = TextAlign.Left,
                  style = MaterialTheme.typography.labelLarge,
                  softWrap = true,
                  modifier =
                      Modifier.fillMaxWidth()
                          .wrapContentHeight()
                          .testTag(PalsAlertItem.PAL_NAME + alertId),
              )

              // Pal's message
              if (isClicked) {
                Text(
                    text = alert.message,
                    textAlign = TextAlign.Left,
                    style = MaterialTheme.typography.labelMedium,
                    softWrap = true,
                    modifier =
                        Modifier.fillMaxWidth()
                            .wrapContentHeight()
                            .testTag(PalsAlertItem.PAL_MESSAGE + alertId))
              }
            }
            AlertProductAndUrgency(alertId)
          }

      if (isClicked && alert.status == Status.CREATED) {
        HorizontalDivider(
            modifier =
                Modifier.fillMaxWidth()
                    .wrapContentHeight()
                    .testTag(PalsAlertItem.PAL_DIVIDER + alertId),
            thickness = MaterialTheme.dimens.borderLine,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
        )
        AlertAcceptButtons(context, alertId)
      }
    }
  }
}

/** Composable function that displays the profile picture of an alert. */
@Composable
private fun AlertProfilePicture(alertId: String) {
  Icon(
      imageVector = Icons.Outlined.AccountCircle,
      contentDescription = "Profile picture",
      modifier =
          Modifier.size(MaterialTheme.dimens.iconSize)
              .wrapContentSize()
              .testTag(AlertListsScreen.ALERT_PROFILE_PICTURE + alertId),
  )
}

/**
 * Composable function that displays the time and location of an alert.
 *
 * @param alert The alert to be displayed.
 */
@Composable
private fun AlertTimeAndLocation(alert: Alert, alertId: String) {
  val formatter = DateTimeFormatter.ofPattern("HH:mm")
  val formattedTime = LocalDateTime.parse(alert.createdAt).format(formatter)
  Text(
      text = "${formattedTime}, ${alert.location}",
      textAlign = TextAlign.Left,
      style = MaterialTheme.typography.labelMedium,
      fontWeight = FontWeight.SemiBold,
      softWrap = true,
      modifier =
          Modifier.fillMaxWidth()
              .wrapContentHeight()
              .testTag(AlertListsScreen.ALERT_TIME_AND_LOCATION + alertId),
  )
}

/** Composable function that displays the product type and urgency of an alert. */
@Composable
private fun AlertProductAndUrgency(alertId: String) {
  Row(
      modifier =
          Modifier.wrapContentSize().testTag(AlertListsScreen.ALERT_PRODUCT_AND_URGENCY + alertId),
      horizontalArrangement =
          Arrangement.spacedBy(MaterialTheme.dimens.small1, Alignment.CenterHorizontally),
      verticalAlignment = Alignment.CenterVertically,
  ) {
    // Product type
    Icon(
        imageVector = Icons.Outlined.Call,
        contentDescription = "Menstrual Product Type",
        modifier =
            Modifier.size(MaterialTheme.dimens.iconSize)
                .testTag(AlertListsScreen.ALERT_PRODUCT_TYPE + alertId),
    )
    // Urgency
    Icon(
        imageVector = Icons.Outlined.Warning,
        contentDescription = "Urgency of the Alert",
        modifier =
            Modifier.size(MaterialTheme.dimens.iconSize)
                .testTag(AlertListsScreen.ALERT_URGENCY + alertId),
    )
  }
}

/**
 * Composable function that displays the accept and decline buttons for a pal's alert.
 *
 * @param context The context to display the toast messages.
 */
@Composable
private fun AlertAcceptButtons(context: Context, alertId: String) {
  Row(
      modifier = Modifier.wrapContentSize().testTag(PalsAlertItem.PAL_BUTTONS + alertId),
      horizontalArrangement =
          Arrangement.spacedBy(MaterialTheme.dimens.small2, Alignment.CenterHorizontally),
      verticalAlignment = Alignment.CenterVertically,
  ) {
    // Accept alert button
    Button(
        onClick = { Toast.makeText(context, TO_IMPLEMENT_ACCEPT_ALERT, Toast.LENGTH_SHORT).show() },
        enabled = true,
        modifier = Modifier.wrapContentSize().testTag(PalsAlertItem.PAL_ACCEPT_BUTTON + alertId),
    ) {
      Row(
          modifier = Modifier.wrapContentSize(),
          horizontalArrangement =
              Arrangement.spacedBy(MaterialTheme.dimens.small1, Alignment.CenterHorizontally),
          verticalAlignment = Alignment.CenterVertically,
      ) {
        Icon(
            imageVector = Icons.Outlined.Check,
            contentDescription = "Accept Alert",
            modifier = Modifier.size(MaterialTheme.dimens.iconSizeSmall),
        )
        Text(
            text = PAL_ALERT_ACCEPT_TEXT,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.wrapContentSize(),
        )
      }
    }

    // Decline alert button
    Button(
        onClick = {
          Toast.makeText(context, TO_IMPLEMENT_DECLINE_ALERT, Toast.LENGTH_SHORT).show()
        },
        enabled = true,
        border =
            BorderStroke(
                width = MaterialTheme.dimens.borderLine,
                color = MaterialTheme.colorScheme.onSecondaryContainer),
        modifier = Modifier.wrapContentSize().testTag(PalsAlertItem.PAL_DECLINE_BUTTON + alertId),
    ) {
      Row(
          modifier = Modifier.wrapContentSize(),
          horizontalArrangement =
              Arrangement.spacedBy(MaterialTheme.dimens.small1, Alignment.CenterHorizontally),
          verticalAlignment = Alignment.CenterVertically,
      ) {
        Icon(
            imageVector = Icons.Outlined.Close,
            contentDescription = "Decline Alert",
            modifier = Modifier.size(MaterialTheme.dimens.iconSizeSmall),
        )
        Text(
            text = PAL_ALERT_DECLINE_TEXT,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.wrapContentSize(),
        )
      }
    }
  }
}

/**
 * Composable function that displays a dialog indicating that there are no alerts.
 *
 * @param text The text to be displayed in the dialog.
 */
@Composable
private fun NoAlertDialog(text: String) {
  Card(
      modifier =
          Modifier.wrapContentSize()
              .clip(RoundedCornerShape(size = MaterialTheme.dimens.small2))
              .testTag(AlertListsScreen.NO_ALERTS_CARD),
      elevation = CardDefaults.cardElevation(MaterialTheme.dimens.small1),
  ) {
    Column(
        modifier = Modifier.wrapContentSize().padding(MaterialTheme.dimens.small3),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement =
            Arrangement.spacedBy(MaterialTheme.dimens.small2, Alignment.CenterVertically),
    ) {
      Icon(
          imageVector = Icons.Outlined.SentimentVerySatisfied,
          contentDescription = "No Alert Emoji",
          modifier =
              Modifier.size(MaterialTheme.dimens.iconSize).testTag(AlertListsScreen.NO_ALERTS_ICON),
      )
      Text(
          text = text,
          modifier = Modifier.wrapContentSize().testTag(AlertListsScreen.NO_ALERTS_TEXT),
      )
    }
  }
}
