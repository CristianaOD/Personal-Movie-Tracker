package cst.unibucfmiif2026.movie.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cst.unibucfmiif2026.PersonalMovieTrackerApp
import cst.unibucfmiif2026.movie.data.MoviesRepository
import cst.unibucfmiif2026.movie.model.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MoviesUiState(
    val isLoading: Boolean = true,
    val movies: List<Movie> = emptyList(),
    val infoMessage: String? = null,
    val searchQuery: String = "",
    val isShowingSearchResults: Boolean = false,
    val watchlistIds: Set<Int> = emptySet()
)

class MoviesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MoviesRepository =
        (application as PersonalMovieTrackerApp).moviesRepository

    private val _uiState = MutableStateFlow(MoviesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeWatchlistIds()
        loadTrendingMovies()
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { state -> state.copy(searchQuery = query) }
    }

    fun loadTrendingMovies() {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    isLoading = true,
                    isShowingSearchResults = false,
                    infoMessage = null
                )
            }

            val result = repository.getTrendingMovies()
            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    movies = applyWatchlistState(result.movies, state.watchlistIds),
                    infoMessage = result.infoMessage,
                    isShowingSearchResults = false
                )
            }
        }
    }

    fun searchMovies() {
        val query = uiState.value.searchQuery.trim()
        if (query.isBlank()) {
            loadTrendingMovies()
            return
        }

        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    isLoading = true,
                    infoMessage = null
                )
            }

            val result = repository.searchMovies(query)
            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    movies = applyWatchlistState(result.movies, state.watchlistIds),
                    infoMessage = result.infoMessage,
                    isShowingSearchResults = true
                )
            }
        }
    }

    fun clearSearch() {
        _uiState.update { state -> state.copy(searchQuery = "") }
        loadTrendingMovies()
    }

    fun toggleWatchlist(movie: Movie) {
        viewModelScope.launch {
            repository.toggleWatchlist(movie)
        }
    }

    private fun observeWatchlistIds() {
        viewModelScope.launch {
            repository.observeWatchlistIds().collect { ids ->
                _uiState.update { state ->
                    state.copy(
                        watchlistIds = ids,
                        movies = applyWatchlistState(state.movies, ids)
                    )
                }
            }
        }
    }

    private fun applyWatchlistState(
        movies: List<Movie>,
        watchlistIds: Set<Int>
    ): List<Movie> {
        return movies.map { movie ->
            movie.copy(isInWatchlist = watchlistIds.contains(movie.id))
        }
    }
}
