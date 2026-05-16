package cst.unibucfmiif2026.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cst.unibucfmiif2026.R
import cst.unibucfmiif2026.movie.data.previewMovies
import cst.unibucfmiif2026.movie.model.Movie
import cst.unibucfmiif2026.movie.viewmodel.MoviesUiState
import cst.unibucfmiif2026.movie.viewmodel.MoviesViewModel
import cst.unibucfmiif2026.ui.theme.UniBucFMIIF2026Theme

@Composable
fun MoviesHomePage(
    userEmail: String? = null,
    onMovieClick: (Movie) -> Unit = {},
    onLogout: () -> Unit = {},
    viewModel: MoviesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    MoviesHomeContent(
        userEmail = userEmail,
        uiState = uiState,
        onMovieClick = onMovieClick,
        onRetry = viewModel::loadTrendingMovies,
        onLogout = onLogout
    )
}

@Composable
private fun MoviesHomeContent(
    userEmail: String?,
    uiState: MoviesUiState,
    onMovieClick: (Movie) -> Unit,
    onRetry: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = stringResource(R.string.home_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.movies_home_subtitle),
            style = MaterialTheme.typography.bodyLarge
        )

        userEmail?.let { email ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.logged_in_as, email),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        uiState.infoMessage?.let { message ->
            Spacer(modifier = Modifier.height(16.dp))
            InfoCard(
                message = message,
                onRetry = onRetry
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.trending_movies_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.movies_count_label, uiState.movies.size),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Button(onClick = onLogout) {
                Text(stringResource(R.string.logout_btn))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.loading_movies_label),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.movies, key = { movie -> movie.id }) { movie ->
                    MovieListItem(
                        movie = movie,
                        onMovieClick = { onMovieClick(movie) }
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoCard(
    message: String,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )

            OutlinedButton(
                onClick = onRetry,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(stringResource(R.string.retry_label))
            }
        }
    }
}

@Composable
private fun MovieListItem(
    movie: Movie,
    onMovieClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    movie.releaseDate?.let { releaseDate ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.release_date_label, releaseDate),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = stringResource(R.string.movie_rating_label, movie.voteAverage),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
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
                text = movie.overview,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            OutlinedButton(
                onClick = onMovieClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(stringResource(R.string.movie_details_cta))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MoviesHomePagePreview() {
    UniBucFMIIF2026Theme {
        MoviesHomeContent(
            userEmail = "user@example.com",
            uiState = MoviesUiState(
                isLoading = false,
                movies = previewMovies
            ),
            onMovieClick = {},
            onRetry = {},
            onLogout = {}
        )
    }
}
