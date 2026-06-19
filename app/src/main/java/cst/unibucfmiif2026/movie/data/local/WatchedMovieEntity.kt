package cst.unibucfmiif2026.movie.data.local

import androidx.room.Entity
import cst.unibucfmiif2026.movie.model.Movie

@Entity(tableName = "watched_movies", primaryKeys = ["movieId", "userId"])
data class WatchedMovieEntity(
    val movieId: Int,
    val userId: String,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val voteAverage: Double,
    val voteCount: Int,
    val genresSerialized: String,
    val rating: Int,
    val comment: String,
    val isFavorite: Boolean,
    val watchedAt: Long
)

fun Movie.toWatchedEntity(
    userId: String,
    rating: Int,
    comment: String,
    isFavorite: Boolean = false
): WatchedMovieEntity {
    return WatchedMovieEntity(
        movieId = id,
        userId = userId,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        voteAverage = voteAverage,
        voteCount = voteCount,
        genresSerialized = genres.joinToString("|"),
        rating = rating,
        comment = comment,
        isFavorite = isFavorite,
        watchedAt = System.currentTimeMillis()
    )
}

fun WatchedMovieEntity.toMovie(): Movie {
    return Movie(
        id = movieId,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        voteAverage = voteAverage,
        voteCount = voteCount,
        genres = genresSerialized
            .takeIf { it.isNotBlank() }
            ?.split("\\|".toRegex())
            .orEmpty(),
        isInWatchlist = false
    )
}