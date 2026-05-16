package cst.unibucfmiif2026.movie.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cst.unibucfmiif2026.movie.data.MoviesRepository
import cst.unibucfmiif2026.movie.model.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MoviesUiState(
    val isLoading: Boolean = true,
    val movies: List<Movie> = emptyList(),
    val infoMessage: String? = null
)

class MoviesViewModel(
    private val repository: MoviesRepository = MoviesRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(MoviesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadTrendingMovies()
    }

    fun loadTrendingMovies() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = repository.getTrendingMovies()
            _uiState.value = MoviesUiState(
                isLoading = false,
                movies = result.movies,
                infoMessage = result.infoMessage
            )
        }
    }
}
