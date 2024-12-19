package com.android.periodpals.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import com.android.periodpals.resources.C.Tag.BottomNavigationMenu
import com.android.periodpals.services.NetworkChangeListener
import com.android.periodpals.ui.theme.dimens

@Composable
fun BottomNavigationMenu(
  onTabSelect: (TopLevelDestination) -> Unit,
  tabList: List<TopLevelDestination>,
  selectedItem: String,
  networkChangeListener: NetworkChangeListener
) {

  val isOnline by networkChangeListener.isNetworkAvailable.collectAsState()

  Column (
    modifier = Modifier.fillMaxWidth()
  ) {
    NavigationBar(
      modifier =
      Modifier.fillMaxWidth()
        .wrapContentHeight()
        .testTag(BottomNavigationMenu.BOTTOM_NAVIGATION_MENU),
      containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
      contentColor = MaterialTheme.colorScheme.onSurface,
      content = {
        tabList.forEach { tab ->
          NavigationBarItem(
            selected = tab.route == selectedItem,
            onClick = { onTabSelect(tab) },
            icon = {
              Icon(
                imageVector = tab.icon,
                contentDescription = null,
                modifier = Modifier.size(MaterialTheme.dimens.iconSize),
              )
            },
            modifier =
            Modifier.wrapContentSize()
              .clip(RoundedCornerShape(percent = MaterialTheme.dimens.roundedPercent))
              .align(Alignment.CenterVertically)
              .testTag(BottomNavigationMenu.BOTTOM_NAVIGATION_MENU_ITEM + tab.textId),
            label = { Text(text = tab.textId, style = MaterialTheme.typography.labelSmall) },
            colors =
            NavigationBarItemDefaults.colors(
              selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
              selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
              indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
              unselectedIconColor = MaterialTheme.colorScheme.onSurface,
              unselectedTextColor = MaterialTheme.colorScheme.onSurface,
            ),
          )
        }
      },
    )

    // Offline bar
    if (!isOnline) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(MaterialTheme.dimens.small3)
          .background(color = MaterialTheme.colorScheme.error),
        contentAlignment = Alignment.TopCenter,
      ) {
        Text(
          text = "No connection",
          color = MaterialTheme.colorScheme.onError,
          style = MaterialTheme.typography.bodySmall,
          textAlign = TextAlign.Center
        )
      }
    }
  }
}
