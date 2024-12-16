package com.android.periodpals.ui.profile

import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
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
import com.android.periodpals.model.user.UserViewModel
import com.android.periodpals.resources.C.Tag.ProfileScreens.ProfileScreen
import com.android.periodpals.resources.ComponentColor.getTertiaryCardColors
import com.android.periodpals.services.PushNotificationsService
import com.android.periodpals.ui.components.ProfilePicture
import com.android.periodpals.ui.components.ProfileSection
import com.android.periodpals.ui.navigation.BottomNavigationMenu
import com.android.periodpals.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import com.android.periodpals.ui.navigation.TopAppBar
import com.android.periodpals.ui.theme.dimens

private const val SCREEN_TITLE = "Your Profile"
private const val TAG = "ProfileScreen"
private const val DEFAULT_NAME = "Error loading name, try again later."
private const val DEFAULT_DESCRIPTION = "Error loading description, try again later."
private val DEFAULT_PROFILE_PICTURE =
    Uri.parse("android.resource://com.android.periodpals/${R.drawable.generic_avatar}")

private const val NEW_USER_TEXT = "New user"
private const val NUMBER_INTERACTION_TEXT = "Number of interactions: "
private const val REVIEWS_TITLE = "Reviews"
private const val NO_REVIEWS_TEXT = "No reviews yet..."

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
    navigationActions: NavigationActions
) {
  val context = LocalContext.current
  val numberInteractions =
      0 // TODO: placeholder to be replaced when we integrate it to the User data class
  val userState by remember { userViewModel.user }
  val userAvatar by remember { userViewModel.avatar }

  Log.d(TAG, "Loading user data")
  userViewModel.init(
      onSuccess = { Log.d(TAG, "User data loaded successfully") },
      onFailure = { e: Exception ->
        Log.d(TAG, "Error loading user data: $e")
        Handler(Looper.getMainLooper()).post { // used to show the Toast in the main thread
          Toast.makeText(context, "Error loading your data! Try again later.", Toast.LENGTH_SHORT)
              .show()
        }
      },
  )

  // Load the user's authentication data and connect the user to the chat services
  authenticationViewModel.loadAuthenticationUserData(
      onSuccess = {
        Log.d(TAG, "Authentication data loaded successfully")
        chatViewModel.connectUser(userState, authenticationViewModel = authenticationViewModel)
      })

  // Only executed once
  LaunchedEffect(Unit) { notificationService.askPermission() }

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag(ProfileScreen.SCREEN),
      topBar = {
        TopAppBar(
            title = SCREEN_TITLE,
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
      // Profile picture
      ProfilePicture(model = userAvatar ?: DEFAULT_PROFILE_PICTURE)

      // Name
      Text(
          modifier = Modifier.fillMaxWidth().wrapContentHeight().testTag(ProfileScreen.NAME_FIELD),
          text = userState?.name ?: DEFAULT_NAME,
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.titleSmall,
      )

      // Description
      Text(
          modifier =
              Modifier.fillMaxWidth().wrapContentHeight().testTag(ProfileScreen.DESCRIPTION_FIELD),
          text = userState?.description ?: DEFAULT_DESCRIPTION,
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.bodyMedium,
      )

      // Contribution
      Text(
          modifier =
              Modifier.fillMaxWidth().wrapContentHeight().testTag(ProfileScreen.CONTRIBUTION_FIELD),
          text =
              if (numberInteractions == 0) NEW_USER_TEXT
              else NUMBER_INTERACTION_TEXT + numberInteractions,
          textAlign = TextAlign.Left,
          style = MaterialTheme.typography.bodyMedium,
      )

      // Review section text
      ProfileSection(text = REVIEWS_TITLE, testTag = ProfileScreen.REVIEWS_SECTION)

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
          text = NO_REVIEWS_TEXT,
          style = MaterialTheme.typography.bodyMedium,
      )
    }
  }
}
