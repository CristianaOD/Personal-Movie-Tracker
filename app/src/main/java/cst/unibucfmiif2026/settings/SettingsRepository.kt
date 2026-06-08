package cst.unibucfmiif2026.settings

import android.content.Context

class SettingsRepository(context: Context) {
    private val sharedPreferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun isDarkModeEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_DARK_MODE, true)
    }

    fun setDarkModeEnabled(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_DARK_MODE, enabled)
            .apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "personal_movie_tracker_settings"
        const val KEY_DARK_MODE = "dark_mode"
    }
}
