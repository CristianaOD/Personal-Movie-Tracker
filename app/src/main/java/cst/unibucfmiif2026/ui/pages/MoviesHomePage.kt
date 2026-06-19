package cst.unibucfmiif2026.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import cst.unibucfmiif2026.R
import cst.unibucfmiif2026.movie.model.Movie
import cst.unibucfmiif2026.movie.network.TmdbImageUrlBuilder
import cst.unibucfmiif2026.movie.viewmodel.MoviesUiState
import cst.unibucfmiif2026.movie.viewmodel.MoviesViewModel
import cst.unibucfmiif2026.ui.theme.*

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
        onSearchQueryChange = { query ->
            viewModel.onSearchQueryChange(query)
            if (query.isBlank()) {
                viewModel.clearSearch()
            } else {
                viewModel.searchMovies()
            }
        },
        onClearSearch = viewModel::clearSearch,
        onMovieClick = onMovieClick,
        onToggleWatchlist = viewModel::toggleWatchlist
    )
}

@Composable
private fun MoviesHomeContent(
    userEmail: String?,
    uiState: MoviesUiState,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onMovieClick: (Movie) -> Unit,
    onToggleWatchlist: (Movie) -> Unit
) {
    val isSearching = uiState.searchQuery.isNotBlank()

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .background(LbBackground),
        contentPadding = PaddingValues(
            start = 12.dp,
            end = 12.dp,
            top = 0.dp,
            bottom = 16.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item(span = { GridItemSpan(2) }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(top = 16.dp, bottom = 8.dp)
            ) {
                Text(
                    text = if (isSearching) "Search results" else "What's trending",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = LbTextPrimary
                )

                Text(
                    text = userEmail ?: "Guest",
                    fontSize = 12.sp,
                    color = LbTextMuted,
                    modifier = Modifier.padding(top = 2.dp, bottom = 16.dp)
                )

                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = {
                        Text(
                            text = "Search films, directors...",
                            color = LbTextMuted,
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = null,
                            tint = if (isSearching) LbGreen else LbTextMuted
                        )
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotBlank()) {
                            IconButton(onClick = onClearSearch) {
                                Icon(
                                    imageVector = Icons.Outlined.Clear,
                                    contentDescription = "Clear",
                                    tint = LbTextMuted
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(4.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = LbTextPrimary,
                        unfocusedTextColor = LbTextPrimary,
                        focusedBorderColor = LbGreen,
                        unfocusedBorderColor = LbBorder,
                        focusedContainerColor = LbSurface,
                        unfocusedContainerColor = LbSurface,
                        cursorColor = LbGreen
                    )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isSearching)
                            "${uiState.movies.size} RESULTS"
                        else
                            "TRENDING TODAY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = LbTextSecondary,
                        letterSpacing = 0.8.sp
                    )
                    if (!isSearching) {
                        Text(
                            text = "${uiState.watchlistIds.size} saved",
                            fontSize = 11.sp,
                            color = LbTextMuted
                        )
                    }
                }
            }
        }

        uiState.infoMessage?.let { message ->
            item(span = { GridItemSpan(2) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(LbSurface)
                        .padding(12.dp)
                ) {
                    Text(
                        text = message,
                        fontSize = 12.sp,
                        color = LbTextSecondary
                    )
                }
            }
        }

        if (uiState.isLoading) {
            item(span = { GridItemSpan(2) }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = LbGreen)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Loading films...",
                        fontSize = 13.sp,
                        color = LbTextSecondary
                    )
                }
            }
        } else if (uiState.movies.isEmpty()) {
            item(span = { GridItemSpan(2) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isSearching) "No results found." else "No films available.",
                        fontSize = 14.sp,
                        color = LbTextMuted
                    )
                }
            }
        } else {
            items(uiState.movies, key = { it.id }) { movie ->
                MovieGridCard(
                    movie = movie,
                    onMovieClick = { onMovieClick(movie) },
                    onToggleWatchlist = { onToggleWatchlist(movie) }
                )
            }
        }
    }
}

@Composable
fun MovieGridCard(
    movie: Movie,
    onMovieClick: () -> Unit,
    onToggleWatchlist: () -> Unit
) {
    val posterUrl = TmdbImageUrlBuilder.posterUrl(movie.posterPath)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.67f)
            .clip(RoundedCornerShape(4.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onMovieClick
            )
    ) {
        if (posterUrl != null) {
            AsyncImage(
                model = posterUrl,
                contentDescription = movie.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LbSurface),
                contentAlignment = Alignment.Center
            ) {
                Text("?", color = LbTextMuted, fontSize = 32.sp)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to Color.Transparent,
                            0.5f to Color.Transparent,
                            0.75f to Color(0xFF000000).copy(alpha = 0.6f),
                            1.0f to Color(0xFF000000).copy(alpha = 0.92f)
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(6.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0xFF000000).copy(alpha = 0.7f))
                .padding(horizontal = 5.dp, vertical = 2.dp)
        ) {
            Text(
                text = "★ ${String.format("%.1f", movie.voteAverage)}",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = LbTextPrimary
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(4.dp)
        ) {
            IconButton(
                onClick = onToggleWatchlist,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (movie.isInWatchlist)
                        Icons.Outlined.Bookmark
                    else
                        Icons.Outlined.BookmarkBorder,
                    contentDescription = "Watchlist",
                    tint = if (movie.isInWatchlist) LbGreen else LbTextPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            Text(
                text = movie.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = LbTextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )
            movie.releaseDate?.take(4)?.let { year ->
                Text(
                    text = year,
                    fontSize = 10.sp,
                    color = LbTextSecondary
                )
            }
        }
    }
}