package cst.unibucfmiif2026

import android.app.Application
import cst.unibucfmiif2026.movie.data.MoviesRepository
import cst.unibucfmiif2026.movie.data.local.AppDatabase
import cst.unibucfmiif2026.settings.SettingsRepository

class PersonalMovieTrackerApp : Application() {
    val appDatabase: AppDatabase by lazy {
        AppDatabase.getInstance(this)
    }

    val moviesRepository: MoviesRepository by lazy {
        MoviesRepository(
            appDatabase.movieDao(),
            watchedMovieDao = appDatabase.watchedMovieDao()
        )
    }

    val settingsRepository: SettingsRepository by lazy {
        SettingsRepository(this)
    }
}
