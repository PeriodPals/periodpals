package com.android.periodpals.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign

/**
 * A composable that displays an error message with [message] and [testTag] for testing purposes.
 */
@Composable
fun ErrorText(message: String, testTag: String) {
  Text(
      modifier = Modifier.fillMaxWidth().testTag(testTag),
      text = message,
      color = Color.Red,
      style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Start))
}
