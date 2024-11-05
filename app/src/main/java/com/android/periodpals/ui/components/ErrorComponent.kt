package com.android.periodpals.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
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
      modifier = Modifier.fillMaxWidth().wrapContentHeight().testTag(testTag),
      text = message,
      color = Color.Red,
      textAlign = TextAlign.Start,
      style = MaterialTheme.typography.labelLarge)
}
