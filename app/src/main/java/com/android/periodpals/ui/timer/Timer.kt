package com.android.periodpals.ui.timer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.android.periodpals.R
import com.android.periodpals.resources.C.Tag.TimerScreen
import com.android.periodpals.ui.navigation.BottomNavigationMenu
import com.android.periodpals.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.TopAppBar
import com.android.periodpals.ui.theme.dimens

private const val SCREEN_TITLE = "Tampon Timer"

/** TODO: Placeholder Screen, waiting for implementation */

@Composable
fun TimerScreen(navigationActions: NavigationActions) {
  Scaffold(
      modifier = Modifier.fillMaxSize().testTag(TimerScreen.SCREEN),
      topBar = { TopAppBar(title = SCREEN_TITLE) },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationActions.currentRoute())
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
      //Text("Timer Screen", modifier = Modifier.fillMaxSize().testTag(TimerScreen.TIMER_TEXT))
      Icon(
        painter = painterResource(R.drawable.tampon_and_pad),
        contentDescription = "Tampon and pad icon")
      Icon(
        painter = painterResource(R.drawable.cotton),
        contentDescription = "Tampon and pad icon")
      Icon(
        painter = painterResource(R.drawable.tampon),
        contentDescription = "Tampon and pad icon")
      Icon(
        painter = painterResource(R.drawable.pad),
        contentDescription = "Tampon and pad icon")
      Icon(
        painter = painterResource(R.drawable.urgency_1),
        contentDescription = "Tampon and pad icon")
      Icon(
        painter = painterResource(R.drawable.urgency_2),
        contentDescription = "Tampon and pad icon")
      Icon(
        painter = painterResource(R.drawable.urgency_3),
        contentDescription = "Tampon and pad icon")

    }
  }
}
