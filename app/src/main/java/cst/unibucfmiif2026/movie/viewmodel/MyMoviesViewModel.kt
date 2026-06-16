package cst.unibucfmiif2026.movie.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cst.unibucfmiif2026.PersonalMovieTrackerApp
import cst.unibucfmiif2026.movie.data.local.WatchedMovieEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class MyMoviesTab { REVIEWED, FAVORITES }
enum class SortOrder { BY_DATE, BY_RATING }

data class MyMoviesUiState(
    val tab: MyMoviesTab = MyMoviesTab.REVIEWED,
    val sortOrder: SortOrder = SortOrder.BY_DATE,
    val reviewed: List<WatchedMovieEntity> = emptyList(),
    val favorites: List<WatchedMovieEntity> = emptyList(),
    val isLoading: Boolean = true
)

class MyMoviesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = (application as PersonalMovieTrackerApp).moviesRepository

    private val _uiState = MutableStateFlow(MyMoviesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeReviewed()
        observeFavorites()
    }

    private fun observeReviewed() {
        viewModelScope.launch {
            repository.observeWatchedMovies().collect { movies ->
                _uiState.update { state ->
                    state.copy(
                        reviewed = applySortOrder(movies, state.sortOrder),
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            repository.observeFavorites().collect { movies ->
                _uiState.update { state ->
                    state.copy(favorites = movies)
                }
            }
        }
    }

    fun setTab(tab: MyMoviesTab) {
        _uiState.update { it.copy(tab = tab) }
    }

    fun setSortOrder(sortOrder: SortOrder) {
        _uiState.update { state ->
            state.copy(
                sortOrder = sortOrder,
                reviewed = applySortOrder(state.reviewed, sortOrder)
            )
        }
    }

    fun toggleFavorite(movieId: Int) {
        viewModelScope.launch {
            repository.toggleFavorite(movieId)
        }
    }

    fun deleteReview(movieId: Int) {
        viewModelScope.launch {
            repository.deleteWatchedMovie(movieId)
        }
    }

    private fun applySortOrder(
        movies: List<WatchedMovieEntity>,
        sortOrder: SortOrder
    ): List<WatchedMovieEntity> {
        return when (sortOrder) {
            SortOrder.BY_DATE -> movies.sortedByDescending { it.watchedAt }
            SortOrder.BY_RATING -> movies.sortedByDescending { it.rating }
        }
    }
}