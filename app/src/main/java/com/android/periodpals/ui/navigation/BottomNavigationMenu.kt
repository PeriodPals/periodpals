package com.android.periodpals.ui.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Displays a bottom navigation menu with selectable tabs.
 *
 * @param onTabSelect Called when a tab is selected, receiving the selected [TopLevelDestination].
 * @param tabList List of [TopLevelDestination] representing the tabs.
 * @param selectedItem The route of the currently selected tab.
 *
 * ### Usage:
 * ```
 * BottomNavigationMenu(
 *     onTabSelect = { route -> navigationActions.navigateTo(route) },
 *     tabList = LIST_TOP_LEVEL_DESTINATION,
 *     selectedItem = navigationActions.currentRoute()
 * )
 * ```
 *
 * ### Testing:
 * Verify the bottom navigation menu is displayed with the testTag "bottomNavigationMenu".
 */
@Composable
fun BottomNavigationMenu(
    onTabSelect: (TopLevelDestination) -> Unit,
    tabList: List<TopLevelDestination>,
    selectedItem: String
) {
  NavigationBar(
      modifier = Modifier.fillMaxWidth().height(60.dp).testTag("bottomNavigationMenu"),
      containerColor = MaterialTheme.colorScheme.surface,
      content = {
        tabList.forEach { tab ->
          NavigationBarItem(
              modifier =
                  Modifier.clip(RoundedCornerShape(50.dp))
                      .align(Alignment.CenterVertically)
                      .testTag(tab.textId),
              icon = { Icon(tab.icon, contentDescription = null) },
              label = {
                Text(
                    text = tab.textId,
                    style =
                        MaterialTheme.typography.bodySmall.copy(
                            fontSize = 10.sp, fontWeight = FontWeight.Normal))
              },
              selected = tab.route == selectedItem,
              onClick = { onTabSelect(tab) })
        }
      })
}
