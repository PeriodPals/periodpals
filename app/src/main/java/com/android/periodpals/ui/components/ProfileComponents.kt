package com.android.periodpals.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

/**
 * A composable that displays an instruction text with [text] and [testTag] for testing purposes.
 */
@Composable
fun ProfileSection(text: String, testTag: String) {
  Text(
      modifier = Modifier.fillMaxWidth().testTag(testTag),
      text = text,
      textAlign = TextAlign.Start,
      style =
          MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp, fontWeight = FontWeight.Medium))
}
