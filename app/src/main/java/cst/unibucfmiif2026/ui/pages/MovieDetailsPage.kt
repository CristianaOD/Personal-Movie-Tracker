package cst.unibucfmiif2026.ui.pages

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import cst.unibucfmiif2026.R
import cst.unibucfmiif2026.movie.data.previewMovies
import cst.unibucfmiif2026.movie.model.Movie
import cst.unibucfmiif2026.movie.network.TmdbImageUrlBuilder
import cst.unibucfmiif2026.movie.viewmodel.MovieDetailsUiState
import cst.unibucfmiif2026.movie.viewmodel.MovieDetailsViewModel
import cst.unibucfmiif2026.ui.theme.UniBucFMIIF2026Theme

@Composable
fun MovieDetailsPage(
    movieId: Int,
    onBackClick: () -> Unit = {}
) {
    val application = LocalContext.current.applicationContext as Application
    val viewModel: MovieDetailsViewModel = viewModel(
        key = "movie_details_$movieId",
        factory = MovieDetailsViewModel.factory(
            application = application,
            movieId = movieId
        )
    )
    val uiState by viewModel.uiState.collectAsState()

    MovieDetailsContent(
        uiState = uiState,
        onRetry = viewModel::loadMovieDetails,
        onToggleWatchlist = viewModel::toggleWatchlist,
        onBackClick = onBackClick
    )
}

@Composable
private fun MovieDetailsContent(
    uiState: MovieDetailsUiState,
    onRetry: () -> Unit,
    onToggleWatchlist: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

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

                MovieDetailsCard(
                    movie = uiState.movie,
                    onToggleWatchlist = onToggleWatchlist
                )

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}

@Composable
private fun MovieDetailsCard(
    movie: Movie,
    onToggleWatchlist: () -> Unit
) {
    val backdropUrl = TmdbImageUrlBuilder.backdropUrl(movie.backdropPath)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            if (backdropUrl != null) {
                AsyncImage(
                    model = backdropUrl,
                    contentDescription = movie.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    MoviePoster(
                        posterPath = movie.posterPath,
                        title = movie.title,
                        modifier = Modifier
                            .width(108.dp)
                            .height(168.dp)
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = movie.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        movie.releaseDate?.let { releaseDate ->
                            Text(
                                text = stringResource(R.string.release_date_label, releaseDate),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Text(
                            text = stringResource(
                                R.string.movie_rating_details_label,
                                movie.voteAverage,
                                movie.voteCount
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        OutlinedButton(onClick = onToggleWatchlist) {
                            Text(
                                if (movie.isInWatchlist) {
                                    stringResource(R.string.remove_from_watchlist_label)
                                } else {
                                    stringResource(R.string.add_to_watchlist_label)
                                }
                            )
                        }
                    }
                }

                if (movie.genres.isNotEmpty()) {
                    Text(
                        text = movie.genres.joinToString(separator = " / "),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

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
}

@Preview(showBackground = true)
@Composable
private fun MovieDetailsPagePreview() {
    UniBucFMIIF2026Theme {
        MovieDetailsContent(
            uiState = MovieDetailsUiState(
                isLoading = false,
                movie = previewMovies.first().copy(isInWatchlist = true)
            ),
            onRetry = {},
            onToggleWatchlist = {},
            onBackClick = {}
        )
    }
}
