package cst.unibucfmiif2026.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cst.unibucfmiif2026.R
import cst.unibucfmiif2026.movie.data.previewMovies
import cst.unibucfmiif2026.movie.model.Movie
import cst.unibucfmiif2026.movie.viewmodel.WatchlistViewModel
import cst.unibucfmiif2026.ui.theme.UniBucFMIIF2026Theme
import androidx.compose.ui.res.stringResource

@Composable
fun WatchlistPage(
    onMovieClick: (Movie) -> Unit,
    viewModel: WatchlistViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = stringResource(R.string.watchlist_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = stringResource(R.string.watchlist_subtitle),
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(26.dp))

        when {
            uiState.isLoading -> {
                Text(stringResource(R.string.loading_watchlist_label))
            }

            uiState.movies.isEmpty() -> {
                Text(
                    text = stringResource(R.string.empty_watchlist_label),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            else -> {
                Text(
                    text = stringResource(R.string.saved_movies_count_label, uiState.movies.size),
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.movies, key = { movie -> movie.id }) { movie ->
                        MovieCard(
                            movie = movie,
                            onMovieClick = { onMovieClick(movie) },
                            onToggleWatchlist = { viewModel.removeFromWatchlist(movie.id) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WatchlistPagePreview() {
    UniBucFMIIF2026Theme {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Preview")
            Spacer(modifier = Modifier.height(12.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(previewMovies) { movie ->
                    MovieCard(
                        movie = movie.copy(isInWatchlist = true),
                        onMovieClick = {},
                        onToggleWatchlist = {}
                    )
                }
            }
        }
    }
}
