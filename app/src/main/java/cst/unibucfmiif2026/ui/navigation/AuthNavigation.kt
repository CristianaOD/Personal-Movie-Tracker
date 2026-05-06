package cst.unibucfmiif2026.ui.navigation

import android.app.Activity
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cst.unibucfmiif2026.MainActivity
import cst.unibucfmiif2026.ui.pages.AddressDetailsPage
import cst.unibucfmiif2026.ui.pages.AddressesPage
import cst.unibucfmiif2026.ui.pages.HomePage
import cst.unibucfmiif2026.ui.pages.LoginPage
import cst.unibucfmiif2026.ui.pages.RegisterPage
import cst.unibucfmiif2026.viewmodel.AuthViewModel

@Composable
fun AuthNavigation(authViewModel: AuthViewModel = viewModel()) {
    val navController = rememberNavController()
    val authState by authViewModel.authState.collectAsState()
    val navigateToHome: () -> Unit = { navController.navigate("homepage") }
    val startDestination = when (authViewModel.isLoggedIn) {
        true -> "homepage"
        false -> "login"
    }
    val context = LocalContext.current

    NavHost(navController, startDestination = startDestination) {
        composable("login") {
            LoginPage(
                onRegisterClick = {
                    navController.navigate("register")
                },
                onLoginClick = authViewModel::login,
                onLoginSuccess = navigateToHome,
                isLoading = authState.isLoading,
                errorMessage = authState.errorMessage
            )
        }

        composable("register") {
            RegisterPage(
                onLoginClick = {
                    navController.popBackStack()
                },
                onRegisterClick = authViewModel::register,
                onRegisterSuccess = navigateToHome,
                isLoading = authState.isLoading,
                errorMessage = authState.errorMessage
            )
        }

        composable("addresses_page") {
            AddressesPage {addressId ->
                navController.navigate("address_details_page/$addressId")
            }
        }

        composable(
            route = "address_details_page/{address_id}",
            arguments = listOf(navArgument("address_id") {
                type = NavType.LongType
            })
        ) {
            val addressId = it.arguments?.getLong("address_id") ?: return@composable
            AddressDetailsPage(id = addressId)
        }

        composable("homepage") {
            HomePage(
                onLogout = {
                    authViewModel.logout()
                    (context as? Activity)?.apply {
                        val intent = Intent(this, MainActivity::class.java)
                        this.startActivity(intent)
                        this.finish()
                    }
                },
                gotoAddresses = { navController.navigate("addresses_page") }
            )
        }
    }
}