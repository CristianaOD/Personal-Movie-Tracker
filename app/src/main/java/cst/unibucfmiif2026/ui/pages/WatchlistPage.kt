package cst.unibucfmiif2026.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkRemove
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import cst.unibucfmiif2026.movie.data.previewMovies
import cst.unibucfmiif2026.movie.model.Movie
import cst.unibucfmiif2026.movie.network.TmdbImageUrlBuilder
import cst.unibucfmiif2026.movie.viewmodel.WatchlistViewModel
import cst.unibucfmiif2026.ui.theme.LbBackground
import cst.unibucfmiif2026.ui.theme.LbBorder
import cst.unibucfmiif2026.ui.theme.LbBorderSubtle
import cst.unibucfmiif2026.ui.theme.LbGreen
import cst.unibucfmiif2026.ui.theme.LbSurface
import cst.unibucfmiif2026.ui.theme.LbSurfaceRaised
import cst.unibucfmiif2026.ui.theme.LbTextMuted
import cst.unibucfmiif2026.ui.theme.LbTextPrimary
import cst.unibucfmiif2026.ui.theme.LbTextSecondary
import cst.unibucfmiif2026.ui.theme.UniBucFMIIF2026Theme

private enum class WatchlistSort {
    RECENT,
    TITLE,
    RATING
}

@Composable
fun WatchlistPage(
    onMovieClick: (Movie) -> Unit,
    viewModel: WatchlistViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    WatchlistContent(
        isLoading = uiState.isLoading,
        movies = uiState.movies,
        onMovieClick = onMovieClick,
        onRemoveMovie = viewModel::removeFromWatchlist
    )
}

@Composable
private fun WatchlistContent(
    isLoading: Boolean,
    movies: List<Movie>,
    onMovieClick: (Movie) -> Unit,
    onRemoveMovie: (Int) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var selectedGenre by remember { mutableStateOf<String?>(null) }
    var sortOrder by remember { mutableStateOf(WatchlistSort.RECENT) }
    var showFilters by remember { mutableStateOf(false) }

    val genres = remember(movies) {
        movies.flatMap { movie -> movie.genres }
            .distinct()
            .sorted()
    }

    val visibleMovies = remember(movies, query, selectedGenre, sortOrder) {
        movies
            .filter { movie ->
                val matchesQuery = query.isBlank() ||
                    movie.title.contains(query, ignoreCase = true) ||
                    movie.overview.contains(query, ignoreCase = true)
                val matchesGenre = selectedGenre == null || movie.genres.contains(selectedGenre)
                matchesQuery && matchesGenre
            }
            .let { filtered ->
                when (sortOrder) {
                    WatchlistSort.RECENT -> filtered
                    WatchlistSort.TITLE -> filtered.sortedBy { movie -> movie.title.lowercase() }
                    WatchlistSort.RATING -> filtered.sortedByDescending { movie -> movie.voteAverage }
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LbBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        WatchlistHeader(
            savedCount = movies.size,
            visibleCount = visibleMovies.size,
            query = query,
            onQueryChange = { query = it },
            showFilters = showFilters,
            onToggleFilters = { showFilters = !showFilters }
        )

        if (showFilters) {
            WatchlistFilterPanel(
                genres = genres,
                selectedGenre = selectedGenre,
                sortOrder = sortOrder,
                onGenreSelected = { genre -> selectedGenre = genre },
                onSortSelected = { sortOrder = it },
                onClearFilters = {
                    query = ""
                    selectedGenre = null
                    sortOrder = WatchlistSort.RECENT
                }
            )
        }

        when {
            isLoading -> LoadingState()
            movies.isEmpty() -> EmptyState("Your watchlist is empty.\nSave a film from Home to see it here.")
            visibleMovies.isEmpty() -> EmptyState("No saved movies match these filters.")
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(visibleMovies, key = { movie -> movie.id }) { movie ->
                        WatchlistMovieCard(
                            movie = movie,
                            onMovieClick = { onMovieClick(movie) },
                            onRemoveMovie = { onRemoveMovie(movie.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WatchlistHeader(
    savedCount: Int,
    visibleCount: Int,
    query: String,
    onQueryChange: (String) -> Unit,
    showFilters: Boolean,
    onToggleFilters: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Watchlist",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = LbTextPrimary
                )
                Text(
                    text = "$visibleCount shown - $savedCount saved",
                    fontSize = 12.sp,
                    color = LbTextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            IconButton(
                onClick = onToggleFilters,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (showFilters) LbGreen else LbSurface)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Tune,
                    contentDescription = "Filter watchlist",
                    tint = if (showFilters) Color(0xFF14120E) else LbTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = {
                Text(
                    text = "Search saved movies",
                    color = LbTextMuted,
                    fontSize = 13.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Search",
                    tint = LbTextSecondary
                )
            },
            trailingIcon = {
                if (query.isNotBlank()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Outlined.Clear,
                            contentDescription = "Clear search",
                            tint = LbTextSecondary
                        )
                    }
                }
            },
            textStyle = androidx.compose.ui.text.TextStyle(
                color = LbTextPrimary,
                fontSize = 14.sp
            ),
            shape = RoundedCornerShape(4.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = LbGreen,
                unfocusedBorderColor = LbBorder,
                focusedContainerColor = LbSurface,
                unfocusedContainerColor = LbSurface,
                cursorColor = LbGreen
            )
        )
    }
}

@Composable
private fun WatchlistFilterPanel(
    genres: List<String>,
    selectedGenre: String?,
    sortOrder: WatchlistSort,
    onGenreSelected: (String?) -> Unit,
    onSortSelected: (WatchlistSort) -> Unit,
    onClearFilters: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LbSurfaceRaised)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FilterSectionLabel("SORT BY")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                label = "Recent",
                selected = sortOrder == WatchlistSort.RECENT,
                onClick = { onSortSelected(WatchlistSort.RECENT) }
            )
            FilterChip(
                label = "Title",
                selected = sortOrder == WatchlistSort.TITLE,
                onClick = { onSortSelected(WatchlistSort.TITLE) }
            )
            FilterChip(
                label = "Rating",
                selected = sortOrder == WatchlistSort.RATING,
                onClick = { onSortSelected(WatchlistSort.RATING) }
            )
        }

        FilterSectionLabel("GENRE")
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                label = "All",
                selected = selectedGenre == null,
                onClick = { onGenreSelected(null) }
            )
            genres.forEach { genre ->
                FilterChip(
                    label = genre,
                    selected = selectedGenre == genre,
                    onClick = { onGenreSelected(genre) }
                )
            }
        }

        Text(
            text = "Clear filters",
            fontSize = 12.sp,
            color = LbTextSecondary,
            modifier = Modifier
                .align(Alignment.End)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClearFilters
                )
                .padding(vertical = 4.dp)
        )
    }
}

