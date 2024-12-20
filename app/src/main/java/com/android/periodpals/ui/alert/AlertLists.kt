package com.android.periodpals.ui.alert

import android.content.Context
import android.net.Uri
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
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
import com.android.periodpals.services.NetworkChangeListener
import com.android.periodpals.ui.components.FILTERS_NO_PREFERENCE_TEXT
import com.android.periodpals.ui.components.FilterDialog
import com.android.periodpals.ui.components.FilterFab
import com.android.periodpals.ui.components.formatAlertTime
import com.android.periodpals.ui.components.trimLocationText
import com.android.periodpals.ui.navigation.BottomNavigationMenu
import com.android.periodpals.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import com.android.periodpals.ui.navigation.TopAppBar
import com.android.periodpals.ui.theme.dimens
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

private val SELECTED_TAB_DEFAULT = AlertListsTab.MY_ALERTS

private val DEFAULT_PROFILE_PICTURE =
    Uri.parse("android.resource://com.android.periodpals/${R.drawable.generic_avatar}")

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
 * @param userViewModel The view model for managing user data.
 * @param authenticationViewModel The view model for managing authentication data.
 * @param locationViewModel The view model for managing the location data.
 * @param gpsService The GPS service that provides the device's geographical coordinates.
 * @param navigationActions The navigation actions for handling navigation events.
 */
