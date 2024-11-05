package com.android.periodpals.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.android.periodpals.R

// Nunuto Sans font family
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

val CompactSmallTypography =
    Typography(
        headlineLarge =
            TextStyle(
                fontFamily = Nunito_Sans,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Normal,
                fontSize = 32.sp,
                lineHeight = 48.sp),
        titleMedium =
            TextStyle(
                fontFamily = Nunito_Sans,
                fontWeight = FontWeight.Medium,
                fontStyle = FontStyle.Normal,
                fontSize = 10.sp),
        bodyLarge =
            TextStyle(
                fontFamily = Nunito_Sans,
                fontWeight = FontWeight.SemiBold,
                fontStyle = FontStyle.Normal,
                fontSize = 18.sp),
        bodyMedium =
            TextStyle(
                fontFamily = Nunito_Sans,
                fontWeight = FontWeight.Medium,
                fontStyle = FontStyle.Normal,
                fontSize = 16.sp),
        labelMedium =
            TextStyle(
                fontFamily = Nunito_Sans,
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Normal,
                fontSize = 14.sp),
        labelSmall =
            TextStyle(
                fontFamily = Nunito_Sans,
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Normal,
                fontSize = 12.sp))

val CompactMediumTypography =
    Typography(
        headlineLarge =
            TextStyle(
                fontFamily = Nunito_Sans,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Normal,
                fontSize = 40.sp,
                lineHeight = 60.sp),
        titleMedium =
            TextStyle(
                fontFamily = Nunito_Sans,
                fontWeight = FontWeight.Medium,
                fontStyle = FontStyle.Normal,
                fontSize = 14.sp),
        bodyLarge =
            TextStyle(
                fontFamily = Nunito_Sans,
                fontWeight = FontWeight.SemiBold,
                fontStyle = FontStyle.Normal,
                fontSize = 20.sp),
        bodyMedium =
            TextStyle(
                fontFamily = Nunito_Sans,
                fontWeight = FontWeight.Medium,
                fontStyle = FontStyle.Normal,
                fontSize = 18.sp),
        labelMedium =
            TextStyle(
                fontFamily = Nunito_Sans,
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Normal,
                fontSize = 16.sp),
        labelSmall =
            TextStyle(
                fontFamily = Nunito_Sans,
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Normal,
                fontSize = 14.sp))

val CompactLargeTypography =
    Typography(
        headlineLarge =
            TextStyle(
                fontFamily = Nunito_Sans,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Normal,
                fontSize = 32.sp),
        headlineMedium =
            TextStyle(
                fontFamily = Nunito_Sans,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Normal,
                fontSize = 24.sp),
        titleMedium =
            TextStyle(
                fontFamily = Nunito_Sans,
                fontWeight = FontWeight.Medium,
                fontStyle = FontStyle.Normal,
                fontSize = 14.sp),
        labelMedium =
            TextStyle(
                fontFamily = Nunito_Sans,
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Normal,
                fontSize = 14.sp))

val MediumTypography =
    Typography(
        headlineLarge =
            TextStyle(
                fontFamily = Nunito_Sans,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Normal,
                fontSize = 38.sp),
        headlineMedium =
            TextStyle(
                fontFamily = Nunito_Sans,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Normal,
                fontSize = 30.sp),
        titleMedium =
            TextStyle(
                fontFamily = Nunito_Sans,
                fontWeight = FontWeight.Medium,
                fontStyle = FontStyle.Normal,
                fontSize = 16.sp),
        labelMedium =
            TextStyle(
                fontFamily = Nunito_Sans,
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Normal,
                fontSize = 16.sp))

val ExpandedTypography =
    Typography(
        headlineLarge =
            TextStyle(
                fontFamily = Nunito_Sans,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Normal,
                fontSize = 42.sp),
        headlineMedium =
            TextStyle(
                fontFamily = Nunito_Sans,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Normal,
                fontSize = 34.sp),
        titleMedium =
            TextStyle(
                fontFamily = Nunito_Sans,
                fontWeight = FontWeight.Medium,
                fontStyle = FontStyle.Normal,
                fontSize = 18.sp),
        labelMedium =
            TextStyle(
                fontFamily = Nunito_Sans,
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Normal,
                fontSize = 18.sp))
