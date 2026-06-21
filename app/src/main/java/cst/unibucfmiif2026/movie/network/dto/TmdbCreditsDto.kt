package cst.unibucfmiif2026.movie.network.dto

data class TmdbCreditsDto(
    val cast: List<TmdbCastMemberDto>?,
    val crew: List<TmdbCrewMemberDto>?
)

data class TmdbCastMemberDto(
    val id: Int,
    val name: String,
    val character: String?,
    val profile_path: String?,
    val order: Int
)

data class TmdbCrewMemberDto(
    val id: Int,
    val name: String,
    val job: String?,
    val department: String?
)