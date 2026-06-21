package cst.unibucfmiif2026.movie.network.dto

data class TmdbPersonSearchResponseDto(
    val results: List<TmdbPersonDto>?
)

data class TmdbPersonDto(
    val id: Int,
    val name: String,
    val known_for_department: String?
)

data class TmdbPersonMovieCreditsDto(
    val cast: List<TmdbMovieDto>?,
    val crew: List<TmdbMovieDto>?
)