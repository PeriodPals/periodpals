package com.android.periodpals.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A composable that displays an instruction text with [text] and [testTag] for testing purposes.
 */
@Composable
fun ProfileText(text: String, testTag: String) {
  Text(
      modifier = Modifier.testTag(testTag),
      text = text,
      style =
          MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp, fontWeight = FontWeight.Medium))
}

/**
 * A composable that displays a description input with [description] and [onValueChange] action and
 * [testTag]
 */
@Composable
fun ProfileDescriptionInput(description: String, onValueChange: (String) -> Unit, testTag: String) {
  OutlinedTextField(
      value = description,
      onValueChange = onValueChange,
      label = { Text("Description") },
      placeholder = { Text("Enter a description") },
      modifier = Modifier.height(124.dp).testTag(testTag),
  )
}
