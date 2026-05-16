package cst.unibucfmiif2026.movie.model

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val releaseDate: String? = null,
    val posterPath: String? = null,
    val backdropPath: String? = null,
    val voteAverage: Double = 0.0,
    val voteCount: Int = 0,
    val genres: List<String> = emptyList(),
    val isInWatchlist: Boolean = false
)
