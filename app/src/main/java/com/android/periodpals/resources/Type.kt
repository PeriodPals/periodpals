package com.android.periodpals.resources

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.android.periodpals.R

// Nunito Sans font family
val Nunito_Sans =
    FontFamily(
        listOf(
            Font(resId = R.font.nunito_sans_black, weight = FontWeight.Black),
            Font(
                resId = R.font.nunito_sans_black_italic,
                weight = FontWeight.Black,
                style = FontStyle.Italic),
            Font(resId = R.font.nunito_sans_bold, weight = FontWeight.Bold),
            Font(
                resId = R.font.nunito_sans_bold_italic,
                weight = FontWeight.Bold,
                style = FontStyle.Italic),
            Font(resId = R.font.nunito_sans_extra_bold, weight = FontWeight.ExtraBold),
            Font(
                resId = R.font.nunito_sans_extra_bold_italic,
                weight = FontWeight.ExtraBold,
                style = FontStyle.Italic),
            Font(resId = R.font.nunito_sans_extra_light, weight = FontWeight.ExtraLight),
            Font(
                resId = R.font.nunito_sans_extra_light_italic,
                weight = FontWeight.ExtraLight,
                style = FontStyle.Italic),
            Font(resId = R.font.nunito_sans_italic, style = FontStyle.Italic),
            Font(resId = R.font.nunito_sans_light, weight = FontWeight.Light),
            Font(
                resId = R.font.nunito_sans_light_italic,
                weight = FontWeight.Light,
                style = FontStyle.Italic),
            Font(resId = R.font.nunito_sans_regular),
            Font(resId = R.font.nunito_sans_semi_bold, weight = FontWeight.SemiBold),
            Font(
                resId = R.font.nunito_sans_semi_bold_italic,
                weight = FontWeight.SemiBold,
                style = FontStyle.Italic)))

/**
 * A function that creates a [Typography] object with the specified text styles depending on the
 * screen size.
 *
 * For more info about which typography to use for different screen sizes, see the
 * [wiki page](https://github.com/PeriodPals/periodpals/wiki/App-Style-Guide#padding-and-typograhy).
 */
fun createTypography(
    headlineMediumSize: Int,
    headlineSmallSize: Int,
    titleLargeSize: Int,
    titleMediumSize: Int,
    titleSmallSize: Int,
    bodyLargeSize: Int,
    bodyMediumSize: Int,
    labelLargeSize: Int,
    labelMediumSize: Int,
    labelSmallSize: Int
): Typography {
  return Typography(
      headlineMedium =
          TextStyle(
              fontFamily = Nunito_Sans,
              fontWeight = FontWeight.Bold,
              fontStyle = FontStyle.Normal,
              fontSize = headlineMediumSize.sp),
      headlineSmall =
          TextStyle(
              fontFamily = Nunito_Sans,
              fontWeight = FontWeight.Bold,
              fontStyle = FontStyle.Normal,
              fontSize = headlineSmallSize.sp),
      titleLarge =
          TextStyle(
              fontFamily = Nunito_Sans,
              fontWeight = FontWeight.Black,
              fontStyle = FontStyle.Normal,
              fontSize = titleLargeSize.sp,
              lineHeight = (titleLargeSize * 1.5).sp),
      titleMedium =
          TextStyle(
              fontFamily = Nunito_Sans,
              fontWeight = FontWeight.SemiBold,
              fontStyle = FontStyle.Normal,
              fontSize = titleMediumSize.sp),
      titleSmall =
          TextStyle(
              fontFamily = Nunito_Sans,
              fontWeight = FontWeight.Bold,
              fontStyle = FontStyle.Normal,
              fontSize = titleSmallSize.sp),
      bodyLarge =
          TextStyle(
              fontFamily = Nunito_Sans,
              fontWeight = FontWeight.SemiBold,
              fontStyle = FontStyle.Normal,
              fontSize = bodyLargeSize.sp),
      bodyMedium =
          TextStyle(
              fontFamily = Nunito_Sans,
              fontWeight = FontWeight.SemiBold,
              fontStyle = FontStyle.Normal,
              fontSize = bodyMediumSize.sp),
      labelLarge =
          TextStyle(
              fontFamily = Nunito_Sans,
              fontWeight = FontWeight.Normal,
              fontStyle = FontStyle.Normal,
              fontSize = labelLargeSize.sp),
      labelMedium =
          TextStyle(
              fontFamily = Nunito_Sans,
              fontWeight = FontWeight.Normal,
              fontStyle = FontStyle.Normal,
              fontSize = labelMediumSize.sp),
      labelSmall =
          TextStyle(
              fontFamily = Nunito_Sans,
              fontWeight = FontWeight.Normal,
              fontStyle = FontStyle.Normal,
              fontSize = labelSmallSize.sp))
}

val CompactSmallTypography =
    createTypography(
        headlineMediumSize = 20,
        headlineSmallSize = 16,
        titleLargeSize = 32,
        titleMediumSize = 20,
        titleSmallSize = 18,
        bodyLargeSize = 18,
        bodyMediumSize = 16,
        labelLargeSize = 14,
        labelMediumSize = 12,
        labelSmallSize = 11)

val CompactMediumTypography =
    createTypography(
        headlineMediumSize = 24,
        headlineSmallSize = 18,
        titleLargeSize = 40,
        titleMediumSize = 22,
        titleSmallSize = 20,
        bodyLargeSize = 20,
        bodyMediumSize = 18,
        labelLargeSize = 16,
        labelMediumSize = 14,
        labelSmallSize = 12)

val CompactLargeTypography =
    createTypography(
        headlineMediumSize = 28,
        headlineSmallSize = 20,
        titleLargeSize = 48,
        titleMediumSize = 28,
        titleSmallSize = 24,
        bodyLargeSize = 24,
        bodyMediumSize = 20,
        labelLargeSize = 18,
        labelMediumSize = 16,
        labelSmallSize = 14)

val MediumTypography =
    createTypography(
        headlineMediumSize = 32,
        headlineSmallSize = 22,
        titleLargeSize = 56,
        titleMediumSize = 28,
        titleSmallSize = 24,
        bodyLargeSize = 24,
        bodyMediumSize = 22,
        labelLargeSize = 20,
        labelMediumSize = 18,
        labelSmallSize = 16)

val ExpandedTypography =
    createTypography(
        headlineMediumSize = 36,
        headlineSmallSize = 22,
        titleLargeSize = 64,
        titleMediumSize = 28,
        titleSmallSize = 24,
        bodyLargeSize = 24,
        bodyMediumSize = 22,
        labelLargeSize = 20,
        labelMediumSize = 18,
        labelSmallSize = 16)
