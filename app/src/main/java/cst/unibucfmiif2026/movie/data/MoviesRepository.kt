package cst.unibucfmiif2026.movie.data

import cst.unibucfmiif2026.BuildConfig
import cst.unibucfmiif2026.movie.data.local.MovieDao
import cst.unibucfmiif2026.movie.data.local.WatchedMovieDao
import cst.unibucfmiif2026.movie.data.local.WatchedMovieEntity
import cst.unibucfmiif2026.movie.data.local.toEntity
import cst.unibucfmiif2026.movie.data.local.toMovie
import cst.unibucfmiif2026.movie.data.local.toWatchedEntity
import cst.unibucfmiif2026.movie.model.Movie
import cst.unibucfmiif2026.movie.network.TmdbRetrofitClient
import cst.unibucfmiif2026.movie.network.dto.toMovie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException

data class MoviesResult(
    val movies: List<Movie>,
    val infoMessage: String? = null
)

data class MovieDetailsResult(
    val movie: Movie?,
    val infoMessage: String? = null
)

class MoviesRepository(
    private val movieDao: MovieDao,
    private val watchedMovieDao: WatchedMovieDao
) {
    fun observeWatchlistMovies(): Flow<List<Movie>> {
        return movieDao.observeWatchlistMovies()
            .map { entities -> entities.map { entity -> entity.toMovie() } }
    }

    fun observeWatchlistIds(): Flow<Set<Int>> {
        return movieDao.observeWatchlistIds()
            .map { ids -> ids.toSet() }
    }

    suspend fun getTrendingMovies(): MoviesResult {
        val apiKey = BuildConfig.TMDB_API_KEY

        if (apiKey.isBlank()) {
            return MoviesResult(
                movies = previewMovies,
                infoMessage = "TMDB API key is not configured yet. Showing preview data."
            )
        }

        return runCatching {
            val response = TmdbRetrofitClient.api.getTrendingMovies(apiKey = apiKey)
            MoviesResult(
                movies = response.results.orEmpty().map { movie -> movie.toMovie() }
            )
        }.getOrElse { error ->
            MoviesResult(
                movies = previewMovies,
                infoMessage = "TMDB request failed: ${error.toReadableTmdbMessage()}. Showing preview data instead."
            )
        }
    }

    suspend fun searchMovies(query: String): MoviesResult {
        val apiKey = BuildConfig.TMDB_API_KEY

        if (apiKey.isBlank()) {
            val filteredPreviewMovies = previewMovies.filter { movie ->
                movie.title.contains(query, ignoreCase = true)
            }

            return MoviesResult(
                movies = filteredPreviewMovies,
                infoMessage = "TMDB API key is not configured yet. Showing preview search results."
            )
        }

        return runCatching {
            val response = TmdbRetrofitClient.api.searchMovies(
                apiKey = apiKey,
                query = query
            )
            MoviesResult(
                movies = response.results.orEmpty().map { movie -> movie.toMovie() }
            )
        }.getOrElse { error ->
            MoviesResult(
                movies = previewMovies.filter { movie ->
                    movie.title.contains(query, ignoreCase = true)
                },
                infoMessage = "TMDB search failed: ${error.toReadableTmdbMessage()}. Showing preview search results instead."
            )
        }
    }

    suspend fun getMovieDetails(movieId: Int): MovieDetailsResult {
        val apiKey = BuildConfig.TMDB_API_KEY
        val watchlistMovie = movieDao.getById(movieId)?.toMovie()
        val previewMovie = previewMovies.firstOrNull { movie -> movie.id == movieId }
        val fallbackMovie = watchlistMovie ?: previewMovie

        if (apiKey.isBlank()) {
            return MovieDetailsResult(
                movie = fallbackMovie,
                infoMessage = "TMDB API key is not configured yet. Showing preview details."
            )
        }

        return runCatching {
            val response = TmdbRetrofitClient.api.getMovieDetails(
                movieId = movieId,
                apiKey = apiKey
            )
            MovieDetailsResult(movie = response.toMovie())
        }.getOrElse { error ->
            MovieDetailsResult(
                movie = fallbackMovie,
                infoMessage = "TMDB details request failed: ${error.toReadableTmdbMessage()}. Showing preview details instead."
            )
        }
    }

    suspend fun addToWatchlist(movie: Movie) {
        movieDao.insert(movie.copy(isInWatchlist = true).toEntity())
    }

    suspend fun removeFromWatchlist(movieId: Int) {
        movieDao.deleteById(movieId)
    }

    suspend fun toggleWatchlist(movie: Movie) {
        if (movie.isInWatchlist) {
            removeFromWatchlist(movie.id)
        } else {
            addToWatchlist(movie)
        }
    }

    // functii pt watched movies
    fun observeWatchedMovies(): Flow<List<WatchedMovieEntity>> {
        return watchedMovieDao.observeAll()
    }

    fun observeWatchedMoviesSortedByRating(): Flow<List<WatchedMovieEntity>> {
        return watchedMovieDao.observeAllSortedByRating()
    }

    fun observeFavorites(): Flow<List<WatchedMovieEntity>> {
        return watchedMovieDao.observeFavorites()
    }

    fun observeWatchedIds(): Flow<Set<Int>> {
        return watchedMovieDao.observeWatchedIds()
            .map { it.toSet() }
    }

    suspend fun getWatchedMovie(movieId: Int): WatchedMovieEntity? {
        return watchedMovieDao.getById(movieId)
    }

    suspend fun saveReview(movie: Movie, rating: Int, comment: String) {
        val existing = watchedMovieDao.getById(movie.id)
        if (existing != null) {
            watchedMovieDao.updateReview(movie.id, rating, comment)
        } else {
            watchedMovieDao.insert(
                movie.toWatchedEntity(rating = rating, comment = comment)
            )
        }
        movieDao.deleteById(movie.id)
    }

    suspend fun toggleFavorite(movieId: Int) {
        val existing = watchedMovieDao.getById(movieId) ?: return
        watchedMovieDao.updateFavorite(movieId, !existing.isFavorite)
    }

    suspend fun deleteWatchedMovie(movieId: Int) {
        watchedMovieDao.deleteById(movieId)
    }

    private fun Throwable.toReadableTmdbMessage(): String {
        return when (this) {
            is HttpException -> {
                val responseBody = response()?.errorBody()?.string()?.take(180).orEmpty()
                buildString {
                    append("HTTP ")
                    append(code())
                    if (responseBody.isNotBlank()) {
                        append(" - ")
                        append(responseBody)
                    }
                }
            }

            is IOException -> "Network error (${localizedMessage ?: "check internet connection"})"
            else -> localizedMessage ?: this::class.java.simpleName
        }
    }
}
