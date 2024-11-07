package com.android.periodpals.ui.theme

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.android.periodpals.MainActivity

// Largest compact S width
private const val COMPACT_S = 360
// Largest compact M width
private const val COMPACT_M = 420

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun PeriodPalsAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    activity: Activity = LocalContext.current as MainActivity,
    content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) PeriodPalsColor.DarkTheme else PeriodPalsColor.LightTheme

  // Match the system bar to the primary color of the app when the app isn't in edit mode (that is,
  // actually running in a device/emulator)
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as Activity).window
      window.statusBarColor = colorScheme.primary.toArgb()
      WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
    }
  }

  val window = calculateWindowSizeClass(activity = activity)
  val config = LocalConfiguration.current

  var appDimens = CompactLargeDimens
  var typography = CompactLargeTypography
  var toasty = ""
  when (window.widthSizeClass) {
    WindowWidthSizeClass.Compact -> {
      if (config.screenWidthDp <= COMPACT_S) {
        // Emulator Small Phone API 34
        appDimens = CompactSmallDimens
        typography = CompactSmallTypography
        toasty = "CompactSmall"
      } else if (config.screenWidthDp <= COMPACT_M) {
        // Emulator Pixel 2 API 34
        appDimens = CompactMediumDimens
        typography = CompactMediumTypography
        toasty = "CompactMedium"
      } else {
        // Emulator Pixel 9 Pro XL API 34
        appDimens = CompactLargeDimens
        typography = CompactLargeTypography
        toasty = "CompactLarge"
      }
    }
    WindowWidthSizeClass.Medium -> {
      // Emulator Medium Tablet API 34
      appDimens = MediumDimens
      typography = MediumTypography
      toasty = "Medium"
    }
    WindowWidthSizeClass.Expanded -> {
      // Pixel C API 34
      appDimens = ExpandedDimens
      typography = ExpandedTypography
      toasty = "Expanded"
    }
  }
  // TODO: delete
  Toast.makeText(activity, toasty, Toast.LENGTH_SHORT).show()

  ProvideAppUtils(appDimens = appDimens) {
    MaterialTheme(colorScheme = colorScheme, typography = typography, content = content)
  }
}

/** A composable function that provides the application dimensions to the composition. */
val MaterialTheme.dimens
  @Composable get() = LocalAppDimens.current
