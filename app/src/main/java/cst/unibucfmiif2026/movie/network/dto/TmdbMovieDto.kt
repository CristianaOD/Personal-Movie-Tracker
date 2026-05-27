package cst.unibucfmiif2026.movie.network.dto

import cst.unibucfmiif2026.movie.model.Movie
import com.google.gson.annotations.SerializedName

private val genreNamesById = mapOf(
    28 to "Action",
    12 to "Adventure",
    16 to "Animation",
    35 to "Comedy",
    80 to "Crime",
    99 to "Documentary",
    18 to "Drama",
    10751 to "Family",
    14 to "Fantasy",
    36 to "History",
    27 to "Horror",
    10402 to "Music",
    9648 to "Mystery",
    10749 to "Romance",
    878 to "Science Fiction",
    10770 to "TV Movie",
    53 to "Thriller",
    10752 to "War",
    37 to "Western"
)

data class TmdbMovieDto(
    val id: Int,
    val title: String,
    val overview: String,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("vote_count") val voteCount: Int = 0,
    @SerializedName("genre_ids") val genreIds: List<Int>? = null,
    val genres: List<TmdbGenreDto>? = null
)

fun TmdbMovieDto.toMovie(): Movie {
    val mappedGenres = when {
        !genres.isNullOrEmpty() -> genres.map { genre -> genre.name }
        !genreIds.isNullOrEmpty() -> genreIds.mapNotNull { genreId -> genreNamesById[genreId] }
        else -> emptyList()
    }

    return Movie(
        id = id,
        title = title,
        overview = overview,
        releaseDate = releaseDate,
        posterPath = posterPath,
        backdropPath = backdropPath,
        voteAverage = voteAverage,
        voteCount = voteCount,
        genres = mappedGenres
    )
}
