package com.android.periodpals.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.android.periodpals.R

// Set of Material typography styles to start with

// TODO: delete
//// For the moment, use the device default typography
// val Typography = Typography()

val Nunito_Sans =
    FontFamily(
        listOf(
            Font(
                resId = R.font.nunito_sans_black,
                weight = FontWeight.Black,
                style = FontStyle.Normal),
            Font(
                resId = R.font.nunito_sans_black_italic,
                weight = FontWeight.Black,
                style = FontStyle.Italic),
            Font(
                resId = R.font.nunito_sans_bold,
                weight = FontWeight.Bold,
                style = FontStyle.Normal),
            Font(
                resId = R.font.nunito_sans_bold_italic,
                weight = FontWeight.Bold,
                style = FontStyle.Italic),
            Font(
                resId = R.font.nunito_sans_extra_bold,
                weight = FontWeight.ExtraBold,
                style = FontStyle.Normal),
            Font(
                resId = R.font.nunito_sans_extra_bold_italic,
                weight = FontWeight.ExtraBold,
                style = FontStyle.Italic),
            Font(
                resId = R.font.nunito_sans_extra_light,
                weight = FontWeight.ExtraLight,
                style = FontStyle.Normal),
            Font(
                resId = R.font.nunito_sans_extra_light_italic,
                weight = FontWeight.ExtraLight,
                style = FontStyle.Italic),
            Font(
                resId = R.font.nunito_sans_italic,
                weight = FontWeight.Normal,
                style = FontStyle.Italic),
            Font(
                resId = R.font.nunito_sans_light,
                weight = FontWeight.Light,
                style = FontStyle.Normal),
            Font(
                resId = R.font.nunito_sans_light_italic,
                weight = FontWeight.Light,
                style = FontStyle.Italic),
            Font(
                resId = R.font.nunito_sans_regular,
                weight = FontWeight.Normal,
                style = FontStyle.Normal),
            Font(
                resId = R.font.nunito_sans_semi_bold,
                weight = FontWeight.SemiBold,
                style = FontStyle.Normal),
            Font(
                resId = R.font.nunito_sans_semi_bold_italic,
                weight = FontWeight.SemiBold,
                style = FontStyle.Italic)))

// Set of Material typography styles to start with
val CompactTypography =
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

val CompactMediumTypography =
    Typography(
        headlineLarge =
            TextStyle(
                fontFamily = Nunito_Sans,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Normal,
                fontSize = 28.sp),
        headlineMedium =
            TextStyle(
                fontFamily = Nunito_Sans,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Normal,
                fontSize = 22.sp),
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

val CompactSmallTypography =
    Typography(
        headlineLarge =
            TextStyle(
                fontFamily = Nunito_Sans,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Normal,
                fontSize = 22.sp),
        headlineMedium =
            TextStyle(
                fontFamily = Nunito_Sans,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Normal,
                fontSize = 16.sp),
        titleMedium =
            TextStyle(
                fontFamily = Nunito_Sans,
                fontWeight = FontWeight.Medium,
                fontStyle = FontStyle.Normal,
                fontSize = 10.sp),
        labelMedium =
            TextStyle(
                fontFamily = Nunito_Sans,
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Normal,
                fontSize = 10.sp))

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
