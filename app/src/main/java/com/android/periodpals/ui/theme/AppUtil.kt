package com.android.periodpals.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember

/**
 * A composable function that provides the application dimensions to the composition.
 *
 * @param appDimens The dimensions to be provided.
 * @param content The content composable that will have access to the provided dimensions.
 */
@Composable
fun ProvideAppUtils(
    appDimens: Dimens,
    content: @Composable () -> Unit,
) {
  val appDimens = remember { appDimens }
  CompositionLocalProvider(LocalAppDimens provides appDimens) { content() }
}

val LocalAppDimens = compositionLocalOf { CompactDimens }
