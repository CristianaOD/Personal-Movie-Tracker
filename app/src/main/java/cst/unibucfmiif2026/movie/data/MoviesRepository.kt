package cst.unibucfmiif2026.movie.data

import cst.unibucfmiif2026.BuildConfig
import cst.unibucfmiif2026.movie.model.Movie
import cst.unibucfmiif2026.movie.network.TmdbRetrofitClient
import cst.unibucfmiif2026.movie.network.dto.toMovie
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

class MoviesRepository {
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

    suspend fun getMovieDetails(movieId: Int): MovieDetailsResult {
        val apiKey = BuildConfig.TMDB_API_KEY
        val previewMovie = previewMovies.firstOrNull { movie -> movie.id == movieId }

        if (apiKey.isBlank()) {
            return MovieDetailsResult(
                movie = previewMovie,
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
                movie = previewMovie,
                infoMessage = "TMDB details request failed: ${error.toReadableTmdbMessage()}. Showing preview details instead."
            )
        }
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
