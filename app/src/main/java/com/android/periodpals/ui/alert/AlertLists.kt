package com.android.periodpals.ui.alert

import android.content.Context
import android.os.Handler
import android.os.Looper
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
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.SentimentVerySatisfied
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
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
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.android.periodpals.ChannelActivity
import com.android.periodpals.R
import com.android.periodpals.model.alert.Alert
import com.android.periodpals.model.alert.AlertViewModel
import com.android.periodpals.model.alert.Product
import com.android.periodpals.model.alert.Status
import com.android.periodpals.model.alert.Urgency
import com.android.periodpals.model.alert.productToPeriodPalsIcon
import com.android.periodpals.model.alert.stringToProduct
import com.android.periodpals.model.alert.stringToUrgency
import com.android.periodpals.model.alert.urgencyToPeriodPalsIcon
import com.android.periodpals.model.authentication.AuthenticationViewModel
import com.android.periodpals.model.chat.ChatViewModel
import com.android.periodpals.model.location.Location
import com.android.periodpals.model.location.LocationViewModel
import com.android.periodpals.model.user.UserViewModel
import com.android.periodpals.resources.C.Tag.AlertListsScreen
import com.android.periodpals.resources.C.Tag.AlertListsScreen.MyAlertItem
import com.android.periodpals.resources.C.Tag.AlertListsScreen.PalsAlertItem
import com.android.periodpals.resources.ComponentColor.getFilledPrimaryButtonColors
import com.android.periodpals.resources.ComponentColor.getPrimaryCardColors
import com.android.periodpals.resources.ComponentColor.getTertiaryCardColors
import com.android.periodpals.services.GPSServiceImpl
import com.android.periodpals.ui.components.FilterDialog
import com.android.periodpals.ui.components.FilterFab
import com.android.periodpals.ui.navigation.BottomNavigationMenu
import com.android.periodpals.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import com.android.periodpals.ui.navigation.TopAppBar
import com.android.periodpals.ui.theme.dimens
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private val SELECTED_TAB_DEFAULT = AlertListsTab.MY_ALERTS

private val INPUT_DATE_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME
private val OUTPUT_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm")

private const val TAG = "AlertListsScreen"

private const val DEFAULT_RADIUS = 100.0

/** Enum class representing the tabs in the AlertLists screen. */
private enum class AlertListsTab {
  MY_ALERTS,
  PALS_ALERTS,
}

/**
 * Composable function that displays the AlertLists screen. It includes a top app bar, tab row for
 * switching between "My Alerts" and "Pals Alerts" tabs, and a bottom navigation menu.
 *
 * @param alertViewModel The view model for managing alert data.
 * @param authenticationViewModel The view model for managing authentication data.
 * @param navigationActions The navigation actions for handling navigation events.
 * @param gpsService The GPS service that provides the device's geographical coordinates.
 * @param navigationActions The navigation actions for handling navigation events.
 */
