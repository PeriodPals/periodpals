package com.android.sample.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.android.sample.ui.theme.PurpleGrey80

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavigationMenu(
    currentScreenTitle: String,
    onHomeClick: () -> Unit,
    onProfileClick: () -> Unit
) {
  TopAppBar(
      title = { currentScreenTitle },
      colors =
          TopAppBarDefaults.topAppBarColors(
              containerColor = PurpleGrey80,
          ),
      navigationIcon = {
        IconButton(onClick = onHomeClick) {
          Icon(imageVector = Icons.Outlined.Home, contentDescription = "Home")
        }
      },
      actions = {
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()) {
              IconButton(onClick = onProfileClick) {
                Icon(imageVector = Icons.Outlined.AccountCircle, contentDescription = "Profile")
              }
            }
      },
      modifier = Modifier.fillMaxWidth(),
  )
}
