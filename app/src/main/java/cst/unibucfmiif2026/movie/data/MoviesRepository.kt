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
import kotlinx.coroutines.flow.flatMapLatest
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
    private val watchedMovieDao: WatchedMovieDao,
    private val currentUserId: () -> String
) {
    private val userId: String
        get() = currentUserId()

    fun observeWatchlistMovies(): Flow<List<Movie>> {
        return movieDao.observeWatchlistMovies(userId)
            .map { entities -> entities.map { entity -> entity.toMovie() } }
    }

    fun observeWatchlistIds(): Flow<Set<Int>> {
        return movieDao.observeWatchlistIds(userId)
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
            // Search dupa titlu
            val movieResults = TmdbRetrofitClient.api.searchMovies(
                apiKey = apiKey,
                query = query
            ).results.orEmpty().map { it.toMovie() }

            // Search dupa persoana (actor/regizor)
            val personMovies = runCatching {
                val personResponse = TmdbRetrofitClient.api.searchPerson(
                    apiKey = apiKey,
                    query = query
                )
                val firstPerson = personResponse.results.orEmpty().firstOrNull()

                if (firstPerson != null) {
                    val credits = TmdbRetrofitClient.api.getPersonMovieCredits(
                        personId = firstPerson.id,
                        apiKey = apiKey
                    )
                    (credits.cast.orEmpty() + credits.crew.orEmpty())
                        .map { it.toMovie() }
                } else {
                    emptyList()
                }
            }.getOrDefault(emptyList())

            // Combin si elimin duplicate dupa id
            val combined = (movieResults + personMovies)
                .distinctBy { it.id }
                .sortedByDescending { it.voteAverage }

            MoviesResult(movies = combined)
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
        val watchlistMovie = movieDao.getById(movieId, userId)?.toMovie()
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
        movieDao.insert(movie.copy(isInWatchlist = true).toEntity(userId))
    }

    suspend fun discoverMoviesByGenre(genreId: Int, genreName: String): MoviesResult {
        val apiKey = BuildConfig.TMDB_API_KEY

        if (apiKey.isBlank()) {
            return MoviesResult(
                movies = previewMovies.filter { movie -> movie.genres.contains(genreName) },
                infoMessage = "TMDB API key is not configured. Showing preview $genreName movies."
            )
        }

        return runCatching {
            val response = TmdbRetrofitClient.api.discoverMoviesByGenre(
                apiKey = apiKey,
                genreId = genreId
            )
            MoviesResult(movies = response.results.orEmpty().map { movie -> movie.toMovie() })
        }.getOrElse { error ->
            MoviesResult(
                movies = previewMovies.filter { movie -> movie.genres.contains(genreName) },
                infoMessage = "Genre request failed: ${error.toReadableTmdbMessage()}. Showing preview data."
            )
        }
    }

    suspend fun removeFromWatchlist(movieId: Int) {
        movieDao.deleteById(movieId, userId)
    }

    suspend fun toggleWatchlist(movie: Movie) {
        if (movie.isInWatchlist) {
            removeFromWatchlist(movie.id)
        } else {
            addToWatchlist(movie)
        }
    }

    fun observeWatchedMovies(): Flow<List<WatchedMovieEntity>> {
        return watchedMovieDao.observeAll(userId)
    }

    fun observeWatchedMoviesSortedByRating(): Flow<List<WatchedMovieEntity>> {
        return watchedMovieDao.observeAllSortedByRating(userId)
    }

    fun observeFavorites(): Flow<List<WatchedMovieEntity>> {
        return watchedMovieDao.observeFavorites(userId)
    }

    fun observeWatchedIds(): Flow<Set<Int>> {
        return watchedMovieDao.observeWatchedIds(userId)
            .map { it.toSet() }
    }

    suspend fun getWatchedMovie(movieId: Int): WatchedMovieEntity? {
        return watchedMovieDao.getById(movieId, userId)
    }

    suspend fun saveReview(movie: Movie, rating: Int, comment: String) {
        val existing = watchedMovieDao.getById(movie.id, userId)
        if (existing != null) {
            watchedMovieDao.updateReview(movie.id, userId, rating, comment)
        } else {
            watchedMovieDao.insert(
                movie.toWatchedEntity(userId = userId, rating = rating, comment = comment)
            )
        }
        movieDao.deleteById(movie.id, userId)
    }

    suspend fun toggleFavorite(movieId: Int) {
        val existing = watchedMovieDao.getById(movieId, userId) ?: return
        watchedMovieDao.updateFavorite(movieId, userId, !existing.isFavorite)
    }

    suspend fun deleteWatchedMovie(movieId: Int) {
        watchedMovieDao.deleteById(movieId, userId)
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
