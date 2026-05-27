package cst.unibucfmiif2026.ui.pages

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
    viewModel: MoviesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    MoviesHomeContent(
        userEmail = userEmail,
        uiState = uiState,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onSearch = viewModel::searchMovies,
        onClearSearch = viewModel::clearSearch,
        onMovieClick = onMovieClick,
        onToggleWatchlist = viewModel::toggleWatchlist,
        onRetry = {
            if (uiState.isShowingSearchResults) {
                viewModel.searchMovies()
            } else {
                viewModel.loadTrendingMovies()
            }
        }
    )
}

@Composable
private fun MoviesHomeContent(
    userEmail: String?,
    uiState: MoviesUiState,
    onSearchQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClearSearch: () -> Unit,
    onMovieClick: (Movie) -> Unit,
    onToggleWatchlist: (Movie) -> Unit,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = stringResource(R.string.home_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.movies_home_subtitle),
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = userEmail?.let { email ->
                stringResource(R.string.logged_in_as, email)
            } ?: stringResource(R.string.guest_mode_label),
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(22.dp))

        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.search_movies_label)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = stringResource(R.string.search_movies_label)
                )
            },
            trailingIcon = {
                if (uiState.searchQuery.isNotBlank()) {
                    IconButton(onClick = onClearSearch) {
                        Icon(
                            imageVector = Icons.Outlined.Clear,
                            contentDescription = stringResource(R.string.clear_search_label)
                        )
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onSearch,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.search_label))
            }

            OutlinedButton(
                onClick = onClearSearch,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.show_trending_label))
            }
        }

        uiState.infoMessage?.let { message ->
            Spacer(modifier = Modifier.height(18.dp))
            InfoCard(
                message = message,
                onRetry = onRetry
            )
        }

        Spacer(modifier = Modifier.height(34.dp))

        Text(
            text = if (uiState.isShowingSearchResults) {
                stringResource(R.string.search_results_title)
            } else {
                stringResource(R.string.trending_movies_title)
            },
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(
                R.string.movies_count_with_watchlist_label,
                uiState.movies.size,
                uiState.watchlistIds.size
            ),
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(18.dp))

        when {
            uiState.isLoading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 40.dp),
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
            }

            uiState.movies.isEmpty() -> {
                Text(
                    text = stringResource(R.string.no_movies_found_label),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.movies, key = { movie -> movie.id }) { movie ->
                        MovieCard(
                            movie = movie,
                            onMovieClick = { onMovieClick(movie) },
                            onToggleWatchlist = { onToggleWatchlist(movie) }
                        )
                    }
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

@Preview(showBackground = true)
@Composable
private fun MoviesHomePagePreview() {
    UniBucFMIIF2026Theme {
        MoviesHomeContent(
            userEmail = "user@example.com",
            uiState = MoviesUiState(
                isLoading = false,
                movies = previewMovies,
                watchlistIds = setOf(previewMovies.first().id)
            ),
            onSearchQueryChange = {},
            onSearch = {},
            onClearSearch = {},
            onMovieClick = {},
            onToggleWatchlist = {},
            onRetry = {}
        )
    }
}
