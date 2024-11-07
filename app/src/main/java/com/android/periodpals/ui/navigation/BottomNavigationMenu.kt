package com.android.periodpals.ui.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.periodpals.resources.C.Tag.BottomNavigationMenu
import com.android.periodpals.ui.theme.dimens

@Composable
fun BottomNavigationMenu(
    onTabSelect: (TopLevelDestination) -> Unit,
    tabList: List<TopLevelDestination>,
    selectedItem: String,
) {
  NavigationBar(
      modifier =
          Modifier.fillMaxWidth()
              .wrapContentHeight()
              .testTag(BottomNavigationMenu.BOTTOM_NAVIGATION_MENU),
      containerColor = MaterialTheme.colorScheme.primaryContainer,
      content = {
        tabList.forEach { tab ->
          NavigationBarItem(
              modifier =
                  Modifier.wrapContentSize()
                      .clip(RoundedCornerShape(50.dp))
                      .align(Alignment.CenterVertically)
                      .testTag(BottomNavigationMenu.BOTTOM_NAVIGATION_MENU_ITEM + tab.textId),
              icon = { Icon(imageVector = tab.icon, contentDescription = null, modifier = Modifier.size(MaterialTheme.dimens.iconSize)) },
              label = { Text(text = tab.textId, style = MaterialTheme.typography.labelSmall) },
              selected = tab.route == selectedItem,
              onClick = { onTabSelect(tab) },
          )
        }
      },
  )
}
