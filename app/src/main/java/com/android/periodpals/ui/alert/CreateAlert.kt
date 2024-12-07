package com.android.periodpals.ui.alert

import android.os.Handler
import android.os.Looper
import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
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
import com.android.periodpals.model.alert.Alert
import com.android.periodpals.model.alert.AlertViewModel
import com.android.periodpals.model.alert.Product
import com.android.periodpals.model.alert.Status
import com.android.periodpals.model.alert.Urgency
import com.android.periodpals.model.alert.stringToProduct
import com.android.periodpals.model.alert.stringToUrgency
import com.android.periodpals.model.authentication.AuthenticationViewModel
import com.android.periodpals.model.location.Location
import com.android.periodpals.model.location.LocationViewModel
import com.android.periodpals.model.user.UserViewModel
import com.android.periodpals.resources.C
import com.android.periodpals.resources.C.Tag.AlertInputs
import com.android.periodpals.resources.ComponentColor.getFilledPrimaryContainerButtonColors
import com.android.periodpals.services.GPSServiceImpl
import com.android.periodpals.ui.components.ActionButton
import com.android.periodpals.ui.components.LocationField
import com.android.periodpals.ui.components.MessageField
import com.android.periodpals.ui.components.ProductField
import com.android.periodpals.ui.components.UrgencyField
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

private const val TAG = "CreateAlertScreen"

/**
 * Composable function for the CreateAlert screen.
 *
 * @param locationViewModel The location view model that provides location-related data and
 *   functionality.
 * @param gpsService The GPS service that provides the device's geographical coordinates.
 * @param navigationActions The navigation actions to handle navigation events.
 * @param alertViewModel The alert view model that provides alert-related functions. Used to create
 *   teh alert in the repository.
 * @param authenticationViewModel The authentication view model that provides authentication-related
 *   data and functions. Used to extract user's uid
 * @param userViewModel The user view model that provides user-related data and functions. Used to
 *   extract user's name
 */
@Composable
fun CreateAlertScreen(
    locationViewModel: LocationViewModel = viewModel(factory = LocationViewModel.Factory),
    gpsService: GPSServiceImpl,
    alertViewModel: AlertViewModel,
    authenticationViewModel: AuthenticationViewModel,
    userViewModel: UserViewModel,
    navigationActions: NavigationActions,
) {
  val context = LocalContext.current
  var product by remember { mutableStateOf<Product?>(null) }
  var urgency by remember { mutableStateOf<Urgency?>(null) }
  var selectedLocation by remember { mutableStateOf<Location?>(null) }
  var message by remember { mutableStateOf(DEFAULT_MESSAGE) }

  LaunchedEffect(Unit) {
    gpsService.askPermissionAndStartUpdates() // Permission to access location
  }
  authenticationViewModel.loadAuthenticationUserData(
      onFailure = {
        Handler(Looper.getMainLooper()).post { // used to show the Toast in the main thread
          Toast.makeText(context, "Error loading your data! Try again later.", Toast.LENGTH_SHORT)
              .show()
        }
        Log.d(TAG, "Authentication data is null")
      },
  )
  userViewModel.loadUser(
      onFailure = {
        Handler(Looper.getMainLooper()).post { // used to show the Toast in the main thread
          Toast.makeText(context, "Error loading your data! Try again later.", Toast.LENGTH_SHORT)
              .show()
        }
        Log.d(TAG, "User data is null")
      },
  )

  val name by remember { mutableStateOf(userViewModel.user.value?.name ?: "") }
  val uid by remember { mutableStateOf(authenticationViewModel.authUserData.value!!.uid) }

  // Screen
  Scaffold(
      modifier = Modifier.fillMaxSize().testTag(C.Tag.CreateAlertScreen.SCREEN),
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
          modifier = Modifier.testTag(AlertInputs.INSTRUCTION_TEXT),
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.bodyMedium,
      )

      // Product dropdown menu
      ProductField(
          product = PRODUCT_DROPDOWN_DEFAULT_VALUE,
          onValueChange = { product = stringToProduct(it) })

      // Urgency dropdown menu
      UrgencyField(
          urgency = URGENCY_DROPDOWN_DEFAULT_VALUE,
          onValueChange = { urgency = stringToUrgency(it) })

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
                validateFields(product, urgency, selectedLocation, message)
            if (!isValid) {
              Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            } else {
              val alert =
                  Alert(
                      uid = uid,
                      name = name,
                      product = product!!,
                      urgency = urgency!!,
                      location = selectedLocation!!.toString(),
                      message = message,
                      status = Status.CREATED)
              alertViewModel.createAlert(
                  alert,
                  onSuccess = { Log.d(TAG, "Alert created") },
                  onFailure = { e ->
                    Log.e(TAG, "createAlert: fail to create alert: ${e.message}")
                  })
              Toast.makeText(context, SUCCESSFUL_SUBMISSION_TOAST_MESSAGE, Toast.LENGTH_SHORT)
                  .show()
              navigationActions.navigateTo(Screen.ALERT_LIST)
            }
          },
          colors = getFilledPrimaryContainerButtonColors(),
          testTag = C.Tag.CreateAlertScreen.SUBMIT_BUTTON,
      )
    }
  }
}
