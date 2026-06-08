package cst.unibucfmiif2026

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import cst.unibucfmiif2026.settings.ThemeViewModel
import cst.unibucfmiif2026.ui.navigation.AuthNavigation
import cst.unibucfmiif2026.ui.theme.UniBucFMIIF2026Theme
import cst.unibucfmiif2026.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var authViewModel: AuthViewModel
    private var onGoogleSignInSuccess: (() -> Unit)? = null

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        authViewModel.handleGoogleSignInResult(
            data = result.data,
            onSuccess = { onGoogleSignInSuccess?.invoke() }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            authViewModel = viewModel()
            val themeViewModel: ThemeViewModel = viewModel()
            val isDarkModeEnabled by themeViewModel.isDarkModeEnabled.collectAsStateWithLifecycle()

            UniBucFMIIF2026Theme(
                darkTheme = isDarkModeEnabled,
                dynamicColor = false
            ) {
                AuthNavigation(
                    isDarkModeEnabled = isDarkModeEnabled,
                    onDarkModeChange = themeViewModel::setDarkModeEnabled,
                    authViewModel = authViewModel,
                    onGoogleSignIn = { onSuccess ->
                        onGoogleSignInSuccess = onSuccess
                        val intent = authViewModel.getGoogleSignInIntent(googleSignInClient)
                        googleSignInLauncher.launch(intent)
                    }
                )
            }
        }
    }
}