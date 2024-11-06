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
val Nunito_Sans = FontFamily(
    listOf(
        Font(resId = R.font.nunito_sans_black, weight = FontWeight.Black),
        Font(resId = R.font.nunito_sans_black_italic, weight = FontWeight.Black, style = FontStyle.Italic),
        Font(resId = R.font.nunito_sans_bold, weight = FontWeight.Bold),
        Font(resId = R.font.nunito_sans_bold_italic, weight = FontWeight.Bold, style = FontStyle.Italic),
        Font(resId = R.font.nunito_sans_extra_bold, weight = FontWeight.ExtraBold),
        Font(resId = R.font.nunito_sans_extra_bold_italic, weight = FontWeight.ExtraBold, style = FontStyle.Italic),
        Font(resId = R.font.nunito_sans_extra_light, weight = FontWeight.ExtraLight),
        Font(resId = R.font.nunito_sans_extra_light_italic, weight = FontWeight.ExtraLight, style = FontStyle.Italic),
        Font(resId = R.font.nunito_sans_italic, style = FontStyle.Italic),
        Font(resId = R.font.nunito_sans_light, weight = FontWeight.Light),
        Font(resId = R.font.nunito_sans_light_italic, weight = FontWeight.Light, style = FontStyle.Italic),
        Font(resId = R.font.nunito_sans_regular),
        Font(resId = R.font.nunito_sans_semi_bold, weight = FontWeight.SemiBold),
        Font(resId = R.font.nunito_sans_semi_bold_italic, weight = FontWeight.SemiBold, style = FontStyle.Italic)
    )
)

fun createTypography(
    headlineLargeSize: Int,
    titleMediumSize: Int,
    bodyLargeSize: Int,
    bodyMediumSize: Int,
    labelMediumSize: Int,
    labelSmallSize: Int
): Typography {
    return Typography(
        headlineLarge = TextStyle(
            fontFamily = Nunito_Sans,
            fontWeight = FontWeight.Black,
            fontStyle = FontStyle.Normal,
            fontSize = headlineLargeSize.sp,
            lineHeight = (headlineLargeSize * 1.5).sp
        ),
        titleMedium = TextStyle(
            fontFamily = Nunito_Sans,
            fontWeight = FontWeight.Medium,
            fontStyle = FontStyle.Normal,
            fontSize = titleMediumSize.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = Nunito_Sans,
            fontWeight = FontWeight.SemiBold,
            fontStyle = FontStyle.Normal,
            fontSize = bodyLargeSize.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = Nunito_Sans,
            fontWeight = FontWeight.Medium,
            fontStyle = FontStyle.Normal,
            fontSize = bodyMediumSize.sp
        ),
        labelMedium = TextStyle(
            fontFamily = Nunito_Sans,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal,
            fontSize = labelMediumSize.sp
        ),
        labelSmall = TextStyle(
            fontFamily = Nunito_Sans,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal,
            fontSize = labelSmallSize.sp
        )
    )
}

val CompactSmallTypography = createTypography(
    headlineLargeSize = 32,
    titleMediumSize = 10,
    bodyLargeSize = 18,
    bodyMediumSize = 16,
    labelMediumSize = 14,
    labelSmallSize = 12
)

val CompactMediumTypography = createTypography(
    headlineLargeSize = 40,
    titleMediumSize = 14,
    bodyLargeSize = 20,
    bodyMediumSize = 18,
    labelMediumSize = 16,
    labelSmallSize = 14
)

val CompactLargeTypography = createTypography(
    headlineLargeSize = 48,
    titleMediumSize = 14,
    bodyLargeSize = 24,
    bodyMediumSize = 20,
    labelMediumSize = 18,
    labelSmallSize = 16
)

val MediumTypography = createTypography(
    headlineLargeSize = 56,
    titleMediumSize = 16,
    bodyLargeSize = 24,
    bodyMediumSize = 22,
    labelMediumSize = 20,
    labelSmallSize = 18
)

val ExpandedTypography = createTypography(
    headlineLargeSize = 64,
    titleMediumSize = 18,
    bodyLargeSize = 24,
    bodyMediumSize = 22,
    labelMediumSize = 20,
    labelSmallSize = 18
)