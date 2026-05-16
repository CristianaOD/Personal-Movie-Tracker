package cst.unibucfmiif2026.movie.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cst.unibucfmiif2026.movie.data.MoviesRepository
import cst.unibucfmiif2026.movie.model.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MovieDetailsUiState(
    val isLoading: Boolean = true,
    val movie: Movie? = null,
    val infoMessage: String? = null
)

class MovieDetailsViewModel(
    private val movieId: Int,
    private val repository: MoviesRepository = MoviesRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(MovieDetailsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadMovieDetails()
    }

    fun loadMovieDetails() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = repository.getMovieDetails(movieId)
            _uiState.value = MovieDetailsUiState(
                isLoading = false,
                movie = result.movie,
                infoMessage = result.infoMessage
            )
        }
    }

    companion object {
        fun factory(movieId: Int): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MovieDetailsViewModel(movieId = movieId) as T
                }
            }
        }
    }
}
