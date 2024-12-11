package com.android.periodpals.ui.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import com.android.periodpals.R
import com.android.periodpals.model.user.UserViewModel
import com.android.periodpals.resources.C.Tag.AlertListsScreen
import com.android.periodpals.resources.C.Tag.ProfileScreens
import com.android.periodpals.resources.C.Tag.ProfileScreens.CreateProfileScreen
import com.android.periodpals.ui.components.CreateProfileSaveButton
import com.android.periodpals.ui.components.MANDATORY_TEXT
import com.android.periodpals.ui.components.PROFILE_TEXT
import com.android.periodpals.ui.components.ProfileInputDescription
import com.android.periodpals.ui.components.ProfileInputDob
import com.android.periodpals.ui.components.ProfileInputName
import com.android.periodpals.ui.components.ProfilePicture
import com.android.periodpals.ui.components.ProfileSection
import com.android.periodpals.ui.components.uriToByteArray
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.TopAppBar
import com.android.periodpals.ui.theme.dimens
import com.dsc.form_builder.TextFieldState
import kotlin.math.roundToInt

private const val SCREEN_TITLE = "Create Your Account"
private val DEFAULT_PROFILE_PICTURE =
    Uri.parse("android.resource://com.android.periodpals/${R.drawable.generic_avatar}")
private const val MIN_RADIUS = 100
private const val MAX_RADIUS = 1000
private const val KILOMETERS_IN_METERS = 1000
/**
 * Composable function for the Create Profile screen.
 *
 * @param navigationActions Actions to handle navigation events.
 */
@Composable
fun CreateProfileScreen(userViewModel: UserViewModel, navigationActions: NavigationActions) {
  val context = LocalContext.current
  val formState = remember { userViewModel.formState }
  formState.reset()

  val nameState = formState.getState<TextFieldState>(UserViewModel.NAME_STATE_NAME)
  val descriptionState = formState.getState<TextFieldState>(UserViewModel.DESCRIPTION_STATE_NAME)
  val dobState = formState.getState<TextFieldState>(UserViewModel.DOB_STATE_NAME)
  val profileImageState = formState.getState<TextFieldState>(UserViewModel.PROFILE_IMAGE_STATE_NAME)
  var userAvatarState by remember {
    mutableStateOf<ByteArray?>(DEFAULT_PROFILE_PICTURE.uriToByteArray(context))
  }
  var sliderPosition by remember { mutableFloatStateOf(500F) }

  val launcher =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
              profileImageState.change(result.data?.data.toString())
              userAvatarState = result.data?.data?.uriToByteArray(context)
            }
          }

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag(CreateProfileScreen.SCREEN),
      topBar = { TopAppBar(title = SCREEN_TITLE) },
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
      ProfilePicture(
          model = userAvatarState,
          onClick = {
            val pickImageIntent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
            launcher.launch(pickImageIntent)
          },
      )

      // Mandatory section title
      ProfileSection(text = MANDATORY_TEXT, testTag = ProfileScreens.MANDATORY_SECTION)

      // Name input field
      ProfileInputName(name = nameState.value, onValueChange = { nameState.change(it) })

      // Date of birth input field
      ProfileInputDob(dob = dobState.value, onValueChange = { dobState.change(it) })

      // Your profile section title
      ProfileSection(text = PROFILE_TEXT, testTag = ProfileScreens.YOUR_PROFILE_SECTION)

      // Description input field
      ProfileInputDescription(
          description = descriptionState.value,
          onValueChange = { descriptionState.change(it) },
      )

      Text(
          text =
              "By specifying this radius, " +
                  "you can control the geographical range for receiving alerts from other users.",
          style = MaterialTheme.typography.labelMedium,
          modifier =
              Modifier.wrapContentHeight()
                  .fillMaxWidth()
                  .padding(top = MaterialTheme.dimens.small3),
          textAlign = TextAlign.Center,
      )

      // radius input field
      Text(
          text =
              if (sliderPosition < KILOMETERS_IN_METERS)
                  "Radius: $sliderPosition m from your position"
              else "Radius: ${sliderPosition / KILOMETERS_IN_METERS} km from your position",
          style = MaterialTheme.typography.bodySmall,
          modifier =
              Modifier.wrapContentHeight()
                  .fillMaxWidth()
                  .testTag(AlertListsScreen.FILTER_RADIUS_TEXT),
          textAlign = TextAlign.Center)

      Slider(
          value = sliderPosition,
          onValueChange = { sliderPosition = (it / 100).roundToInt() * 100f }, // Round to 100
          valueRange = MIN_RADIUS.toFloat()..MAX_RADIUS.toFloat(),
          steps = (MAX_RADIUS - MIN_RADIUS) / 100 - 1,
          modifier =
              Modifier.wrapContentHeight()
                  .fillMaxWidth()
                  .testTag(AlertListsScreen.FILTER_RADIUS_SLIDER),
      )

      // Save button
      CreateProfileSaveButton(
          nameState,
          dobState,
          descriptionState,
          profileImageState,
          userAvatarState,
          sliderPosition,
          context,
          userViewModel,
          navigationActions,
      )
    }
  }
}
