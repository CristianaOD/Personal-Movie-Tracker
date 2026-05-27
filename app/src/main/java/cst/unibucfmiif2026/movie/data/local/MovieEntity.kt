package cst.unibucfmiif2026.movie.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import cst.unibucfmiif2026.movie.model.Movie

@Entity(tableName = "watchlist_movies")
data class MovieEntity(
    @PrimaryKey val id: Int,
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

fun Movie.toEntity(): MovieEntity {
    return MovieEntity(
        id = id,
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
            ?.split("|")
            .orEmpty(),
        isInWatchlist = true
    )
}
