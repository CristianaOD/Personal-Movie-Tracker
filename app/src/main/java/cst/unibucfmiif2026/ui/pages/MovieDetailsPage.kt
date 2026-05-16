package cst.unibucfmiif2026.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cst.unibucfmiif2026.R
import cst.unibucfmiif2026.movie.data.previewMovies
import cst.unibucfmiif2026.movie.model.Movie
import cst.unibucfmiif2026.movie.viewmodel.MovieDetailsUiState
import cst.unibucfmiif2026.movie.viewmodel.MovieDetailsViewModel
import cst.unibucfmiif2026.ui.theme.UniBucFMIIF2026Theme

@Composable
fun MovieDetailsPage(
    movieId: Int,
    onBackClick: () -> Unit = {}
) {
    val viewModel: MovieDetailsViewModel = viewModel(
        key = "movie_details_$movieId",
        factory = MovieDetailsViewModel.factory(movieId)
    )
    val uiState by viewModel.uiState.collectAsState()

    MovieDetailsContent(
        uiState = uiState,
        onRetry = viewModel::loadMovieDetails,
        onBackClick = onBackClick
    )
}

@Composable
private fun MovieDetailsContent(
    uiState: MovieDetailsUiState,
    onRetry: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedButton(onClick = onBackClick) {
            Text(stringResource(R.string.back_to_movies))
        }

        when {
            uiState.isLoading -> {
                CircularProgressIndicator()
                Text(stringResource(R.string.loading_movie_details_label))
            }

            uiState.movie == null -> {
                Text(
                    text = stringResource(R.string.movie_details_not_found),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Button(onClick = onRetry) {
                    Text(stringResource(R.string.retry_label))
                }
            }

            else -> {
                uiState.infoMessage?.let { message ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Text(
                            text = message,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                MovieDetailsCard(movie = uiState.movie)
            }
        }
    }
}

@Composable
private fun MovieDetailsCard(movie: Movie) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            movie.releaseDate?.let { releaseDate ->
                Text(
                    text = stringResource(R.string.release_date_label, releaseDate),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                text = stringResource(R.string.movie_rating_details_label, movie.voteAverage, movie.voteCount),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            if (movie.genres.isNotEmpty()) {
                Text(
                    text = movie.genres.joinToString(separator = " / "),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.movie_overview_label),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = movie.overview,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MovieDetailsPagePreview() {
    UniBucFMIIF2026Theme {
        MovieDetailsContent(
            uiState = MovieDetailsUiState(
                isLoading = false,
                movie = previewMovies.first()
            ),
            onRetry = {},
            onBackClick = {}
        )
    }
}
