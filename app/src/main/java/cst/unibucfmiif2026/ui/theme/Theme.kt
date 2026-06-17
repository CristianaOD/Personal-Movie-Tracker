package cst.unibucfmiif2026.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

val LocalAppDarkMode = staticCompositionLocalOf { false }

private val DarkColorScheme = darkColorScheme(
	primary = CinemaGold,
	onPrimary = CinemaGoldDark,
	secondary = CinemaBlue,
	onSecondary = CinemaBackground,
	tertiary = MovieSky,
	background = CinemaBackground,
	onBackground = CinemaText,
	surface = CinemaSurface,
	onSurface = CinemaText,
	surfaceVariant = CinemaSurfaceVariant,
	onSurfaceVariant = ColorWhiteSoft,
	secondaryContainer = CinemaSurfaceVariant,
	onSecondaryContainer = CinemaText,
	outline = CinemaOutline,
	surfaceContainer = CinemaSurface,
	surfaceContainerHigh = CinemaSurfaceRaised,
	surfaceContainerHighest = CinemaSurfaceVariant
)

private val LightColorScheme = lightColorScheme(
	primary = SoftCinemaGreen,
	onPrimary = SoftCinemaGreenDark,
	secondary = SoftCinemaTextMuted,
	onSecondary = ColorWhitePure,
	tertiary = SoftCinemaOrange,
	onTertiary = SoftCinemaText,
	background = SoftCinemaBackground,
	onBackground = SoftCinemaText,
	surface = SoftCinemaSurface,
	onSurface = SoftCinemaText,
	surfaceVariant = SoftCinemaSurfaceVariant,
	onSurfaceVariant = SoftCinemaTextMuted,
	secondaryContainer = SoftCinemaSurfaceRaised,
	onSecondaryContainer = SoftCinemaText,
	outline = SoftCinemaOutline,
	surfaceContainer = SoftCinemaSurface,
	surfaceContainerHigh = SoftCinemaSurfaceRaised,
	surfaceContainerHighest = SoftCinemaSurfaceVariant

	/* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun UniBucFMIIF2026Theme(
	darkTheme: Boolean = isSystemInDarkTheme(),
	// Dynamic color is available on Android 12+
	dynamicColor: Boolean = true,
	content: @Composable () -> Unit
) {
	val colorScheme = when {
		dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
			val context = LocalContext.current
			if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
		}

		darkTheme -> DarkColorScheme
		else -> LightColorScheme
	}

	CompositionLocalProvider(LocalAppDarkMode provides darkTheme) {
		MaterialTheme(
			colorScheme = colorScheme,
			typography = Typography,
			content = content
		)
	}
}
