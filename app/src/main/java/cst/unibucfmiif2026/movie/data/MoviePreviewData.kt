package cst.unibucfmiif2026.movie.data

import cst.unibucfmiif2026.movie.model.Movie

val previewMovies = listOf(
    Movie(
        id = 1,
        title = "Dune: Part Two",
        overview = "Paul Atreides unites with Chani and the Fremen while seeking revenge against the conspirators who destroyed his family.",
        releaseDate = "2024-03-01",
        voteAverage = 8.6,
        genres = listOf("Science Fiction", "Adventure", "Drama")
    ),
    Movie(
        id = 2,
        title = "The Wild Robot",
        overview = "After a shipwreck, an intelligent robot called Roz is stranded on an uninhabited island and must adapt to its harsh surroundings.",
        releaseDate = "2024-09-27",
        voteAverage = 8.3,
        genres = listOf("Animation", "Family", "Adventure")
    ),
    Movie(
        id = 3,
        title = "Inside Out 2",
        overview = "Riley enters her teenage years and new emotions arrive at headquarters, changing everything the original team thought they understood.",
        releaseDate = "2024-06-14",
        voteAverage = 7.9,
        genres = listOf("Animation", "Comedy", "Family")
    )
)
