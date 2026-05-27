package cst.unibucfmiif2026.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

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
	primary = MovieBlue,
	onPrimary = ColorWhitePure,
	secondary = MovieSlate,
	onSecondary = ColorWhitePure,
	tertiary = MovieBlueDark,
	background = MovieBackground,
	onBackground = Ink900,
	surface = MovieSurface,
	onSurface = Ink900,
	surfaceVariant = MovieSurfaceVariant,
	onSurfaceVariant = Ink700,
	secondaryContainer = MovieSky,
	onSecondaryContainer = MovieBlueDark,
	outline = MovieOutline,
	surfaceContainer = MovieSurface,
	surfaceContainerHigh = Color(0xFFF0F3FA),
	surfaceContainerHighest = MovieSurfaceVariant

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

	MaterialTheme(
		colorScheme = colorScheme,
		typography = Typography,
		content = content
	)
}
