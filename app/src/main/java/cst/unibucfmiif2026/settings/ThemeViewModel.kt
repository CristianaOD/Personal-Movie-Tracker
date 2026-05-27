package cst.unibucfmiif2026.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import cst.unibucfmiif2026.PersonalMovieTrackerApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ThemeViewModel(application: Application) : AndroidViewModel(application) {
    private val settingsRepository =
        (application as PersonalMovieTrackerApp).settingsRepository

    private val _isDarkModeEnabled =
        MutableStateFlow(settingsRepository.isDarkModeEnabled())
    val isDarkModeEnabled = _isDarkModeEnabled.asStateFlow()

    fun setDarkModeEnabled(enabled: Boolean) {
        settingsRepository.setDarkModeEnabled(enabled)
        _isDarkModeEnabled.value = enabled
    }
}
