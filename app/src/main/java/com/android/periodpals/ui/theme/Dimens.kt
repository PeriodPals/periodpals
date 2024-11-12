package com.android.periodpals.ui.theme

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
    val small2: Dp = 0.dp,
    val small3: Dp = 0.dp,
    val medium1: Dp = 0.dp,
    val medium2: Dp = 0.dp, // not yet used
    val medium3: Dp = 0.dp,
    val large: Dp = 0.dp,
    val borderLine: Dp = 1.dp,
    val buttonHeight: Dp = 40.dp,
    val buttonRoundedPercent: Int = 50,
    val cardElevation: Dp = 4.dp,
    val cardRounded: Dp = 12.dp,
    val iconSize: Dp = 0.dp,
    val iconSizeSmall: Dp = iconSize * 2 / 3,
    val iconButtonSize: Dp = iconSize * 5 / 3,
    val profilePictureSize: Dp = 190.dp,
)

// Width <= 360dp
val CompactSmallDimens =
    Dimens(
        small1 = 3.dp,
        small2 = 6.dp,
        small3 = 12.dp,
        medium1 = 18.dp,
        medium2 = 24.dp,
        medium3 = 30.dp,
        large = 45.dp,
        cardRounded = 9.dp,
        iconSize = 20.dp)

// 360dp < Width <= 500dp
val CompactMediumDimens =
    Dimens(
        small1 = 4.dp,
        small2 = 8.dp,
        small3 = 16.dp,
        medium1 = 24.dp,
        medium2 = 32.dp,
        medium3 = 40.dp,
        large = 60.dp,
        cardRounded = 12.dp,
        iconSize = 24.dp)

// 500dp < Width
val CompactLargeDimens =
    Dimens(
        small1 = 5.dp,
        small2 = 10.dp,
        small3 = 20.dp,
        medium1 = 30.dp,
        medium2 = 40.dp,
        medium3 = 50.dp,
        large = 65.dp,
        cardRounded = 15.dp,
        iconSize = 26.dp)

val MediumDimens =
    Dimens(
        small1 = 8.dp,
        small2 = 16.dp,
        small3 = 32.dp,
        medium1 = 48.dp,
        medium2 = 64.dp,
        medium3 = 80.dp,
        large = 120.dp,
        cardRounded = 24.dp,
        iconSize = 30.dp)

val ExpandedDimens =
    Dimens(
        small1 = 9.dp,
        small2 = 18.dp,
        small3 = 36.dp,
        medium1 = 54.dp,
        medium2 = 72.dp,
        medium3 = 90.dp,
        large = 135.dp,
        cardRounded = 27.dp,
        iconSize = 30.dp)
