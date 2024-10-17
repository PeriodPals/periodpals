package com.android.periodpals.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen

class CreateProfileScreenScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
  ComposeScreen<CreateProfileScreenScreen>(
    semanticsProvider = semanticsProvider,
    viewBuilderAction = { hasTestTag("createProfileScreen") },
  )
