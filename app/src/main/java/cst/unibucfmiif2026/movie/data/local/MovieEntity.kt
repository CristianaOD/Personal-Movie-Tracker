package cst.unibucfmiif2026.movie.data.local

import androidx.room.Entity
import cst.unibucfmiif2026.movie.model.Movie

@Entity(tableName = "watchlist_movies", primaryKeys = ["id", "userId"])
data class MovieEntity(
    val id: Int,
    val userId: String,
    val title: String,
    val overview: String,
    val releaseDate: String?,
    val posterPath: String?,
    val backdropPath: String?,
    val voteAverage: Double,
    val voteCount: Int,
    val genresSerialized: String,
    val addedAt: Long
)

fun Movie.toEntity(userId: String): MovieEntity {
    return MovieEntity(
        id = id,
        userId = userId,
        title = title,
        overview = overview,
        releaseDate = releaseDate,
        posterPath = posterPath,
        backdropPath = backdropPath,
        voteAverage = voteAverage,
        voteCount = voteCount,
        genresSerialized = genres.joinToString(separator = "|"),
        addedAt = System.currentTimeMillis()
    )
}

fun MovieEntity.toMovie(): Movie {
    return Movie(
        id = id,
        title = title,
        overview = overview,
        releaseDate = releaseDate,
        posterPath = posterPath,
        backdropPath = backdropPath,
        voteAverage = voteAverage,
        voteCount = voteCount,
        genres = genresSerialized
            .takeIf { value -> value.isNotBlank() }
            ?.split("\\|".toRegex())
            .orEmpty(),
        isInWatchlist = true
    )
}