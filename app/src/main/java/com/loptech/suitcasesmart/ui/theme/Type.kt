package com.loptech.suitcasesmart.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.loptech.suitcasesmart.R

val SoraFontFamily = FontFamily(
    Font(R.font.sora_light,    FontWeight.Light),
    Font(R.font.sora_regular,  FontWeight.Normal),
    Font(R.font.sora_medium,   FontWeight.Medium),
    Font(R.font.sora_semibold, FontWeight.SemiBold),
    Font(R.font.sora_bold,     FontWeight.Bold),
)

val DmSansFontFamily = FontFamily(
    Font(R.font.dm_sans_regular, FontWeight.Normal),
    Font(R.font.dm_sans_medium,  FontWeight.Medium),
)

val Typography = Typography(
    displayLarge  = TextStyle(fontFamily = SoraFontFamily, fontWeight = FontWeight.Bold,     fontSize = 57.sp),
    displayMedium = TextStyle(fontFamily = SoraFontFamily, fontWeight = FontWeight.Bold,     fontSize = 45.sp),
    displaySmall  = TextStyle(fontFamily = SoraFontFamily, fontWeight = FontWeight.Bold,     fontSize = 36.sp),
    headlineLarge  = TextStyle(fontFamily = SoraFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 32.sp),
    headlineMedium = TextStyle(fontFamily = SoraFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 28.sp),
    headlineSmall  = TextStyle(fontFamily = SoraFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 24.sp),
    titleLarge  = TextStyle(fontFamily = SoraFontFamily, fontWeight = FontWeight.Bold,   fontSize = 22.sp),
    titleMedium = TextStyle(fontFamily = SoraFontFamily, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 24.sp),
    titleSmall  = TextStyle(fontFamily = SoraFontFamily, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp),
    bodyLarge  = TextStyle(fontFamily = DmSansFontFamily, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.5.sp),
    bodyMedium = TextStyle(fontFamily = DmSansFontFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall  = TextStyle(fontFamily = DmSansFontFamily, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 16.sp),
    labelLarge  = TextStyle(fontFamily = DmSansFontFamily, fontWeight = FontWeight.Medium, fontSize = 14.sp),
    labelMedium = TextStyle(fontFamily = DmSansFontFamily, fontWeight = FontWeight.Medium, fontSize = 12.sp),
    labelSmall  = TextStyle(fontFamily = DmSansFontFamily, fontWeight = FontWeight.Medium, fontSize = 11.sp, letterSpacing = 0.5.sp),
)
