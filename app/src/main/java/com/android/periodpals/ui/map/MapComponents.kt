package com.android.periodpals.ui.map

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AcUnit
import androidx.compose.material.icons.outlined.Accessibility
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.android.periodpals.ui.theme.dimens



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapBottomSheet(modifier: Modifier = Modifier) {
  val sheetState = rememberModalBottomSheetState()
  val scope = rememberCoroutineScope()
  var showBottomSheet by remember { mutableStateOf(true) }

  if (showBottomSheet) {
    ModalBottomSheet(
      onDismissRequest = { showBottomSheet = false },
      sheetState = sheetState
    ) {
      Row (
        modifier = Modifier
          .fillMaxWidth()
          .padding(
          horizontal = MaterialTheme.dimens.small3,
          vertical = MaterialTheme.dimens.small1)
      ) {

        Icon(
          imageVector = Icons.Outlined.AccountCircle,
          contentDescription = "Profile picture",
          modifier =
          Modifier.size(MaterialTheme.dimens.iconSize).wrapContentSize()
        )

        Column {
          Text (
            text = "Skibidi"
          )

          Text (
            text = "Location"
          )
        }

        Icon(
          imageVector = Icons.Outlined.AcUnit,
          contentDescription = "Product type"
        )

        Icon(
          imageVector = Icons.Outlined.Accessibility,
          contentDescription = "Urgency level"
        )
      }
    }
  }
}