@Composable
fun AlertListsScreen(
    alertViewModel: AlertViewModel,
    userViewModel: UserViewModel,
    authenticationViewModel: AuthenticationViewModel,
    locationViewModel: LocationViewModel,
    gpsService: GPSServiceImpl,
    chatViewModel: ChatViewModel,
    networkChangeListener: NetworkChangeListener,
    navigationActions: NavigationActions,
) {
  var selectedTab by remember { mutableStateOf(SELECTED_TAB_DEFAULT) }
  val context = LocalContext.current
  var showFilterDialog by remember { mutableStateOf(false) }
  var isFilterApplied by remember { mutableStateOf(false) }
  var selectedLocation by remember { mutableStateOf<Location?>(null) }
  var radiusInMeters by remember { mutableDoubleStateOf(DEFAULT_RADIUS) }
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
  userViewModel.loadUsers(
      onSuccess = { Log.d(TAG, "loadUsers: Success") },
      onFailure = { e: Exception -> Log.e(TAG, "loadUsers: Failure: $e") })

  val uid by remember { mutableStateOf(authenticationViewModel.authUserData.value!!.uid) }
  alertViewModel.setUserID(uid)

  LaunchedEffect(Unit) {
    alertViewModel.fetchAlerts(
        onSuccess = {
          alertViewModel.alerts.value
          alertViewModel.removeFilters()
        },
        onFailure = { e -> Log.d(TAG, "Error fetching alerts: $e") })
  }

  val myAlertsList = alertViewModel.myAlerts.value
  var palsAlertsList by remember { mutableStateOf(alertViewModel.palAlerts) }
  val acceptedAlerts by remember { mutableStateOf(alertViewModel.acceptedAlerts) }

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
            networkChangeListener = networkChangeListener)
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
          product =
              productFilter?.let { productToPeriodPalsIcon(it).textId }
                  ?: FILTERS_NO_PREFERENCE_TEXT,
          urgency =
              urgencyFilter?.let { urgencyToPeriodPalsIcon(it).textId }
                  ?: FILTERS_NO_PREFERENCE_TEXT,
          onDismiss = { showFilterDialog = false },
          onLocationSelected = { selectedLocation = it },
          onSave = { radius, product, urgency ->
            radiusInMeters = radius
            productFilter = stringToProduct(product)
            urgencyFilter = stringToUrgency(urgency)
            isFilterApplied =
                (radius != 100.0) ||
                    (productFilter != Product.NO_PREFERENCE) ||
                    (urgencyFilter != null)

            selectedLocation?.let {
              alertViewModel.fetchAlertsWithinRadius(
                  location = it,
                  radius = radiusInMeters,
                  onSuccess = {
                    Log.d(TAG, "Successfully fetched alerts within radius: $radiusInMeters")
                  },
                  onFailure = { e -> Log.e(TAG, "Error fetching alerts within radius", e) })
            } ?: Log.d(TAG, "Selected location is null")

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
              itemsIndexed(myAlertsList) { index, alert ->
                MyAlertItem(
                    alert = alert,
                    indexTestTag = index,
                    alertViewModel = alertViewModel,
                    userViewModel = userViewModel,
                    navigationActions = navigationActions,
                    context)
              }
            }
        AlertListsTab.PALS_ALERTS -> {
          if (acceptedAlerts.value.isNotEmpty()) {
            item {
              Text(
                  text = "Accepted Alerts",
                  style = MaterialTheme.typography.headlineSmall,
                  modifier =
                      Modifier.padding(bottom = MaterialTheme.dimens.small2)
                          .testTag(AlertListsScreen.ACCEPTED_ALERTS_TEXT))
            }
            itemsIndexed(acceptedAlerts.value) { index, alert ->
              PalsAlertItem(
                  alert = alert,
                  indexTestTag = index,
                  alertViewModel = alertViewModel,
                  userViewModel = userViewModel,
                  chatViewModel = chatViewModel,
                  authenticationViewModel = authenticationViewModel,
                  isAccepted = true)
            }
            item {
              HorizontalDivider(
                  thickness = MaterialTheme.dimens.borderLine,
                  color = MaterialTheme.colorScheme.onSecondaryContainer,
                  modifier =
                      Modifier.padding(vertical = MaterialTheme.dimens.small2)
                          .testTag(AlertListsScreen.ACCEPTED_ALERTS_DIVIDER))
            }
          }
          if (palsAlertsList.value.isEmpty()) {
            item { NoAlertDialog(context.getString(R.string.alert_lists_no_pals_alerts_dialog)) }
          } else {
            itemsIndexed(palsAlertsList.value) { index, alert ->
              PalsAlertItem(
                  alert = alert,
                  indexTestTag = index,
                  alertViewModel = alertViewModel,
                  userViewModel = userViewModel,
                  chatViewModel = chatViewModel,
                  authenticationViewModel = authenticationViewModel)
            }
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
 * @param indexTestTag The index of the alert in the list.
 * @param alertViewModel The view model for managing alert data.
 * @param userViewModel The view model fro managing user data
 * @param navigationActions The navigation actions for handling navigation events.
 * @param context The context of the current activity.
 */
@Composable
private fun MyAlertItem(
    alert: Alert,
    indexTestTag: Int,
    alertViewModel: AlertViewModel,
    userViewModel: UserViewModel,
    navigationActions: NavigationActions,
    context: Context,
) {
  Card(
      modifier =
          Modifier.fillMaxWidth().wrapContentHeight().testTag(MyAlertItem.MY_ALERT + indexTestTag),
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
      AlertProfilePicture(alert, indexTestTag, userViewModel)

      Column(
          modifier = Modifier.fillMaxWidth().wrapContentHeight().weight(1f),
          horizontalAlignment = Alignment.Start,
          verticalArrangement =
              Arrangement.spacedBy(MaterialTheme.dimens.small1, Alignment.CenterVertically),
      ) {
        // Time, location
        AlertTimeAndLocation(alert, indexTestTag)

        // Product type and urgency
        AlertProductAndUrgency(alert, indexTestTag)
      }

      // Edit alert button
      Button(
          onClick = {
            alertViewModel.selectAlert(alert)
            navigationActions.navigateTo(Screen.EDIT_ALERT)
          },
          modifier = Modifier.wrapContentSize().testTag(MyAlertItem.MY_EDIT_BUTTON + indexTestTag),
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
 * profile picture, time, location, name, message, product type, urgency, accept or un-accept.
 * buttons.
 *
 * @param alert The alert to be displayed.
 * @param indexTestTag The id of the alert used to create unique test tags for each alert card.
 * @param alertViewModel The view model for managing alert data.
 * @param userViewModel The view model for managing user data.
 * @param chatViewModel The view model fro managing the chat
 * @param isAccepted A boolean indicating whether the alert has been accepted.
 */
@Composable
fun PalsAlertItem(
    alert: Alert,
    indexTestTag: Int,
    alertViewModel: AlertViewModel,
    userViewModel: UserViewModel,
    chatViewModel: ChatViewModel,
    authenticationViewModel: AuthenticationViewModel,
    isAccepted: Boolean = false
) {
  var isClicked by remember { mutableStateOf(false) }
  val testTag =
      if (!isAccepted) {
        PalsAlertItem.PAL_ALERT + indexTestTag
      } else {
        PalsAlertItem.PAL_ACCEPTED_ALERT + indexTestTag
      }
  Card(
      modifier = Modifier.fillMaxWidth().wrapContentHeight().testTag(testTag),
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
            AlertProfilePicture(alert, indexTestTag, userViewModel)

            Column(
                modifier = Modifier.fillMaxWidth().wrapContentHeight().weight(1f),
                horizontalAlignment = Alignment.Start,
                verticalArrangement =
                    Arrangement.spacedBy(MaterialTheme.dimens.small1, Alignment.CenterVertically),
            ) {
              // Pal's time, location
              AlertTimeAndLocation(alert, indexTestTag)

              // Pal's name
              Text(
                  text = alert.name,
                  textAlign = TextAlign.Left,
                  style = MaterialTheme.typography.labelLarge,
                  modifier =
                      Modifier.fillMaxWidth()
                          .wrapContentHeight()
                          .testTag(PalsAlertItem.PAL_NAME + indexTestTag),
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
                            .testTag(PalsAlertItem.PAL_MESSAGE + indexTestTag))
              }
            }
            AlertProductAndUrgency(alert, indexTestTag)
          }

      if (isClicked && alert.status == Status.CREATED) {
        HorizontalDivider(
            modifier =
                Modifier.fillMaxWidth()
                    .wrapContentHeight()
                    .testTag(PalsAlertItem.PAL_DIVIDER + indexTestTag),
            thickness = MaterialTheme.dimens.borderLine,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
        )
        if (isAccepted) {
          AlertUnAcceptButton(
              alert, indexTestTag, onClick = { alertViewModel.unAcceptAlert(alert) })
        } else {
          AlertAcceptButtons(
              alert,
              indexTestTag,
              chatViewModel,
              authenticationViewModel,
              userViewModel,
              onClick = {
                isClicked = false
                alertViewModel.acceptAlert(alert)
              })
        }
      }
    }
  }
}

/**
 * Composable function that displays the profile picture of an alert.
 *
 * @param alert The alert to be displayed.
 * @param indexTestTag The id of the alert used to create unique test tags for each alert card.
 * @param userViewModel `UserViewModel` used to fetch the profile picture of the user.
 */
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun AlertProfilePicture(alert: Alert, indexTestTag: Int, userViewModel: UserViewModel) {
  val user =
      userViewModel.users.value?.find { it.name == alert.name } // TODO: match by uid not by name
  val imageUrl = user?.imageUrl ?: ""
  var model by remember { mutableStateOf<Any?>(null) }

  LaunchedEffect(imageUrl) {
    userViewModel.downloadFilePublic(
        imageUrl, onSuccess = { model = it }, onFailure = { model = null })
  }

  GlideImage(
      model = model ?: DEFAULT_PROFILE_PICTURE,
      contentDescription = "Profile picture",
      contentScale = ContentScale.Crop,
      modifier =
          Modifier.size(MaterialTheme.dimens.iconButtonSize)
              .clip(shape = CircleShape)
              .wrapContentSize()
              .testTag(AlertListsScreen.ALERT_PROFILE_PICTURE + indexTestTag),
  )
}

/**
 * Composable function that displays the time and location of an alert.
 *
 * @param alert The alert to be displayed.
 * @param indexTestTag The id of the alert used to create unique test tags for each alert card.
 */
@Composable
private fun AlertTimeAndLocation(alert: Alert, indexTestTag: Int) {
  val formattedTime = formatAlertTime(alert.createdAt)
  Text(
      modifier =
          Modifier.fillMaxWidth()
              .wrapContentHeight()
              .testTag(AlertListsScreen.ALERT_TIME_AND_LOCATION + indexTestTag),
      text = "${formattedTime}, ${trimLocationText(Location.fromString(alert.location).name)}",
      fontWeight = FontWeight.SemiBold,
      textAlign = TextAlign.Left,
      style = MaterialTheme.typography.labelMedium,
  )
}

/**
 * Composable function that displays the product type and urgency of an alert.
 *
 * @param alert The alert to be displayed.
 * @param indexTestTag The id of the alert used to create unique test tags for each alert card.
 */
@Composable
private fun AlertProductAndUrgency(alert: Alert, indexTestTag: Int) {
  Row(
      modifier =
          Modifier.wrapContentSize()
              .testTag(AlertListsScreen.ALERT_PRODUCT_AND_URGENCY + indexTestTag),
      horizontalArrangement =
          Arrangement.spacedBy(MaterialTheme.dimens.small1, Alignment.CenterHorizontally),
      verticalAlignment = Alignment.CenterVertically,
  ) {
    // Product type
    Icon(
        painter = painterResource(productToPeriodPalsIcon(alert.product).icon),
        contentDescription = productToPeriodPalsIcon(alert.product).textId,
        modifier =
            Modifier.size(MaterialTheme.dimens.iconSize)
                .testTag(AlertListsScreen.ALERT_PRODUCT_TYPE + indexTestTag),
    )
    // Urgency
    Icon(
        painter = painterResource(urgencyToPeriodPalsIcon(alert.urgency).icon),
        contentDescription = urgencyToPeriodPalsIcon(alert.urgency).textId,
        modifier =
            Modifier.size(MaterialTheme.dimens.iconSize)
                .testTag(AlertListsScreen.ALERT_URGENCY + indexTestTag),
    )
  }
}

/**
 * Composable function that displays the accept buttons for a pal's alert.
 *
 * @param alert The alert to be accepted.
 * @param indexTestTag The id of the alert used to create unique test tags for each alert card
 */
@Composable
private fun AlertAcceptButtons(
    alert: Alert,
    indexTestTag: Int,
    chatViewModel: ChatViewModel,
    authenticationViewModel: AuthenticationViewModel,
    userViewModel: UserViewModel,
    onClick: (Alert) -> Unit
) {
  val context = LocalContext.current
  Row(
      modifier =
          Modifier.fillMaxWidth()
              .wrapContentHeight()
              .testTag(PalsAlertItem.PAL_BUTTONS + indexTestTag),
      horizontalArrangement =
          Arrangement.spacedBy(MaterialTheme.dimens.small2, Alignment.CenterHorizontally),
      verticalAlignment = Alignment.CenterVertically,
  ) {
    // Accept alert button
    AlertActionButton(
        text = context.getString(R.string.alert_lists_pal_alert_accept_text),
        icon = Icons.Outlined.Check,
        onClick = {
          onClick(alert)
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
        testTag = PalsAlertItem.PAL_ACCEPT_BUTTON + indexTestTag,
    )
  }
}

@Composable
private fun AlertUnAcceptButton(alert: Alert, indexTestTag: Int, onClick: (Alert) -> Unit) {
  val context = LocalContext.current
  Row(
      modifier =
          Modifier.fillMaxWidth().wrapContentHeight().testTag(PalsAlertItem.PAL_BUTTONS + alert.id),
      horizontalArrangement =
          Arrangement.spacedBy(MaterialTheme.dimens.small2, Alignment.CenterHorizontally),
      verticalAlignment = Alignment.CenterVertically,
  ) {
    // Accept alert button
    AlertActionButton(
        text = context.getString(R.string.alert_lists_pal_alert_unaccept_text),
        icon = Icons.Outlined.Close,
        onClick = { onClick(alert) },
        contentDescription = "Accept Alert",
        buttonColor =
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError,
            ),
        testTag = PalsAlertItem.PAL_UNACCEPT_BUTTON + indexTestTag,
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
