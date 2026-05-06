package cst.unibucfmiif2026

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cst.unibucfmiif2026.ui.navigation.AuthNavigation
import cst.unibucfmiif2026.ui.theme.UniBucFMIIF2026Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UniBucFMIIF2026Theme {
                AuthNavigation()
            }
        }
    }
}
