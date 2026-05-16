package cst.unibucfmiif2026.movie.network.dto

data class TmdbTrendingMoviesResponseDto(
    val page: Int,
    val results: List<TmdbMovieDto>? = null
)
