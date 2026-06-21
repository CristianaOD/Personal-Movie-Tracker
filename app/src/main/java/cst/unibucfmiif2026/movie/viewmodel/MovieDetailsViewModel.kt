package cst.unibucfmiif2026.movie.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cst.unibucfmiif2026.PersonalMovieTrackerApp
import cst.unibucfmiif2026.movie.data.MoviesRepository
import cst.unibucfmiif2026.movie.model.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MovieDetailsUiState(
    val isLoading: Boolean = true,
    val movie: Movie? = null,
    val infoMessage: String? = null
)

class MovieDetailsViewModel(
    application: Application,
    private val movieId: Int
) : AndroidViewModel(application) {
    private val repository: MoviesRepository =
        (application as PersonalMovieTrackerApp).moviesRepository

    private val _uiState = MutableStateFlow(MovieDetailsUiState())
    val uiState = _uiState.asStateFlow()

    private val _watchedMovie = MutableStateFlow<cst.unibucfmiif2026.movie.data.local.WatchedMovieEntity?>(null)
    val watchedMovie = _watchedMovie.asStateFlow()

    init {
        observeWatchlistIds()
        observeWatchedMovie()
        loadMovieDetails()
    }

    fun loadMovieDetails() {
        viewModelScope.launch {
            _uiState.update { state -> state.copy(isLoading = true) }
            val result = repository.getMovieDetails(movieId)
            val watchlistIds = repository.observeWatchlistIds().first()

            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    movie = result.movie?.copy(
                        isInWatchlist = watchlistIds.contains(movieId)
                    ),
                    infoMessage = result.infoMessage
                )
            }
        }
    }

    fun toggleWatchlist() {
        val movie = uiState.value.movie ?: return
        viewModelScope.launch {
            repository.toggleWatchlist(movie)
        }
    }

    private fun observeWatchlistIds() {
        viewModelScope.launch {
            repository.observeWatchlistIds().collect { ids ->
                _uiState.update { state ->
                    val currentMovie = state.movie ?: return@update state
                    state.copy(
                        movie = currentMovie.copy(
                            isInWatchlist = ids.contains(movieId)
                        )
                    )
                }
            }
        }
    }

    private fun observeWatchedMovie() {
        viewModelScope.launch {
            repository.observeWatchedIds().collect { ids ->
                if (ids.contains(movieId)) {
                    _watchedMovie.value = repository.getWatchedMovie(movieId)
                } else {
                    _watchedMovie.value = null
                }
            }
        }
    }

    fun saveReview(rating: Int, comment: String) {
        val movie = uiState.value.movie ?: return
        viewModelScope.launch {
            repository.saveReview(movie, rating, comment)
            _watchedMovie.value = repository.getWatchedMovie(movieId)
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            repository.toggleFavorite(movieId)
            _watchedMovie.value = repository.getWatchedMovie(movieId)
        }
    }

    fun deleteReview() {
        viewModelScope.launch {
            repository.deleteWatchedMovie(movieId)
            _watchedMovie.value = null
        }
    }

    companion object {
        fun factory(
            application: Application,
            movieId: Int
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MovieDetailsViewModel(
                        application = application,
                        movieId = movieId
                    ) as T
                }
            }
        }
    }
}
