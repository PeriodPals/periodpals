package com.android.periodpals.ui.alert

import android.util.Log
import android.widget.Toast
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
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.android.periodpals.model.alert.Alert
import com.android.periodpals.model.alert.Product
import com.android.periodpals.model.alert.Status
import com.android.periodpals.model.alert.Urgency
import com.android.periodpals.resources.C.Tag.AlertListsScreen
import com.android.periodpals.resources.C.Tag.AlertListsScreen.MyAlertItem
import com.android.periodpals.resources.C.Tag.AlertListsScreen.PalsAlertItem
import com.android.periodpals.ui.navigation.BottomNavigationMenu
import com.android.periodpals.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.TopAppBar
import com.android.periodpals.ui.theme.ComponentColor.getCardColors
import com.android.periodpals.ui.theme.ComponentColor.getFilledButtonPrimaryColors
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
private val DATE_FORMATTER = DateTimeFormatter.ofPattern("HH:mm")
const val LOG_TAG = "AlertListsScreen"
private val MY_ALERTS_LIST: List<Alert> =
    listOf(
        Alert(
            id = "1",
            uid = "1",
            name = "Hippo Alpha",
            product = Product.TAMPON,
            urgency = Urgency.HIGH,
            createdAt = LocalDateTime.now().toString(),
            location = "Rolex Learning Center",
            message = "I need help!",
            status = Status.CREATED,
        ),
        Alert(
            id = "2",
            uid = "1",
            name = "Hippo Beta",
            product = Product.PAD,
            urgency = Urgency.LOW,
            createdAt = LocalDateTime.now().toString(),
            location = "BC",
            message = "I forgot my pads at home :/",
            status = Status.PENDING,
        ),
    )
private val PALS_ALERTS_LIST: List<Alert> =
    listOf(
        Alert(
            id = "3",
            uid = "2",
            name = "Hippo Gamma",
            product = Product.TAMPON,
            urgency = Urgency.MEDIUM,
            createdAt = LocalDateTime.now().toString(),
            location = "EPFL",
            message = "I need help!",
            status = Status.CREATED,
        ),
        Alert(
            id = "4",
            uid = "3",
            name = "Hippo Delta",
            product = Product.PAD,
            urgency = Urgency.HIGH,
            createdAt = LocalDateTime.now().toString(),
            location = "Rolex Learning Center",
            message = "I forgot my pads at home :/",
            status = Status.PENDING,
        ),
    )

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
 * @param myAlertsList Placeholder value for the list of the current user's alerts, passed as
 *   parameter for testing purposes. TODO: replace by the alertVM when implemented.
 * @param palsAlertsList Placeholder value for the list of other users' alerts, passed as parameter
 *   for testing purposes. TODO: replace by the alertVM when implemented.
 */
