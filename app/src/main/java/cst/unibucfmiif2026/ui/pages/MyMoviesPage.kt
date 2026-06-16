package cst.unibucfmiif2026.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import cst.unibucfmiif2026.movie.data.local.WatchedMovieEntity
import cst.unibucfmiif2026.movie.network.TmdbImageUrlBuilder
import cst.unibucfmiif2026.movie.viewmodel.MyMoviesTab
import cst.unibucfmiif2026.movie.viewmodel.MyMoviesViewModel
import cst.unibucfmiif2026.movie.viewmodel.SortOrder
import cst.unibucfmiif2026.ui.theme.*

@Composable
fun MyMoviesPage(
    onMovieClick: (Int) -> Unit = {},
    viewModel: MyMoviesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LbBackground)
            .statusBarsPadding()
    ) {
        // Header
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "My Movies",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = LbTextPrimary
            )
            Text(
                text = "${uiState.reviewed.size} reviewed · ${uiState.favorites.size} favorites",
                fontSize = 12.sp,
                color = LbTextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        MyMoviesTabRow(
            selectedTab = uiState.tab,
            onTabSelected = viewModel::setTab
        )

        if (uiState.tab == MyMoviesTab.REVIEWED) {
            SortRow(
                sortOrder = uiState.sortOrder,
                onSortSelected = viewModel::setSortOrder
            )
        }

        when (uiState.tab) {
            MyMoviesTab.REVIEWED -> {
                if (uiState.reviewed.isEmpty()) {
                    EmptyState(message = "No reviews yet.\nRate a film from its details page.")
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(uiState.reviewed, key = { it.movieId }) { movie ->
                            ReviewedMovieCard(
                                movie = movie,
                                onMovieClick = { onMovieClick(movie.movieId) },
                                onToggleFavorite = { viewModel.toggleFavorite(movie.movieId) },
                                onDelete = { viewModel.deleteReview(movie.movieId) }
                            )
                        }
                    }
                }
            }
            MyMoviesTab.FAVORITES -> {
                if (uiState.favorites.isEmpty()) {
                    EmptyState(message = "No favorites yet.\nMark a film as favorite from its details page.")
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(uiState.favorites, key = { it.movieId }) { movie ->
                            ReviewedMovieCard(
                                movie = movie,
                                onMovieClick = { onMovieClick(movie.movieId) },
                                onToggleFavorite = { viewModel.toggleFavorite(movie.movieId) },
                                onDelete = { viewModel.deleteReview(movie.movieId) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MyMoviesTabRow(
    selectedTab: MyMoviesTab,
    onTabSelected: (MyMoviesTab) -> Unit
) {
    val tabs = listOf(MyMoviesTab.REVIEWED to "REVIEWED", MyMoviesTab.FAVORITES to "FAVORITES")
    Row(modifier = Modifier.fillMaxWidth()) {
        tabs.forEach { (tab, label) ->
            val isSelected = selectedTab == tab
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onTabSelected(tab) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) LbTextPrimary else LbTextMuted,
                    letterSpacing = 0.8.sp,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(if (isSelected) LbGreen else LbBorderSubtle)
                )
            }
        }
    }
}

@Composable
private fun SortRow(
    sortOrder: SortOrder,
    onSortSelected: (SortOrder) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "SORT BY",
            fontSize = 10.sp,
            color = LbTextMuted,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.6.sp
        )
        SortChip(
            label = "Date",
            selected = sortOrder == SortOrder.BY_DATE,
            onClick = { onSortSelected(SortOrder.BY_DATE) }
        )
        SortChip(
            label = "My Rating",
            selected = sortOrder == SortOrder.BY_RATING,
            onClick = { onSortSelected(SortOrder.BY_RATING) }
        )
    }
}

@Composable
private fun SortChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(2.dp))
            .background(if (selected) LbGreen else LbSurface)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = label.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (selected) Color(0xFF14120E) else LbTextSecondary,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun ReviewedMovieCard(
    movie: WatchedMovieEntity,
    onMovieClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete review?", color = LbTextPrimary) },
            text = { Text("This will remove your review and rating for ${movie.title}.", color = LbTextSecondary) },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteDialog = false
                }) {
                    Text("Delete", color = LbError)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = LbTextSecondary)
                }
            },
            containerColor = LbSurfaceRaised
        )
    }

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
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = movie.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = LbTextPrimary
            )

            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                repeat(5) { index ->
                    Text(
                        text = "★",
                        fontSize = 13.sp,
                        color = if (index < movie.rating) LbGreen else LbBorder
                    )
                }
            }

            if (movie.comment.isNotBlank()) {
                Text(
                    text = movie.comment,
                    fontSize = 12.sp,
                    color = LbTextSecondary,
                    lineHeight = 17.sp,
                    maxLines = 3
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (movie.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Toggle favorite",
                        tint = if (movie.isFavorite) LbError else LbTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }

                TextButton(
                    onClick = { showDeleteDialog = true },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Delete",
                        fontSize = 11.sp,
                        color = LbTextMuted
                    )
                }
            }
        }
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
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            lineHeight = 22.sp
        )
    }
}