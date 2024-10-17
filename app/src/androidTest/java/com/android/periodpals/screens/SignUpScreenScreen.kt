package com.android.periodpals.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen

class SignUpScreenScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
  ComposeScreen<SignUpScreenScreen>(
    semanticsProvider = semanticsProvider,
    viewBuilderAction = { hasTestTag("signUpScreen") },
  )
