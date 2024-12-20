package com.android.periodpals.ui.profile

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SentimentVeryDissatisfied
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import com.android.periodpals.R
import com.android.periodpals.model.authentication.AuthenticationViewModel
import com.android.periodpals.model.chat.ChatViewModel
import com.android.periodpals.model.user.User
import com.android.periodpals.model.user.UserViewModel
import com.android.periodpals.resources.C.Tag.ProfileScreens.ProfileScreen
import com.android.periodpals.resources.ComponentColor.getTertiaryCardColors
import com.android.periodpals.services.NetworkChangeListener
import com.android.periodpals.services.PushNotificationsService
import com.android.periodpals.ui.components.ProfilePicture
import com.android.periodpals.ui.components.ProfileSection
import com.android.periodpals.ui.navigation.BottomNavigationMenu
import com.android.periodpals.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import com.android.periodpals.ui.navigation.TopAppBar
import com.android.periodpals.ui.theme.dimens

private const val TAG = "ProfileScreen"

private val DEFAULT_PROFILE_PICTURE =
    Uri.parse("android.resource://com.android.periodpals/${R.drawable.generic_avatar}")

/**
 * A composable function that displays the user's profile screen.
 *
 * This screen includes the user's profile picture, name, description, contribution information, and
 * a section for reviews. It also includes a top app bar with an edit button and a bottom navigation
 * menu.
 *
 * @param userViewModel The ViewModel that handles user data.
 * @param authenticationViewModel The ViewModel that handles authentication data.
 * @param notificationService The service that handles push notifications.
 * @param chatViewModel The ViewModel that handles chat data.
 * @param navigationActions The navigation actions to navigate between screens.
 */
@Composable
fun ProfileScreen(
    userViewModel: UserViewModel,
    authenticationViewModel: AuthenticationViewModel,
    notificationService: PushNotificationsService,
    chatViewModel: ChatViewModel,
    networkChangeListener: NetworkChangeListener,
    navigationActions: NavigationActions
) {
  val context = LocalContext.current
  val numberInteractions =
      0 // TODO: placeholder to be replaced when we integrate it to the User data class
  val userState by remember { userViewModel.user }
  val userAvatar by remember { userViewModel.avatar }

  Log.d(TAG, "Loading user data")
  init(
      userViewModel,
      authenticationViewModel,
      chatViewModel,
      userState,
      onSuccess = { Log.d(TAG, "User data loaded successfully") },
      onFailure = { Log.d(TAG, "Error loading user data: $it") })

  // Only executed once
  LaunchedEffect(Unit) { notificationService.askPermission() }

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag(ProfileScreen.SCREEN),
      topBar = {
        TopAppBar(
            title = context.getString(R.string.profile_screen_title),
            settingsButton = true,
            onSettingsButtonClick = { navigationActions.navigateTo(Screen.SETTINGS) },
            editButton = true,
            onEditButtonClick = { navigationActions.navigateTo(Screen.EDIT_PROFILE) },
        )
      },
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
      // Profile picture
      ProfilePicture(model = userAvatar ?: DEFAULT_PROFILE_PICTURE)

      // Name
      Text(
          modifier = Modifier.fillMaxWidth().wrapContentHeight().testTag(ProfileScreen.NAME_FIELD),
          text = userState?.name ?: context.getString(R.string.profile_default_name),
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.titleSmall,
      )

      // Description
      Text(
          modifier =
              Modifier.fillMaxWidth().wrapContentHeight().testTag(ProfileScreen.DESCRIPTION_FIELD),
          text = userState?.description ?: context.getString(R.string.profile_default_description),
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.bodyMedium,
      )

      // Contribution
      Text(
          modifier =
              Modifier.fillMaxWidth().wrapContentHeight().testTag(ProfileScreen.CONTRIBUTION_FIELD),
          text =
              if (numberInteractions == 0) context.getString(R.string.profile_new_user)
              else context.getString(R.string.profile_number_interaction_text) + numberInteractions,
          textAlign = TextAlign.Left,
          style = MaterialTheme.typography.bodyMedium,
      )

      // Review section text
      ProfileSection(
          text = context.getString(R.string.profile_reviews_title),
          testTag = ProfileScreen.REVIEWS_SECTION)

      // Reviews or no reviews card
      if (numberInteractions == 0) {
        NoReviewCard()
      } else {
        /** TODO: Implement the review section */
      }
    }
  }
}

/**
 * A composable function that displays a card indicating that there are no reviews available.
 *
 * This card contains an icon and a text message informing the user that no reviews are present.
 *
 * @sample NoReviewCard
 */
@Composable
private fun NoReviewCard() {
  val context = LocalContext.current
  Card(
      modifier = Modifier.wrapContentSize().testTag(ProfileScreen.NO_REVIEWS_CARD),
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
          imageVector = Icons.Outlined.SentimentVeryDissatisfied,
          contentDescription = "NoReviews",
          modifier =
              Modifier.size(MaterialTheme.dimens.iconSize).testTag(ProfileScreen.NO_REVIEWS_ICON),
      )
      Text(
          modifier = Modifier.wrapContentSize().testTag(ProfileScreen.NO_REVIEWS_TEXT),
          text = context.getString(R.string.profile_no_reviews_text),
          style = MaterialTheme.typography.bodyMedium,
      )
    }
  }
}

/**
 * Initializes the user profile.
 *
 * This function loads the user profile and downloads the user's profile picture.
 *
 * @param userViewModel The ViewModel that handles user data.
 * @param authenticationViewModel The ViewModel that handles authentication data.
 * @param chatViewModel The ViewModel that handles chat data.
 * @param userState The user's state.
 * @param onSuccess Callback function to be called when the user profile is successfully loaded.
 * @param onFailure Callback function to be called when there is an error loading the user profile.
 */
fun init(
    userViewModel: UserViewModel,
    authenticationViewModel: AuthenticationViewModel,
    chatViewModel: ChatViewModel,
    userState: User?,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
) {
  // Load the user's authentication data and connect the user to the chat services
  authenticationViewModel.loadAuthenticationUserData(
      onSuccess = {
        Log.d(TAG, "Authentication data loaded successfully")
        chatViewModel.connectUser(userState, authenticationViewModel = authenticationViewModel)
        userViewModel.loadUser(
            authenticationViewModel.authUserData.value!!.uid,
            onSuccess = {
              userViewModel.user.value?.let {
                userViewModel.downloadFile(
                    it.imageUrl,
                    onSuccess = { onSuccess() },
                    onFailure = { e: Exception -> onFailure(Exception(e)) })
              }
            },
            onFailure = { e: Exception -> onFailure(Exception(e)) })
      },
      onFailure = { Log.d(TAG, "Authentication data is null") })
}
