package com.android.periodpals.ui.profile

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SentimentVeryDissatisfied
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.periodpals.R
import com.android.periodpals.model.user.UserViewModel
import com.android.periodpals.resources.C.Tag.ProfileScreens.ProfileScreen
import com.android.periodpals.ui.components.ProfilePicture
import com.android.periodpals.ui.components.ProfileSection
import com.android.periodpals.ui.navigation.BottomNavigationMenu
import com.android.periodpals.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import com.android.periodpals.ui.navigation.TopAppBar
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi

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
 * @param navigationActions The navigation actions to navigate between screens.
 * @sample ProfileScreen
 */
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ProfileScreen(userViewModel: UserViewModel, navigationActions: NavigationActions) {
  val context = LocalContext.current
  val numberInteractions =
      0 // TODO: placeholder to be replaced when we integrate it to the User data class

  Log.d(TAG, "Loading user data")
  userViewModel.loadUser()
  val userState = userViewModel.user
  if (userState.value == null) {
    Log.d(TAG, "User data is null")
    Toast.makeText(context, "Error loading your data! Try again later.", Toast.LENGTH_SHORT).show()
  }

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag(ProfileScreen.SCREEN),
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationActions.currentRoute(),
        )
      },
      topBar = {
        TopAppBar(
            title = SCREEN_TITLE,
            editButton = true,
            onEditButtonClick = { navigationActions.navigateTo(Screen.EDIT_PROFILE) },
        )
      },
  ) { padding ->
    Column(
        modifier = Modifier.padding(padding).padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
    ) {
      // Profile picture
      ProfilePicture(
          model = userState.value?.imageUrl ?: DEFAULT_PROFILE_PICTURE.toString(),
          onClick = null,
      )

      // Name
      Text(
          text = userState.value?.name ?: DEFAULT_NAME,
          fontSize = 24.sp,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.testTag(ProfileScreen.NAME_FIELD),
      )

      // Description
      Text(
          text = userState.value?.description ?: DEFAULT_DESCRIPTION,
          textAlign = TextAlign.Center,
          fontSize = 20.sp,
          modifier = Modifier.testTag(ProfileScreen.DESCRIPTION_FIELD),
      )

      // Contribution
      Text(
          text =
              if (numberInteractions == 0) NEW_USER_TEXT
              else NUMBER_INTERACTION_TEXT + numberInteractions,
          fontSize = 16.sp,
          modifier = Modifier.align(Alignment.Start).testTag(ProfileScreen.CONTRIBUTION_FIELD),
      )

      // Review section text
      ProfileSection(
          text = REVIEWS_TITLE,
          testTag = ProfileScreen.REVIEWS_SECTION,
      )

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
      elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
      modifier = Modifier.testTag(ProfileScreen.NO_REVIEWS_CARD),
  ) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.padding(7.dp),
    ) {
      Icon(
          imageVector = Icons.Outlined.SentimentVeryDissatisfied,
          contentDescription = "NoReviews",
          modifier = Modifier.testTag(ProfileScreen.NO_REVIEWS_ICON),
      )
      Text(text = NO_REVIEWS_TEXT, modifier = Modifier.testTag(ProfileScreen.NO_REVIEWS_TEXT))
    }
  }
}
