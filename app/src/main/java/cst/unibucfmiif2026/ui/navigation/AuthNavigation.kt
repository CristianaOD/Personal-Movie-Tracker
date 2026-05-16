package cst.unibucfmiif2026.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cst.unibucfmiif2026.ui.pages.LoginPage
import cst.unibucfmiif2026.ui.pages.MovieDetailsPage
import cst.unibucfmiif2026.ui.pages.MoviesHomePage
import cst.unibucfmiif2026.ui.pages.RegisterPage
import cst.unibucfmiif2026.viewmodel.AuthViewModel

private object AuthRoute {
    const val Login = "login"
    const val Register = "register"
    const val Home = "home"
    const val MovieDetails = "movie-details"
}

@Composable
fun AuthNavigation(authViewModel: AuthViewModel = viewModel()) {
    val navController = rememberNavController()
    val authState by authViewModel.authState.collectAsState()
    val startDestination = if (authViewModel.isLoggedIn) AuthRoute.Home else AuthRoute.Login

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

    NavHost(navController = navController, startDestination = startDestination) {
        composable(AuthRoute.Login) {
            LoginPage(
                onRegisterClick = { navController.navigate(AuthRoute.Register) },
                onLoginClick = authViewModel::login,
                onLoginSuccess = navigateToHome,
                onContinueAsGuest = navigateToHome,
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
                },
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
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
