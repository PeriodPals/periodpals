package com.android.periodpals.ui.theme

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object PeriodPalsColor {
  val LightTheme: ColorScheme =
      lightColorScheme(
          primary = Color(0xFF415F91),
          onPrimary = Color(0xFFFFFFFF),
          primaryContainer = Color(0xFFD6E3FF),
          onPrimaryContainer = Color(0xFF001B3E),
          secondary = Color(0xFF565F71),
          onSecondary = Color(0xFFFFFFFF),
          secondaryContainer = Color(0xFFDAE2F9),
          onSecondaryContainer = Color(0xFF131C2B),
          tertiary = Color(0xFF5C5891),
          onTertiary = Color(0xFFFFFFFF),
          tertiaryContainer = Color(0xFFE3DFFF),
          onTertiaryContainer = Color(0xFF18124A),
          error = Color(0xFFBA1A1A),
          onError = Color(0xFFFFFFFF),
          errorContainer = Color(0xFFFFDAD6),
          onErrorContainer = Color(0xFF410002),
          background = Color(0xFFF9F9FF),
          onBackground = Color(0xFF191C20),
          surface = Color(0xFFF9F9FF),
          onSurface = Color(0xFF191C20),
          surfaceVariant = Color(0xFFE0E2EC),
          onSurfaceVariant = Color(0xFF44474E),
          outline = Color(0xFF74777F),
          outlineVariant = Color(0xFFC4C6D0),
          scrim = Color(0xFF000000),
          inverseSurface = Color(0xFF2E3036),
          inverseOnSurface = Color(0xFFF0F0F7),
          inversePrimary = Color(0xFFAAC7FF),
          surfaceDim = Color(0xFFD9D9E0),
          surfaceBright = Color(0xFFF9F9FF),
          surfaceContainerLowest = Color(0xFFFFFFFF),
          surfaceContainerLow = Color(0xFFF3F3FA),
          surfaceContainer = Color(0xFFEDEDF4),
          surfaceContainerHigh = Color(0xFFE7E8EE),
          surfaceContainerHighest = Color(0xFFE2E2E9))

  val DarkTheme: ColorScheme =
      darkColorScheme(
          primary = Color(0xFFAAC7FF),
          onPrimary = Color(0xFF0A305F),
          primaryContainer = Color(0xFF284777),
          onPrimaryContainer = Color(0xFFD6E3FF),
          secondary = Color(0xFFBEC6DC),
          onSecondary = Color(0xFF283141),
          secondaryContainer = Color(0xFF3E4759),
          onSecondaryContainer = Color(0xFFDAE2F9),
          tertiary = Color(0xFFC5C0FF),
          onTertiary = Color(0xFF2D2960),
          tertiaryContainer = Color(0xFF444078),
          onTertiaryContainer = Color(0xFFE3DFFF),
          error = Color(0xFFFFB4AB),
          onError = Color(0xFF690005),
          errorContainer = Color(0xFF93000A),
          onErrorContainer = Color(0xFFFFDAD6),
          background = Color(0xFF111318),
          onBackground = Color(0xFFE2E2E9),
          surface = Color(0xFF111318),
          onSurface = Color(0xFFE2E2E9),
          surfaceVariant = Color(0xFF44474E),
          onSurfaceVariant = Color(0xFFC4C6D0),
          outline = Color(0xFF8E9099),
          outlineVariant = Color(0xFF44474E),
          scrim = Color(0xFF000000),
          inverseSurface = Color(0xFFE2E2E9),
          inverseOnSurface = Color(0xFF2E3036),
          inversePrimary = Color(0xFF415F91),
          surfaceDim = Color(0xFF111318),
          surfaceBright = Color(0xFF37393E),
          surfaceContainerLowest = Color(0xFF0C0E13),
          surfaceContainerLow = Color(0xFF191C20),
          surfaceContainer = Color(0xFF1D2024),
          surfaceContainerHigh = Color(0xFF282A2F),
          surfaceContainerHighest = Color(0xFF33353A))
}

object ComponentColor {
  /** Returns the primary card colors for alert items. */
  @Composable
  fun getPrimaryCardColors(): CardColors {
    return CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    )
  }

  /** Returns the tertiary card colors for other cards. */
  @Composable
  fun getTertiaryCardColors(): CardColors {
    return CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
    )
  }

  /** Returns the filled primary container button colors. */
  @Composable
  fun getFilledPrimaryContainerButtonColors(): ButtonColors {
    return ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    )
  }

  /** Returns the filled tertiary container button colors. */
  @Composable
  fun getFilledPrimaryButtonColors(): ButtonColors {
    return ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
    )
  }

  /** Returns the filled icon button colors. */
  @Composable
  fun getFilledIconButtonColors(): IconButtonColors {
    return IconButtonDefaults.iconButtonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
    )
  }

  @Composable
  fun getTopAppBarIconButtonColors(): IconButtonColors {
    return IconButtonDefaults.iconButtonColors(
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    )
  }

  /** Returns the outlined text field colors. */
  @Composable
  fun getOutlinedTextFieldColors(): TextFieldColors {
    return OutlinedTextFieldDefaults.colors(
        focusedTextColor = MaterialTheme.colorScheme.primary,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
    )
  }

  /** Returns the menu outlined text field colors. */
  @Composable
  fun getMenuOutlinedTextFieldColors(): TextFieldColors {
    return OutlinedTextFieldDefaults.colors(
        focusedTextColor = MaterialTheme.colorScheme.primary,
        unfocusedTextColor = MaterialTheme.colorScheme.secondary,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        focusedLeadingIconColor = MaterialTheme.colorScheme.secondary,
        unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurface,
    )
  }

  /** Returns the menu text field colors. */
  @Composable
  fun getMenuTextFieldColors(): TextFieldColors {
    return TextFieldDefaults.colors(
        focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
        focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
        unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurface,
    )
  }

  /** Returns the dropdown menu item colors. */
  @Composable
  fun getMenuItemColors(): MenuItemColors {
    return MenuItemColors(
        textColor = MaterialTheme.colorScheme.onTertiaryContainer,
        leadingIconColor = MaterialTheme.colorScheme.onTertiaryContainer,
        trailingIconColor = MaterialTheme.colorScheme.onTertiaryContainer,
        disabledTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
        disabledLeadingIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
        disabledTrailingIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
    )
  }
}
