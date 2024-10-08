package com.android.sample.ui.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.android.sample.ui.theme.Purple80
import com.android.sample.ui.theme.PurpleGrey40

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavigationMenu(
    currentScreenTitle: String,
    onHomeClick: () -> Unit,
    onProfileClick: () -> Unit
) {
  CenterAlignedTopAppBar(
      title = {
        Text(
            text = currentScreenTitle,
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black,
            fontSize = 20.sp,
            textAlign = TextAlign.Center)
      },
      navigationIcon = {
        IconButton(onClick = onHomeClick) {
          Icon(imageVector = Icons.Filled.Home, contentDescription = "Home", tint = PurpleGrey40)
        }
      },
      actions = {
        IconButton(onClick = onProfileClick) {
          Icon(
              imageVector = Icons.Filled.AccountCircle,
              contentDescription = "Profile",
              tint = PurpleGrey40)
        }
      },
      colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Purple80),
      modifier = Modifier.fillMaxWidth())
}
