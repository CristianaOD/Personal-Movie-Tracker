package cst.unibucfmiif2026.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cst.unibucfmiif2026.R
import cst.unibucfmiif2026.ui.pages.LoginPage
import cst.unibucfmiif2026.ui.pages.MovieDetailsPage
import cst.unibucfmiif2026.ui.pages.MoviesHomePage
import cst.unibucfmiif2026.ui.pages.RegisterPage
import cst.unibucfmiif2026.ui.pages.SettingsPage
import cst.unibucfmiif2026.ui.pages.WatchlistPage
import cst.unibucfmiif2026.viewmodel.AuthViewModel
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding

private object AuthRoute {
    const val Login = "login"
    const val Register = "register"
    const val Home = "home"
    const val Watchlist = "watchlist"
    const val Settings = "settings"
    const val MovieDetails = "movie-details"
}

private data class MainDestination(
    val route: String,
    val labelResId: Int,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun AuthNavigation(
    isDarkModeEnabled: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    authViewModel: AuthViewModel = viewModel(),
    onGoogleSignIn: (onSuccess: () -> Unit) -> Unit = {}
) {
    val navController = rememberNavController()
    val authState by authViewModel.authState.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val startDestination = if (authViewModel.isLoggedIn) AuthRoute.Home else AuthRoute.Login

    val mainDestinations = listOf(
        MainDestination(AuthRoute.Home, R.string.nav_home_label, Icons.Outlined.Home),
        MainDestination(AuthRoute.Watchlist, R.string.nav_watchlist_label, Icons.Outlined.BookmarkBorder),
        MainDestination(AuthRoute.Settings, R.string.nav_settings_label, Icons.Outlined.Settings)
    )

    val showBottomBar = mainDestinations.any { destination ->
        currentDestination?.hierarchy?.any { navDestination ->
            navDestination.route == destination.route
        } == true
    }

    val navigateToHome: () -> Unit = {
        navController.navigate(AuthRoute.Home) {
            popUpTo(navController.graph.findStartDestination().id) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    val navigateToLogin: () -> Unit = {
        authViewModel.logout()
        navController.navigate(AuthRoute.Login) {
            popUpTo(navController.graph.findStartDestination().id) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    mainDestinations.forEach { destination ->
                        val selected = currentDestination?.hierarchy?.any { navDestination ->
                            navDestination.route == destination.route
                        } == true

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = destination.icon,
                                    contentDescription = stringResource(destination.labelResId)
                                )
                            },
                            label = {
                                Text(stringResource(destination.labelResId))
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AuthRoute.Login) {
                LoginPage(
                    onRegisterClick = { navController.navigate(AuthRoute.Register) },
                    onLoginClick = authViewModel::login,
                    onLoginSuccess = navigateToHome,
                    onContinueAsGuest = navigateToHome,
                    onGoogleSignIn = { onGoogleSignIn(navigateToHome) },
                    isLoading = authState.isLoading,
                    errorMessage = authState.errorMessage,
                    onErrorDismiss = authViewModel::clearError
                )
            }

            composable(AuthRoute.Register) {
                RegisterPage(
                    onLoginClick = { navController.popBackStack() },
                    onRegisterClick = authViewModel::register,
                    onRegisterSuccess = navigateToHome,
                    onContinueAsGuest = navigateToHome,
                    onGoogleSignIn = { onGoogleSignIn(navigateToHome) },
                    isLoading = authState.isLoading,
                    errorMessage = authState.errorMessage,
                    onErrorDismiss = authViewModel::clearError
                )
            }

            composable(AuthRoute.Home) {
                MoviesHomePage(
                    userEmail = authViewModel.currentUserEmail,
                    onMovieClick = { movie ->
                        navController.navigate("${AuthRoute.MovieDetails}/${movie.id}")
                    }
                )
            }

            composable(AuthRoute.Watchlist) {
                WatchlistPage(
                    onMovieClick = { movie ->
                        navController.navigate("${AuthRoute.MovieDetails}/${movie.id}")
                    }
                )
            }

            composable(AuthRoute.Settings) {
                SettingsPage(
                    userEmail = authViewModel.currentUserEmail,
                    isLoggedIn = authViewModel.isLoggedIn,
                    isDarkModeEnabled = isDarkModeEnabled,
                    onDarkModeChange = onDarkModeChange,
                    onLogout = navigateToLogin
                )
            }

            composable(
                route = "${AuthRoute.MovieDetails}/{movieId}",
                arguments = listOf(
                    navArgument("movieId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->
                val movieId = backStackEntry.arguments?.getInt("movieId") ?: return@composable
                MovieDetailsPage(
                    movieId = movieId,
                    onBackClick = {
                        if (!navController.popBackStack()) {
                            navController.navigate(AuthRoute.Home)
                        }
                    }
                )
            }
        }
    }
}
