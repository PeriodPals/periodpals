package com.android.periodpals.resources

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A data class representing a set of standardized dimension values for consistent padding, spacing,
 * and sizing throughout the app's UI.
 *
 * These dimensions can be adjusted based on the device's screen size to ensure responsiveness and a
 * uniform look and feel across different screen types (e.g., compact, medium, expanded).
 *
 * For more info about which typography to use for different screen sizes, see the
 * [wiki page](https://github.com/PeriodPals/periodpals/wiki/App-Style-Guide#padding-and-typograhy).
 */
data class Dimens(
    val extraSmall: Dp = 0.dp, // not yet used
    val small1: Dp = 0.dp,
    val small2: Dp = small1 * 2,
    val small3: Dp = small1 * 4,
    val medium1: Dp = small1 * 6,
    val medium2: Dp = small1 * 8, // not yet used
    val medium3: Dp = small1 * 10,
    val large: Dp = small1 * 15,
    val borderLine: Dp = 1.dp,
    val buttonHeight: Dp = 40.dp, // not yet used
    val cardElevation: Dp = 4.dp,
    val cardRoundedSize: Dp = small1 * 3,
    val iconSize: Dp = 0.dp,
    val iconSizeSmall: Dp = iconSize * 2 / 3,
    val iconButtonSize: Dp = iconSize * 2,
    val profilePictureSize: Dp = small1 * 50,
    val roundedPercent: Int = 50,
)

// Width <= 360dp
val CompactSmallDimens = Dimens(small1 = 3.dp, iconSize = 20.dp)

// 360dp < Width <= 500dp
/** Reference padding and spacing values for a medium screen size. */
val CompactMediumDimens =
    Dimens(
        small1 = 4.dp,
        small2 = 8.dp,
        small3 = 16.dp,
        medium1 = 24.dp,
        medium2 = 32.dp,
        medium3 = 40.dp,
        large = 60.dp,
        borderLine = 1.dp,
        buttonHeight = 40.dp,
        cardElevation = 4.dp,
        cardRoundedSize = 12.dp,
        iconSize = 24.dp,
        iconSizeSmall = 16.dp,
        iconButtonSize = 40.dp,
        profilePictureSize = 200.dp,
        roundedPercent = 50,
    )

// 500dp < Width
val CompactLargeDimens = Dimens(small1 = 5.dp, iconSize = 30.dp)

val MediumDimens = Dimens(small1 = 8.dp, iconSize = 30.dp)

val ExpandedDimens = Dimens(small1 = 9.dp, iconSize = 30.dp)
