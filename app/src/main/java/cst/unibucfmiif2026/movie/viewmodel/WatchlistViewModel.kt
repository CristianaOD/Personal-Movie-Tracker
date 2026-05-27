package cst.unibucfmiif2026.movie.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cst.unibucfmiif2026.PersonalMovieTrackerApp
import cst.unibucfmiif2026.movie.data.MoviesRepository
import cst.unibucfmiif2026.movie.model.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WatchlistUiState(
    val isLoading: Boolean = true,
    val movies: List<Movie> = emptyList()
)

class WatchlistViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MoviesRepository =
        (application as PersonalMovieTrackerApp).moviesRepository

    private val _uiState = MutableStateFlow(WatchlistUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeWatchlistMovies().collect { movies ->
                _uiState.value = WatchlistUiState(
                    isLoading = false,
                    movies = movies
                )
            }
        }
    }

    fun removeFromWatchlist(movieId: Int) {
        viewModelScope.launch {
            repository.removeFromWatchlist(movieId)
        }
    }
}
