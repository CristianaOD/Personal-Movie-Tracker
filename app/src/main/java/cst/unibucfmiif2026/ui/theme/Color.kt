package cst.unibucfmiif2026.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

val MovieBlue = Color(0xFF3E5F99)
val MovieBlueDark = Color(0xFF2B4A7E)
val MovieSlate = Color(0xFF5F6F8D)
val MovieSky = Color(0xFFDCE7FF)
val MovieBackground = Color(0xFFF5F7FC)
val MovieSurface = Color(0xFFFFFFFF)
val MovieSurfaceVariant = Color(0xFFE2E7F2)
val MovieOutline = Color(0xFF7A8598)

val CinemaGold = Color(0xFFFFC857)
val CinemaGoldDark = Color(0xFF402D00)
val CinemaBlue = Color(0xFF9CB6E7)
val CinemaBackground = Color(0xFF10131A)
val CinemaSurface = Color(0xFF171B24)
val CinemaSurfaceRaised = Color(0xFF212734)
val CinemaSurfaceVariant = Color(0xFF2A3140)
val CinemaOutline = Color(0xFF8C96AA)
val CinemaText = Color(0xFFE8ECF4)

val ColorWhitePure = Color(0xFFFFFFFF)
val ColorWhiteSoft = Color(0xFFCFD6E6)
val Ink900 = Color(0xFF171B24)
val Ink700 = Color(0xFF4F596B)

// Soft cinema light
val SoftCinemaGreen = Color(0xFF00B85A)
val SoftCinemaGreenDark = Color(0xFF00391F)
val SoftCinemaBackground = Color(0xFFF6F7F4)
val SoftCinemaSurface = Color(0xFFFFFFFF)
val SoftCinemaSurfaceRaised = Color(0xFFEEF1EC)
val SoftCinemaSurfaceVariant = Color(0xFFE1E6DE)
val SoftCinemaOutline = Color(0xFF96A093)
val SoftCinemaText = Color(0xFF1C1C1C)
val SoftCinemaTextMuted = Color(0xFF596257)
val SoftCinemaOrange = Color(0xFFFF8A3D)

private val LbDarkBackground = Color(0xFF1C1C1C)
private val LbDarkSurface = Color(0xFF262626)
private val LbDarkSurfaceRaised = Color(0xFF2E2E2E)
private val LbDarkBorder = Color(0xFF333333)
private val LbDarkBorderSubtle = Color(0xFF2A2A2A)
private val LbDarkGreen = Color(0xFF00E054)
private val LbDarkOrange = Color(0xFFFF8000)
private val LbDarkBlue = Color(0xFF40BCF4)
private val LbDarkTextPrimary = Color(0xFFFFFFFF)
private val LbDarkTextSecondary = Color(0xFF89898E)
private val LbDarkTextMuted = Color(0xFF555555)
private val LbDarkError = Color(0xFFE9501A)
private val LbDarkErrorSurface = Color(0xFF1A1A14)

private val SoftCinemaBorderSubtle = Color(0xFFD7DDD4)
private val SoftCinemaBlue = Color(0xFF287EA8)
private val SoftCinemaError = Color(0xFFC84A20)
private val SoftCinemaErrorSurface = Color(0xFFFFECE5)

val LbBackground: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalAppDarkMode.current) LbDarkBackground else SoftCinemaBackground

val LbSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalAppDarkMode.current) LbDarkSurface else SoftCinemaSurface

val LbSurfaceRaised: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalAppDarkMode.current) LbDarkSurfaceRaised else SoftCinemaSurfaceRaised

val LbBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalAppDarkMode.current) LbDarkBorder else SoftCinemaOutline

val LbBorderSubtle: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalAppDarkMode.current) LbDarkBorderSubtle else SoftCinemaBorderSubtle

val LbGreen: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalAppDarkMode.current) LbDarkGreen else SoftCinemaGreen

val LbOrange: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalAppDarkMode.current) LbDarkOrange else SoftCinemaOrange

val LbBlue: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalAppDarkMode.current) LbDarkBlue else SoftCinemaBlue

val LbTextPrimary: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalAppDarkMode.current) LbDarkTextPrimary else SoftCinemaText

val LbTextSecondary: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalAppDarkMode.current) LbDarkTextSecondary else SoftCinemaTextMuted

val LbTextMuted: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalAppDarkMode.current) LbDarkTextMuted else Color(0xFF7B8578)

val LbError: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalAppDarkMode.current) LbDarkError else SoftCinemaError

val LbErrorSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalAppDarkMode.current) LbDarkErrorSurface else SoftCinemaErrorSurface
