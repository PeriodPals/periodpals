package com.android.periodpals.ui.alert

import android.util.Log
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.periodpals.R
import com.android.periodpals.model.alert.Alert
import com.android.periodpals.model.alert.AlertViewModel
import com.android.periodpals.model.alert.AlertViewModel.Companion.LOCATION_STATE_NAME
import com.android.periodpals.model.alert.AlertViewModel.Companion.MESSAGE_STATE_NAME
import com.android.periodpals.model.alert.AlertViewModel.Companion.PRODUCT_STATE_NAME
import com.android.periodpals.model.alert.AlertViewModel.Companion.URGENCY_STATE_NAME
import com.android.periodpals.model.alert.Status
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
import com.android.periodpals.services.NetworkChangeListener
import com.android.periodpals.ui.components.ActionButton
import com.android.periodpals.ui.components.LocationField
import com.android.periodpals.ui.components.MessageField
import com.android.periodpals.ui.components.ProductField
import com.android.periodpals.ui.components.UrgencyField
import com.android.periodpals.ui.navigation.BottomNavigationMenu
import com.android.periodpals.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import com.android.periodpals.ui.navigation.TopAppBar
import com.android.periodpals.ui.theme.dimens
import com.dsc.form_builder.TextFieldState

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
    networkChangeListener: NetworkChangeListener,
    navigationActions: NavigationActions,
) {
  val context = LocalContext.current
  val formState = remember { alertViewModel.formState }
  formState.reset()

  val productState = formState.getState<TextFieldState>(PRODUCT_STATE_NAME)
  productState.change(context.getString(R.string.create_alert_product_dropdown_default_value))
  val urgencyState = formState.getState<TextFieldState>(URGENCY_STATE_NAME)
  urgencyState.change(context.getString(R.string.create_alert_urgency_dropdown_default_value))
  val locationState = formState.getState<TextFieldState>(LOCATION_STATE_NAME)
  val messageState = formState.getState<TextFieldState>(MESSAGE_STATE_NAME)

  LaunchedEffect(Unit) {
    gpsService.askPermissionAndStartUpdates() // Permission to access location
  }
  authenticationViewModel.loadAuthenticationUserData(
      onFailure = { Log.d(TAG, "Authentication data is null") })
  userViewModel.loadUser(
      authenticationViewModel.authUserData.value!!.uid,
      onFailure = { Log.d(TAG, "User data is null") })

  val name by remember { mutableStateOf(userViewModel.user.value?.name ?: "") }
  val uid by remember { mutableStateOf(authenticationViewModel.authUserData.value!!.uid) }

  // Screen
  Scaffold(
      modifier = Modifier.fillMaxSize().testTag(C.Tag.CreateAlertScreen.SCREEN),
      topBar = { TopAppBar(title = context.getString(R.string.create_alert_screen_title)) },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationActions.currentRoute(),
            networkChangeListener = networkChangeListener)
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
          text = context.getString(R.string.create_alert_instruction_text),
          modifier = Modifier.testTag(AlertInputs.INSTRUCTION_TEXT),
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.bodyMedium,
      )

      // Product dropdown menu
      ProductField(product = productState.value, onValueChange = { productState.change(it) })

      // Urgency dropdown menu
      UrgencyField(urgency = productState.value, onValueChange = { urgencyState.change(it) })

      // Location field
      LocationField(
          location =
              if (locationState.value.isEmpty()) null else Location.fromString(locationState.value),
          locationViewModel = locationViewModel,
          onLocationSelected = { locationState.change(it.toString()) },
          gpsService,
      )

      // Message field
      MessageField(text = messageState.value, onValueChange = { messageState.change(it) })

      // "Ask for Help" button
      ActionButton(
          buttonText = context.getString(R.string.create_alert_submission_button_text),
          onClick = {
            val errorMessage =
                when {
                  !productState.validate() -> productState.errorMessage
                  !urgencyState.validate() -> urgencyState.errorMessage
                  !locationState.validate() -> locationState.errorMessage
                  !messageState.validate() -> messageState.errorMessage
                  else -> null
                }
            if (errorMessage != null) {
              return@ActionButton
            }

            val alert =
                Alert(
                    uid = uid,
                    name = name,
                    product = stringToProduct(productState.value)!!,
                    urgency = stringToUrgency(urgencyState.value)!!,
                    location = locationState.value,
                    message = messageState.value,
                    status = Status.CREATED,
                )
            alertViewModel.createAlert(
                alert,
                onSuccess = { Log.d(TAG, "Alert created") },
                onFailure = { e -> Log.e(TAG, "createAlert: fail to create alert: ${e.message}") },
            )
            navigationActions.navigateTo(Screen.ALERT_LIST)
          },
          colors = getFilledPrimaryContainerButtonColors(),
          testTag = C.Tag.CreateAlertScreen.SUBMIT_BUTTON,
      )
    }
  }
}
