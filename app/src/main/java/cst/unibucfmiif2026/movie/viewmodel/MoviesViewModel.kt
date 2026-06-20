package cst.unibucfmiif2026.movie.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cst.unibucfmiif2026.PersonalMovieTrackerApp
import cst.unibucfmiif2026.movie.data.MoviesRepository
import cst.unibucfmiif2026.movie.model.Movie
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class MovieGenreFilter(val genreId: Int?, val label: String) {
    ALL(null, "All"),
    ACTION(28, "Action"),
    COMEDY(35, "Comedy"),
    DRAMA(18, "Drama"),
    HORROR(27, "Horror"),
    ROMANCE(10749, "Romance"),
    THRILLER(53, "Thriller")
}

data class MoviesUiState(
    val isLoading: Boolean = true,
    val movies: List<Movie> = emptyList(),
    val infoMessage: String? = null,
    val searchQuery: String = "",
    val selectedGenre: MovieGenreFilter = MovieGenreFilter.ALL,
    val isShowingSearchResults: Boolean = false,
    val watchlistIds: Set<Int> = emptySet()
)

class MoviesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MoviesRepository =
        (application as PersonalMovieTrackerApp).moviesRepository

    private val _uiState = MutableStateFlow(MoviesUiState())
    val uiState = _uiState.asStateFlow()
    private var searchJob: Job? = null

    init {
        observeWatchlistIds()
        loadTrendingMovies()
    }

    fun onSearchQueryChange(query: String) {
        searchJob?.cancel()
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                selectedGenre = if (query.isNotBlank()) MovieGenreFilter.ALL else state.selectedGenre,
                infoMessage = null
            )
        }

        val trimmedQuery = query.trim()
        when {
            trimmedQuery.isBlank() -> loadTrendingMovies()
            trimmedQuery.length >= MIN_SEARCH_LENGTH -> {
                searchJob = viewModelScope.launch {
                    delay(SEARCH_DEBOUNCE_MS)
                    searchMovies(trimmedQuery)
                }
            }
        }
    }

    fun selectGenre(genre: MovieGenreFilter) {
        searchJob?.cancel()
        _uiState.update { state ->
            state.copy(searchQuery = "", selectedGenre = genre, infoMessage = null)
        }
        if (genre == MovieGenreFilter.ALL) loadTrendingMovies() else loadGenre(genre)
    }

    fun loadTrendingMovies() {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    isLoading = true,
                    selectedGenre = MovieGenreFilter.ALL,
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

    private suspend fun searchMovies(query: String) {
        _uiState.update { state -> state.copy(isLoading = true, infoMessage = null) }
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

    fun clearSearch() {
        searchJob?.cancel()
        _uiState.update { state ->
            state.copy(searchQuery = "", selectedGenre = MovieGenreFilter.ALL)
        }
        loadTrendingMovies()
    }

    private fun loadGenre(genre: MovieGenreFilter) {
        val genreId = genre.genreId ?: return
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(isLoading = true, isShowingSearchResults = false, infoMessage = null)
            }
            val result = repository.discoverMoviesByGenre(genreId, genre.label)
            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    movies = applyWatchlistState(result.movies, state.watchlistIds),
                    infoMessage = result.infoMessage
                )
            }
        }
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

    private companion object {
        const val MIN_SEARCH_LENGTH = 2
        const val SEARCH_DEBOUNCE_MS = 500L
    }
}