@Composable
private fun FilterSectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 10.sp,
        color = LbTextMuted,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(2.dp))
            .background(if (selected) LbGreen else LbSurface)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (selected) Color(0xFF14120E) else LbTextSecondary
        )
    }
}

@Composable
private fun WatchlistMovieCard(
    movie: Movie,
    onMovieClick: () -> Unit,
    onRemoveMovie: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(LbSurface)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onMovieClick
            )
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val posterUrl = TmdbImageUrlBuilder.posterUrl(movie.posterPath)
        if (posterUrl != null) {
            AsyncImage(
                model = posterUrl,
                contentDescription = movie.title,
                modifier = Modifier
                    .width(60.dp)
                    .height(90.dp)
                    .clip(RoundedCornerShape(3.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(90.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(LbSurfaceRaised),
                contentAlignment = Alignment.Center
            ) {
                Text("?", color = LbTextMuted, fontSize = 20.sp)
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = movie.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = LbTextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = String.format("%.1f", movie.voteAverage),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = LbGreen,
                    modifier = Modifier.padding(start = 8.dp, top = 1.dp)
                )
            }

            if (movie.genres.isNotEmpty()) {
                Text(
                    text = movie.genres.take(3).joinToString(" / "),
                    fontSize = 11.sp,
                    color = LbTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = movie.overview,
                fontSize = 12.sp,
                color = LbTextSecondary,
                lineHeight = 17.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                movie.releaseDate?.let { releaseDate ->
                    Text(
                        text = releaseDate.take(4),
                        fontSize = 11.sp,
                        color = LbTextMuted
                    )
                } ?: Spacer(modifier = Modifier.width(1.dp))

                IconButton(
                    onClick = onRemoveMovie,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.BookmarkRemove,
                        contentDescription = "Remove from watchlist",
                        tint = LbTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = LbGreen)
    }
}

@Composable
private fun EmptyState(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            fontSize = 14.sp,
            color = LbTextMuted,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WatchlistPagePreview() {
    UniBucFMIIF2026Theme {
        WatchlistContent(
            isLoading = false,
            movies = previewMovies.map { movie -> movie.copy(isInWatchlist = true) },
            onMovieClick = {},
            onRemoveMovie = {}
        )
    }
}