@Composable
fun AlertListsScreen(
    navigationActions: NavigationActions,
    myAlertsList: List<Alert> = MY_ALERTS_LIST,
    palsAlertsList: List<Alert> = PALS_ALERTS_LIST,
) {
  var selectedTab by remember { mutableStateOf(SELECTED_TAB_DEFAULT) }

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag(AlertListsScreen.SCREEN),
      topBar = {
        Column(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
          TopAppBar(title = SCREEN_TITLE)
          TabRow(
              modifier =
                  Modifier.fillMaxWidth().wrapContentHeight().testTag(AlertListsScreen.TAB_ROW),
              selectedTabIndex = selectedTab.ordinal,
          ) {
            Tab(
                modifier = Modifier.wrapContentSize().testTag(AlertListsScreen.MY_ALERTS_TAB),
                text = {
                  Text(
                      modifier = Modifier.wrapContentSize(),
                      text = MY_ALERTS_TAB_TITLE,
                      style = MaterialTheme.typography.headlineSmall,
                  )
                },
                selected = selectedTab == AlertListsTab.MY_ALERTS,
                onClick = { selectedTab = AlertListsTab.MY_ALERTS },
            )
            Tab(
                modifier = Modifier.wrapContentSize().testTag(AlertListsScreen.PALS_ALERTS_TAB),
                text = {
                  Text(
                      modifier = Modifier.wrapContentSize(),
                      text = PALS_ALERTS_TAB_TITLE,
                      style = MaterialTheme.typography.headlineSmall,
                  )
                },
                selected = selectedTab == AlertListsTab.PALS_ALERTS,
                onClick = { selectedTab = AlertListsTab.PALS_ALERTS },
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
      containerColor = MaterialTheme.colorScheme.surface,
      contentColor = MaterialTheme.colorScheme.onSurface,
  ) { paddingValues ->
    LazyColumn(
        modifier =
            Modifier.fillMaxSize()
                .padding(paddingValues)
                .padding(
                    horizontal = MaterialTheme.dimens.medium3,
                    vertical = MaterialTheme.dimens.small3,
                ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.small2, Alignment.Top),
    ) {
      when (selectedTab) {
        AlertListsTab.MY_ALERTS ->
            if (myAlertsList.isEmpty()) {
              item { NoAlertDialog(NO_MY_ALERTS_DIALOG) }
            } else {
              items(myAlertsList) { alert -> MyAlertItem(alert) }
            }
        AlertListsTab.PALS_ALERTS ->
            if (palsAlertsList.isEmpty()) {
              item { NoAlertDialog(NO_PAL_ALERTS_DIALOG) }
            } else {
              items(palsAlertsList) { alert -> PalsAlertItem(alert = alert) }
            }
      }
    }
  }
}

/**
 * Composable function that displays an individual user's alert item. It includes details such as
 * profile picture, time, location, product type, urgency, and an edit button.
 *
 * @param alert The alert to be displayed.
 */
@Composable
private fun MyAlertItem(alert: Alert) {
  // TODO: Change the logic about alert.id being null when implementing the AlertViewModel
  if (alert.id == null) {
    Log.d(LOG_TAG, "Alert id is null")
    return
  }
  val idTestTag = alert.id
  val context = LocalContext.current // TODO: Delete when implement edit alert action
  Card(
      modifier =
          Modifier.fillMaxWidth().wrapContentHeight().testTag(MyAlertItem.MY_ALERT + idTestTag),
      shape = RoundedCornerShape(size = MaterialTheme.dimens.cardRoundedSize),
      colors = getCardColors(),
      elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.dimens.cardElevation),
  ) {
    Row(
        modifier =
            Modifier.fillMaxWidth()
                .wrapContentHeight()
                .padding(
                    horizontal = MaterialTheme.dimens.small3,
                    vertical = MaterialTheme.dimens.small1,
                ),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.small3, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically,
    ) {
      // My profile picture
      AlertProfilePicture(idTestTag)

      Column(
          modifier = Modifier.fillMaxWidth().wrapContentHeight().weight(1f),
          horizontalAlignment = Alignment.Start,
          verticalArrangement =
              Arrangement.spacedBy(MaterialTheme.dimens.small1, Alignment.CenterVertically),
      ) {
        // Time, location
        AlertTimeAndLocation(alert, idTestTag)

        // Product type and urgency
        AlertProductAndUrgency(idTestTag)
      }

      // Edit alert button
      Button(
          onClick = {
            // TODO: Implement edit alert action
            Toast.makeText(context, "To implement edit alert screen", Toast.LENGTH_SHORT).show()
          },
          modifier = Modifier.wrapContentSize().testTag(MyAlertItem.MY_EDIT_BUTTON + idTestTag),
          colors = getFilledButtonPrimaryColors(),
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
 * Composable function that displays an individual pal's alert item. It includes details such as
 * profile picture, time, location, name, message, product type, urgency, accept and decline
 * buttons.
 *
 * @param alert The alert to be displayed.
 */
@Composable
fun PalsAlertItem(alert: Alert) {
  // TODO: Change the logic about alert.id being null when implementing the AlertViewModel
  if (alert.id == null) {
    Log.d(LOG_TAG, "Alert id is null")
    return
  }
  val idTestTag = alert.id
  var isClicked by remember { mutableStateOf(false) }
  Card(
      modifier =
          Modifier.fillMaxWidth().wrapContentHeight().testTag(PalsAlertItem.PAL_ALERT + idTestTag),
      onClick = { isClicked = !isClicked },
      shape = RoundedCornerShape(size = MaterialTheme.dimens.cardRoundedSize),
      colors = getCardColors(),
      elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.dimens.cardElevation),
  ) {
    Column(
        modifier =
            Modifier.fillMaxWidth()
                .wrapContentHeight()
                .padding(
                    horizontal = MaterialTheme.dimens.small3,
                    vertical = MaterialTheme.dimens.small1,
                ),
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
            AlertProfilePicture(idTestTag)

            Column(
                modifier = Modifier.fillMaxWidth().wrapContentHeight().weight(1f),
                horizontalAlignment = Alignment.Start,
                verticalArrangement =
                    Arrangement.spacedBy(MaterialTheme.dimens.small1, Alignment.CenterVertically),
            ) {
              // Pal's time, location
              AlertTimeAndLocation(alert, idTestTag)

              // Pal's name
              Text(
                  text = alert.name,
                  textAlign = TextAlign.Left,
                  style = MaterialTheme.typography.labelLarge,
                  modifier =
                      Modifier.fillMaxWidth()
                          .wrapContentHeight()
                          .testTag(PalsAlertItem.PAL_NAME + idTestTag),
              )

              // Pal's message
              if (isClicked) {
                Text(
                    text = alert.message,
                    textAlign = TextAlign.Left,
                    style = MaterialTheme.typography.labelMedium,
                    modifier =
                        Modifier.fillMaxWidth()
                            .wrapContentHeight()
                            .testTag(PalsAlertItem.PAL_MESSAGE + idTestTag))
              }
            }
            AlertProductAndUrgency(idTestTag)
          }

      if (isClicked && alert.status == Status.CREATED) {
        HorizontalDivider(
            modifier =
                Modifier.fillMaxWidth()
                    .wrapContentHeight()
                    .testTag(PalsAlertItem.PAL_DIVIDER + idTestTag),
            thickness = MaterialTheme.dimens.borderLine,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
        )
        AlertAcceptButtons(idTestTag)
      }
    }
  }
}

/**
 * Composable function that displays the profile picture of an alert.
 *
 * @param idTestTag The id of the alert used to create unique test tags for each alert card.
 */
@Composable
private fun AlertProfilePicture(idTestTag: String) {
  // TODO: Implement profile picture with VM fetch
  Icon(
      imageVector = Icons.Outlined.AccountCircle,
      contentDescription = "Profile picture",
      modifier =
          Modifier.size(MaterialTheme.dimens.iconSize)
              .wrapContentSize()
              .testTag(AlertListsScreen.ALERT_PROFILE_PICTURE + idTestTag),
  )
}

/**
 * Composable function that displays the time and location of an alert.
 *
 * @param alert The alert to be displayed.
 * @param idTestTag The id of the alert used to create unique test tags for each alert card.
 */
@Composable
private fun AlertTimeAndLocation(alert: Alert, idTestTag: String) {
  val formattedTime = LocalDateTime.parse(alert.createdAt).format(DATE_FORMATTER)
  Text(
      modifier =
          Modifier.fillMaxWidth()
              .wrapContentHeight()
              .testTag(AlertListsScreen.ALERT_TIME_AND_LOCATION + idTestTag),
      text = "${formattedTime}, ${alert.location}",
      fontWeight = FontWeight.SemiBold,
      textAlign = TextAlign.Left,
      style = MaterialTheme.typography.labelMedium,
  )
}

/**
 * Composable function that displays the product type and urgency of an alert.
 *
 * @param idTestTag The id of the alert used to create unique test tags for each alert card.
 */
@Composable
private fun AlertProductAndUrgency(idTestTag: String) {
  Row(
      modifier =
          Modifier.wrapContentSize()
              .testTag(AlertListsScreen.ALERT_PRODUCT_AND_URGENCY + idTestTag),
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
                .testTag(AlertListsScreen.ALERT_PRODUCT_TYPE + idTestTag),
    )
    // Urgency
    Icon(
        imageVector = Icons.Outlined.Warning,
        contentDescription = "Urgency of the Alert",
        modifier =
            Modifier.size(MaterialTheme.dimens.iconSize)
                .testTag(AlertListsScreen.ALERT_URGENCY + idTestTag),
    )
  }
}

/**
 * Composable function that displays the accept and decline buttons for a pal's alert.
 *
 * @param idTestTag The id of the alert used to create unique test tags for each alert card.
 */
@Composable
private fun AlertAcceptButtons(idTestTag: String) {
  val context = LocalContext.current // TODO: Delete when implement accept / reject alert action
  Row(
      modifier =
          Modifier.fillMaxWidth()
              .wrapContentHeight()
              .testTag(PalsAlertItem.PAL_BUTTONS + idTestTag),
      horizontalArrangement =
          Arrangement.spacedBy(MaterialTheme.dimens.small2, Alignment.CenterHorizontally),
      verticalAlignment = Alignment.CenterVertically,
  ) {
    // Accept alert button
    AlertActionButton(
        text = PAL_ALERT_ACCEPT_TEXT,
        icon = Icons.Outlined.Check,
        onClick = {
          // TODO: Implement accept alert action
          Toast.makeText(context, "To implement accept alert action", Toast.LENGTH_SHORT).show()
        },
        contentDescription = "Accept Alert",
        color = Color(0xFF6FCF97),
        testTag = PalsAlertItem.PAL_ACCEPT_BUTTON + idTestTag,
    )

    // Decline alert button
    AlertActionButton(
        text = PAL_ALERT_DECLINE_TEXT,
        icon = Icons.Outlined.Close,
        onClick = {
          // TODO: Implement decline alert action
          Toast.makeText(context, "To implement decline alert action", Toast.LENGTH_SHORT).show()
        },
        contentDescription = "Decline Alert",
        color = Color(0xFFF37065),
        testTag = PalsAlertItem.PAL_DECLINE_BUTTON + idTestTag,
    )
  }
}

/**
 * Composable function that displays an alert action button with an icon and text.
 *
 * @param text The text to be displayed on the button.
 * @param icon The icon to be displayed on the button.
 * @param onClick The action to be executed when the button is clicked.
 * @param testTag The test tag for the button.
 */
@Composable
private fun AlertActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    contentDescription: String,
    color: Color,
    testTag: String
) {
  Button(
      onClick = onClick,
      modifier = Modifier.wrapContentSize().testTag(testTag),
      colors =
          ButtonDefaults.buttonColors(
              containerColor = color, contentColor = MaterialTheme.colorScheme.onSecondary),
  ) {
    Row(
        modifier = Modifier.wrapContentSize(),
        horizontalArrangement =
            Arrangement.spacedBy(MaterialTheme.dimens.small1, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
      Icon(
          imageVector = icon,
          contentDescription = contentDescription,
          modifier = Modifier.size(MaterialTheme.dimens.iconSizeSmall),
      )
      Text(
          text = text,
          style = MaterialTheme.typography.labelMedium,
          modifier = Modifier.wrapContentSize(),
      )
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
      modifier = Modifier.wrapContentSize().testTag(AlertListsScreen.NO_ALERTS_CARD),
      shape = RoundedCornerShape(size = MaterialTheme.dimens.cardRoundedSize),
      colors = getCardColors(),
      elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.dimens.cardElevation),
  ) {
    Column(
        modifier = Modifier.wrapContentSize().padding(MaterialTheme.dimens.small2),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement =
            Arrangement.spacedBy(MaterialTheme.dimens.small2, Alignment.CenterVertically),
    ) {
      Icon(
          modifier =
              Modifier.size(MaterialTheme.dimens.iconSize).testTag(AlertListsScreen.NO_ALERTS_ICON),
          imageVector = Icons.Outlined.SentimentVerySatisfied,
          contentDescription = "No Alert Emoji",
      )
      Text(
          modifier = Modifier.wrapContentSize().testTag(AlertListsScreen.NO_ALERTS_TEXT),
          text = text,
          style = MaterialTheme.typography.bodyMedium,
      )
    }
  }
}
