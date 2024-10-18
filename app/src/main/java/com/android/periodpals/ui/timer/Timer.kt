package com.android.periodpals.ui.timer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.android.periodpals.ui.navigation.BottomNavigationMenu
import com.android.periodpals.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.TopAppBar

/* Placeholder Screen, waiting for implementation */
@Composable
fun TimerScreen(navigationActions: NavigationActions) {
  Scaffold(
      bottomBar = ({
            BottomNavigationMenu(
                onTabSelect = { route -> navigationActions.navigateTo(route) },
                tabList = LIST_TOP_LEVEL_DESTINATION,
                selectedItem = navigationActions.currentRoute())
          }),
      topBar = {
        TopAppBar(
            title = "Timer",
        )
      },
      content = { pd -> Text("Timer Screen", modifier = Modifier.fillMaxSize().padding(pd)) })
}
