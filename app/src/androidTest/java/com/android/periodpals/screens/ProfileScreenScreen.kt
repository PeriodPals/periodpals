package com.android.periodpals.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen

class ProfileScreenScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
  ComposeScreen<ProfileScreenScreen>(
    semanticsProvider = semanticsProvider,
    viewBuilderAction = { hasTestTag("profileScreen") },
  )
