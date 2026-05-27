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

    init {
        observeWatchlistIds()
        loadMovieDetails()
    }

    fun loadMovieDetails() {
        viewModelScope.launch {
            _uiState.update { state -> state.copy(isLoading = true) }
            val result = repository.getMovieDetails(movieId)
            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    movie = result.movie?.copy(
                        isInWatchlist = state.movie?.isInWatchlist
                            ?: result.movie.isInWatchlist
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
                    state.copy(
                        movie = state.movie?.copy(
                            isInWatchlist = ids.contains(movieId)
                        )
                    )
                }
            }
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
