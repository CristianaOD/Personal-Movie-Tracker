package cst.unibucfmiif2026.movie.network.dto

import cst.unibucfmiif2026.movie.model.Movie
import com.google.gson.annotations.SerializedName

data class TmdbMovieDto(
    val id: Int,
    val title: String,
    val overview: String,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("vote_count") val voteCount: Int = 0,
    val genres: List<TmdbGenreDto>? = null
)

fun TmdbMovieDto.toMovie(): Movie {
    return Movie(
        id = id,
        title = title,
        overview = overview,
        releaseDate = releaseDate,
        posterPath = posterPath,
        backdropPath = backdropPath,
        voteAverage = voteAverage,
        voteCount = voteCount,
        genres = genres.orEmpty().map { genre -> genre.name }
    )
}
