package cst.unibucfmiif2026

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cst.unibucfmiif2026.settings.ThemeViewModel
import cst.unibucfmiif2026.ui.navigation.AuthNavigation
import cst.unibucfmiif2026.ui.theme.UniBucFMIIF2026Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeViewModel: ThemeViewModel = viewModel()
            val isDarkModeEnabled by themeViewModel.isDarkModeEnabled.collectAsStateWithLifecycle()

            UniBucFMIIF2026Theme(
                darkTheme = isDarkModeEnabled,
                dynamicColor = false
            ) {
                AuthNavigation(
                    isDarkModeEnabled = isDarkModeEnabled,
                    onDarkModeChange = themeViewModel::setDarkModeEnabled
                )
            }
        }
    }
}
