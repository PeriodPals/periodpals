package com.android.periodpals.ui.chat

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.android.periodpals.model.chat.ChatViewModel
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.TopAppBar
import com.android.periodpals.ui.theme.dimens

private const val SCREEN_TITLE = "ChatScreen"

/**
 * Screen that displays the top app bar, bottom navigation bar and the chat.
 *
 * @param chatViewModel The ViewModel for managing chat-related data and operations.
 * @param navigationActions The actions for navigating between screens.
 */
@Composable
fun ChatScreen(chatViewModel: ChatViewModel, navigationActions: NavigationActions) {
  Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
            title = SCREEN_TITLE,
            backButton = true,
            onBackButtonClick = { navigationActions.goBack() })
      },
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
      // TODO: delete when implementing the screen
      Text("Chat Screen", modifier = Modifier.fillMaxSize())
    }
  }
}