@Composable
fun AlertListsScreen(
    alertViewModel: AlertViewModel,
    authenticationViewModel: AuthenticationViewModel,
    userViewModel: UserViewModel,
    locationViewModel: LocationViewModel,
    gpsService: GPSServiceImpl,
    chatViewModel: ChatViewModel,
    navigationActions: NavigationActions,
) {
  var selectedTab by remember { mutableStateOf(SELECTED_TAB_DEFAULT) }
  val context = LocalContext.current
  var showFilterDialog by remember { mutableStateOf(false) }
  var isFilterApplied by remember { mutableStateOf(false) }
  var selectedLocation by remember { mutableStateOf<Location?>(null) }
  var radiusInMeters by remember { mutableDoubleStateOf(100.0) }
  var productFilter by remember { mutableStateOf<Product?>(Product.NO_PREFERENCE) }
  var urgencyFilter by remember { mutableStateOf<Urgency?>(null) }

  authenticationViewModel.loadAuthenticationUserData(
      onFailure = {
        Handler(Looper.getMainLooper()).post { // used to show the Toast in the main thread
          Toast.makeText(context, "Error loading your data! Try again later.", Toast.LENGTH_SHORT)
              .show()
        }
        Log.d(TAG, "Authentication data is null")
      },
  )

  val uid by remember { mutableStateOf(authenticationViewModel.authUserData.value!!.uid) }
  alertViewModel.setUserID(uid)
  alertViewModel.fetchAlerts(
      onSuccess = {
        alertViewModel.alerts.value
        alertViewModel.removeFilters()
      },
      onFailure = { e -> Log.d(TAG, "Error fetching alerts: $e") })

  val myAlertsList = alertViewModel.myAlerts.value
  var palsAlertsList by remember { mutableStateOf(alertViewModel.palAlerts) }

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag(AlertListsScreen.SCREEN),
      topBar = {
        Column(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
          TopAppBar(
              title = context.getString(R.string.alert_lists_screen_title),
              chatButton = true,
              onChatButtonClick = { navigationActions.navigateTo(Screen.CHAT) })
          TabRow(
              modifier =
                  Modifier.fillMaxWidth().wrapContentHeight().testTag(AlertListsScreen.TAB_ROW),
              selectedTabIndex = selectedTab.ordinal,
              containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
              contentColor = MaterialTheme.colorScheme.onSurface,
          ) {
            Tab(
                modifier = Modifier.wrapContentSize().testTag(AlertListsScreen.MY_ALERTS_TAB),
                text = {
                  Text(
                      modifier = Modifier.wrapContentSize(),
                      text = context.getString(R.string.alert_lists_tab_my_alerts_title),
                      color = MaterialTheme.colorScheme.onSurface,
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
                      text = context.getString(R.string.alert_lists_tab_pals_alerts_title),
                      color = MaterialTheme.colorScheme.onSurface,
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
      floatingActionButton = {
        if (selectedTab == AlertListsTab.PALS_ALERTS) {
          FilterFab(isFilterApplied) { showFilterDialog = !showFilterDialog }
        }
      },
      containerColor = MaterialTheme.colorScheme.surface,
      contentColor = MaterialTheme.colorScheme.onSurface,
  ) { paddingValues ->
    if (showFilterDialog) {
      FilterDialog(
          context = context,
          currentRadius = radiusInMeters,
          location = selectedLocation,
          product = productToPeriodPalsIcon(productFilter!!).textId,
          urgency =
              if (urgencyFilter == null) context.getString(R.string.alert_lists_filter_default)
              else urgencyToPeriodPalsIcon(urgencyFilter!!).textId,
          onDismiss = { showFilterDialog = false },
          onLocationSelected = { selectedLocation = it },
          onSave = { radius, product, urgency ->
            radiusInMeters = radius
            productFilter = stringToProduct(product)
            urgencyFilter = stringToUrgency(urgency)
            isFilterApplied = true
            if (selectedLocation != null) {
              alertViewModel.fetchAlertsWithinRadius(
                  selectedLocation!!,
                  radiusInMeters,
                  onSuccess = {
                    palsAlertsList = alertViewModel.palAlerts
                    Log.d(TAG, "Alerts within radius: $palsAlertsList")
                  },
                  onFailure = { e -> Log.d(TAG, "Error fetching alerts within radius: $e") })
            }

            // if a product filter was selected, show only alerts with said product marked as needed
            // (or alerts with no product preference)
            // if an urgency filter was selected, show only alerts with said urgency
            alertViewModel.setFilter {
              (productFilter == Product.NO_PREFERENCE ||
                  (it.product == (productFilter) || it.product == Product.NO_PREFERENCE)) &&
                  (urgencyFilter == null || it.urgency == urgencyFilter)
            }
          },
          onReset = {
            radiusInMeters = DEFAULT_RADIUS
            selectedLocation = null
            isFilterApplied = false
            alertViewModel.removeFilters()
            productFilter = Product.NO_PREFERENCE
            urgencyFilter = null
          },
          locationViewModel = locationViewModel,
          gpsService = gpsService)
    }
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
              item { NoAlertDialog(context.getString(R.string.alert_lists_no_my_alerts_dialog)) }
            } else {
              items(myAlertsList) { alert ->
                MyAlertItem(alert, alertViewModel, navigationActions, context)
              }
            }
        AlertListsTab.PALS_ALERTS ->
            if (palsAlertsList.value.isEmpty()) {
              item { NoAlertDialog(context.getString(R.string.alert_lists_no_pals_alerts_dialog)) }
            } else {
              items(palsAlertsList.value) { alert ->
                PalsAlertItem(alert, chatViewModel, authenticationViewModel, userViewModel)
              }
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
 * @param alertViewModel The view model for managing alert data.
 * @param navigationActions The navigation actions for handling navigation events.
 * @param context The context of the current activity.
 */
@Composable
private fun MyAlertItem(
    alert: Alert,
    alertViewModel: AlertViewModel,
    navigationActions: NavigationActions,
    context: Context,
) {
  val idTestTag = alert.id
  Card(
      modifier =
          Modifier.fillMaxWidth().wrapContentHeight().testTag(MyAlertItem.MY_ALERT + idTestTag),
      shape = RoundedCornerShape(size = MaterialTheme.dimens.cardRoundedSize),
      colors = getPrimaryCardColors(),
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
        AlertProductAndUrgency(alert, idTestTag)
      }

      // Edit alert button
      Button(
          onClick = {
            alertViewModel.selectAlert(alert)
            navigationActions.navigateTo(Screen.EDIT_ALERT)
          },
          modifier = Modifier.wrapContentSize().testTag(MyAlertItem.MY_EDIT_BUTTON + idTestTag),
          colors = getFilledPrimaryButtonColors(),
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
              text = context.getString(R.string.alert_lists_my_alert_edit_text),
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
fun PalsAlertItem(
    alert: Alert,
    chatViewModel: ChatViewModel,
    authenticationViewModel: AuthenticationViewModel,
    userViewModel: UserViewModel
) {
  val idTestTag = alert.id
  var isClicked by remember { mutableStateOf(false) }
  Card(
      modifier =
          Modifier.fillMaxWidth().wrapContentHeight().testTag(PalsAlertItem.PAL_ALERT + idTestTag),
      onClick = { isClicked = !isClicked },
      shape = RoundedCornerShape(size = MaterialTheme.dimens.cardRoundedSize),
      colors = getPrimaryCardColors(),
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
            AlertProductAndUrgency(alert, idTestTag)
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
        AlertAcceptButtons(idTestTag, alert, chatViewModel, authenticationViewModel, userViewModel)
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
 * Formats the alert creation time to a readable string.
 *
 * @param createdAt The creation time of the alert in ISO_OFFSET_DATE_TIME format.
 * @return A formatted time string or "Invalid Time" if the input is invalid.
 */
private fun formatAlertTime(createdAt: String?): String {
  return try {
    val dateTime = OffsetDateTime.parse(createdAt, INPUT_DATE_FORMATTER)
    dateTime.format(OUTPUT_TIME_FORMATTER)
  } catch (e: DateTimeParseException) {
    throw DateTimeParseException("Invalid or null input for alert creation time", createdAt, 0)
  }
}

/**
 * Composable function that displays the time and location of an alert.
 *
 * @param alert The alert to be displayed.
 * @param idTestTag The id of the alert used to create unique test tags for each alert card.
 */
@Composable
private fun AlertTimeAndLocation(alert: Alert, idTestTag: String) {
  val formattedTime = formatAlertTime(alert.createdAt)
  Text(
      modifier =
          Modifier.fillMaxWidth()
              .wrapContentHeight()
              .testTag(AlertListsScreen.ALERT_TIME_AND_LOCATION + idTestTag),
      text = "${formattedTime}, ${Location.fromString(alert.location).name}",
      fontWeight = FontWeight.SemiBold,
      textAlign = TextAlign.Left,
      style = MaterialTheme.typography.labelMedium,
  )
}

/**
 * Composable function that displays the product type and urgency of an alert.
 *
 * @param alert The alert to be displayed.
 * @param idTestTag The id of the alert used to create unique test tags for each alert card.
 */
@Composable
private fun AlertProductAndUrgency(alert: Alert, idTestTag: String) {
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
        painter = painterResource(productToPeriodPalsIcon(alert.product).icon),
        contentDescription = "Menstrual Product Type",
        modifier =
            Modifier.size(MaterialTheme.dimens.iconSize)
                .testTag(AlertListsScreen.ALERT_PRODUCT_TYPE + idTestTag),
    )
    // Urgency
    Icon(
        painter = painterResource(urgencyToPeriodPalsIcon(alert.urgency).icon),
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
private fun AlertAcceptButtons(
    idTestTag: String,
    alert: Alert,
    chatViewModel: ChatViewModel,
    authenticationViewModel: AuthenticationViewModel,
    userViewModel: UserViewModel
) {
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
        text = context.getString(R.string.alert_lists_pal_alert_accept_text),
        icon = Icons.Outlined.Check,
        onClick = {
          val authUserData = authenticationViewModel.authUserData.value
          if (authUserData != null) {
            Log.d(TAG, "Accepting alert from ${authUserData.uid}")
            val channelCid =
                chatViewModel.createChannel(
                    myUid = authUserData.uid,
                    palUid = alert.uid,
                    myName = userViewModel.user.value!!.name,
                    palName = alert.name)
            Log.d(TAG, "Channel CID: $channelCid")
            if (channelCid != null) {
              val intent = ChannelActivity.getIntent(context, channelCid)
              context.startActivity(intent)
            }
          } else {
            Toast.makeText(context, "Error: User data is not available", Toast.LENGTH_SHORT).show()
          }
        },
        contentDescription = "Accept Alert",
        buttonColor =
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary,
            ),
        testTag = PalsAlertItem.PAL_ACCEPT_BUTTON + idTestTag,
    )

    // Decline alert button
    AlertActionButton(
        text = context.getString(R.string.alert_lists_pal_alert_decline_text),
        icon = Icons.Outlined.Close,
        onClick = {
          // TODO: Implement decline alert action
          Toast.makeText(context, "To implement decline alert action", Toast.LENGTH_SHORT).show()
        },
        contentDescription = "Decline Alert",
        buttonColor =
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError,
            ),
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
 * @param contentDescription The content description for the icon.
 * @param buttonColor The color scheme for the button.
 * @param testTag The test tag for the button.
 */
@Composable
private fun AlertActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    contentDescription: String,
    buttonColor: ButtonColors,
    testTag: String
) {
  Button(
      onClick = onClick,
      modifier = Modifier.wrapContentSize().testTag(testTag),
      colors = buttonColor,
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
      colors = getTertiaryCardColors(),
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
