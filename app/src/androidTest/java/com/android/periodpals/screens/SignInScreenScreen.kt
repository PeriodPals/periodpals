package com.android.periodpals.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen

class SignInScreenScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
  ComposeScreen<SignInScreenScreen>(
    semanticsProvider = semanticsProvider,
    viewBuilderAction = { hasTestTag("signInScreen") },
  )
