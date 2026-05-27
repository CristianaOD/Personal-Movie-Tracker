package cst.unibucfmiif2026.movie.network

object TmdbImageUrlBuilder {
    private const val BASE_IMAGE_URL = "https://image.tmdb.org/t/p/"

    fun posterUrl(path: String?, size: String = "w342"): String? {
        return path
            ?.takeIf { value -> value.isNotBlank() }
            ?.let { value -> "$BASE_IMAGE_URL$size$value" }
    }

    fun backdropUrl(path: String?, size: String = "w780"): String? {
        return path
            ?.takeIf { value -> value.isNotBlank() }
            ?.let { value -> "$BASE_IMAGE_URL$size$value" }
    }
}
