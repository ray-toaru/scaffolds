package toaru.ray.android.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import toaru.ray.android.R

val provider =
    GoogleFont.Provider(
        providerAuthority = "com.google.android.gms.fonts",
        providerPackage = "com.google.android.gms",
        certificates = R.array.com_google_android_gms_fonts_certs,
    )

val bodyFontFamily =
    FontFamily(
        Font(
            googleFont = GoogleFont("AR One Sans"),
            fontProvider = provider,
        ),
    )

val displayFontFamily =
    FontFamily(
        Font(
            googleFont = GoogleFont("ADLaM Display"),
            fontProvider = provider,
        ),
    )

// Default Material 3 typography values
val baseline = Typography()

val AppTypography =
    Typography(
        displayLarge =
            baseline.displayLarge.copy(
                fontFamily = displayFontFamily,
                fontWeight = FontWeight.SemiBold,
            ),
        displayMedium =
            baseline.displayMedium.copy(
                fontFamily = displayFontFamily,
                fontWeight = FontWeight.SemiBold,
            ),
        displaySmall =
            baseline.displaySmall.copy(
                fontFamily = displayFontFamily,
                fontWeight = FontWeight.Medium,
            ),
        headlineLarge =
            baseline.headlineLarge.copy(
                fontFamily = displayFontFamily,
                fontWeight = FontWeight.SemiBold,
            ),
        headlineMedium =
            baseline.headlineMedium.copy(
                fontFamily = displayFontFamily,
                fontWeight = FontWeight.Medium,
            ),
        headlineSmall =
            baseline.headlineSmall.copy(
                fontFamily = bodyFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 26.sp,
                lineHeight = 32.sp,
            ),
        titleLarge =
            baseline.titleLarge.copy(
                fontFamily = bodyFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 22.sp,
                lineHeight = 28.sp,
            ),
        titleMedium =
            baseline.titleMedium.copy(
                fontFamily = bodyFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                lineHeight = 24.sp,
            ),
        titleSmall =
            baseline.titleSmall.copy(
                fontFamily = bodyFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                lineHeight = 22.sp,
            ),
        bodyLarge =
            baseline.bodyLarge.copy(
                fontFamily = bodyFontFamily,
                fontSize = 16.sp,
                lineHeight = 24.sp,
            ),
        bodyMedium =
            baseline.bodyMedium.copy(
                fontFamily = bodyFontFamily,
                fontSize = 14.sp,
                lineHeight = 20.sp,
            ),
        bodySmall =
            baseline.bodySmall.copy(
                fontFamily = bodyFontFamily,
                fontSize = 13.sp,
                lineHeight = 18.sp,
            ),
        labelLarge =
            baseline.labelLarge.copy(
                fontFamily = bodyFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 18.sp,
            ),
        labelMedium =
            baseline.labelMedium.copy(
                fontFamily = bodyFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                lineHeight = 16.sp,
            ),
        labelSmall =
            baseline.labelSmall.copy(
                fontFamily = bodyFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                lineHeight = 14.sp,
            ),
    )
