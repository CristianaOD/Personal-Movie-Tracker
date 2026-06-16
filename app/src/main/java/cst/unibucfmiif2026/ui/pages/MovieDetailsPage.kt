package cst.unibucfmiif2026.ui.pages

import android.app.Application
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import cst.unibucfmiif2026.movie.data.local.WatchedMovieEntity
import cst.unibucfmiif2026.movie.model.Movie
import cst.unibucfmiif2026.movie.network.TmdbImageUrlBuilder
import cst.unibucfmiif2026.movie.viewmodel.MovieDetailsUiState
import cst.unibucfmiif2026.movie.viewmodel.MovieDetailsViewModel
import cst.unibucfmiif2026.ui.theme.*

@Composable
fun MovieDetailsPage(
    movieId: Int,
    onBackClick: () -> Unit = {}
) {
    val application = LocalContext.current.applicationContext as Application
    val viewModel: MovieDetailsViewModel = viewModel(
        key = "movie_details_$movieId",
        factory = MovieDetailsViewModel.factory(application, movieId)
    )
    val uiState by viewModel.uiState.collectAsState()
    val watchedMovie by viewModel.watchedMovie.collectAsState()

    MovieDetailsContent(
        uiState = uiState,
        watchedMovie = watchedMovie,
        onRetry = viewModel::loadMovieDetails,
        onToggleWatchlist = viewModel::toggleWatchlist,
        onSaveReview = viewModel::saveReview,
        onToggleFavorite = viewModel::toggleFavorite,
        onDeleteReview = viewModel::deleteReview,
        onBackClick = onBackClick
    )
}

@Composable
private fun MovieDetailsContent(
    uiState: MovieDetailsUiState,
    watchedMovie: WatchedMovieEntity? = null,
    onRetry: () -> Unit = {},
    onToggleWatchlist: () -> Unit = {},
    onSaveReview: (Int, String) -> Unit = { _, _ -> },
    onToggleFavorite: () -> Unit = {},
    onDeleteReview: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LbBackground)
    ) {
        when {
            uiState.isLoading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = LbGreen)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Loading...",
                        color = LbTextSecondary,
                        fontSize = 13.sp
                    )
                }
            }

            uiState.movie == null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Film not found",
                        color = LbTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onRetry,
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LbGreen,
                            contentColor = Color(0xFF14120E)
                        )
                    ) {
                        Text("RETRY", fontWeight = FontWeight.Bold, letterSpacing = 0.6.sp)
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        val backdropUrl = TmdbImageUrlBuilder.backdropUrl(uiState.movie.backdropPath)
                        if (backdropUrl != null) {
                            AsyncImage(
                                model = backdropUrl,
                                contentDescription = uiState.movie.title,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(240.dp),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(240.dp)
                                    .background(
                                        Brush.verticalGradient(
                                            colorStops = arrayOf(
                                                0.0f to Color.Transparent,
                                                0.6f to LbBackground.copy(alpha = 0.6f),
                                                1.0f to LbBackground
                                            )
                                        )
                                    )
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .background(LbSurface)
                            )
                        }

                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .statusBarsPadding()
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ArrowBack,
                                contentDescription = "Back",
                                tint = LbTextPrimary
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Poster + info
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            // Poster
                            val posterUrl = TmdbImageUrlBuilder.posterUrl(uiState.movie.posterPath)
                            if (posterUrl != null) {
                                AsyncImage(
                                    model = posterUrl,
                                    contentDescription = uiState.movie.title,
                                    modifier = Modifier
                                        .width(100.dp)
                                        .height(150.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .width(100.dp)
                                        .height(150.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(LbSurface),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("?", color = LbTextMuted, fontSize = 24.sp)
                                }
                            }

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = uiState.movie.title,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LbTextPrimary,
                                    lineHeight = 26.sp
                                )

                                uiState.movie.releaseDate?.let {
                                    Text(
                                        text = it.take(4),
                                        fontSize = 13.sp,
                                        color = LbTextMuted
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text("★", fontSize = 14.sp, color = LbGreen)
                                    Text(
                                        text = String.format("%.1f", uiState.movie.voteAverage),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = LbTextPrimary
                                    )
                                    Text(
                                        text = "(${uiState.movie.voteCount})",
                                        fontSize = 11.sp,
                                        color = LbTextMuted
                                    )
                                }

                                if (uiState.movie.genres.isNotEmpty()) {
                                    Text(
                                        text = uiState.movie.genres.joinToString(" / "),
                                        fontSize = 12.sp,
                                        color = LbBlue
                                    )
                                }

                                OutlinedButton(
                                    onClick = onToggleWatchlist,
                                    shape = RoundedCornerShape(4.dp),
                                    border = BorderStroke(
                                        1.dp,
                                        if (uiState.movie.isInWatchlist) LbGreen else LbBorder
                                    ),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = if (uiState.movie.isInWatchlist) LbGreen else LbTextSecondary
                                    ),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = if (uiState.movie.isInWatchlist) "✓ WATCHLIST" else "+ WATCHLIST",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }
                        }

                        uiState.infoMessage?.let { message ->
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

                        LbSectionLabel("OVERVIEW")
                        Text(
                            text = uiState.movie.overview,
                            fontSize = 14.sp,
                            color = LbTextSecondary,
                            lineHeight = 21.sp
                        )

                        HorizontalDivider(color = LbBorderSubtle)

                        ReviewSection(
                            watchedMovie = watchedMovie,
                            onSaveReview = onSaveReview,
                            onToggleFavorite = onToggleFavorite,
                            onDeleteReview = onDeleteReview
                        )

                        Spacer(modifier = Modifier.navigationBarsPadding())
                    }
                }
            }
        }
    }
}

@Composable
private fun LbSectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = LbTextSecondary,
        letterSpacing = 0.8.sp
    )
}

@Composable
private fun ReviewSection(
    watchedMovie: WatchedMovieEntity?,
    onSaveReview: (Int, String) -> Unit,
    onToggleFavorite: () -> Unit,
    onDeleteReview: () -> Unit
) {
    var isEditing by remember { mutableStateOf(watchedMovie == null) }
    var selectedRating by remember { mutableIntStateOf(watchedMovie?.rating ?: 0) }
    var comment by remember(watchedMovie) { mutableStateOf(watchedMovie?.comment ?: "") }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LbSectionLabel("MY REVIEW")
            if (watchedMovie != null && !isEditing) {
                Row {
                    TextButton(
                        onClick = { isEditing = true },
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Text("Edit", color = LbBlue, fontSize = 12.sp)
                    }
                    TextButton(
                        onClick = onDeleteReview,
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Text("Delete", color = LbError, fontSize = 12.sp)
                    }
                }
            }
        }

        if (watchedMovie != null && !isEditing) {
            // View mode
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                repeat(5) { index ->
                    Text(
                        text = "★",
                        fontSize = 22.sp,
                        color = if (index < watchedMovie.rating) LbGreen else LbBorder
                    )
                }
            }

            if (watchedMovie.comment.isNotBlank()) {
                Text(
                    text = watchedMovie.comment,
                    fontSize = 13.sp,
                    color = LbTextSecondary,
                    lineHeight = 19.sp
                )
            }

            OutlinedButton(
                onClick = onToggleFavorite,
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(
                    1.dp,
                    if (watchedMovie.isFavorite) LbError else LbBorder
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (watchedMovie.isFavorite) LbError else LbTextSecondary
                )
            ) {
                Icon(
                    imageVector = if (watchedMovie.isFavorite)
                        Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (watchedMovie.isFavorite) "REMOVE FROM FAVORITES" else "ADD TO FAVORITES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        } else {
            LbSectionLabel("YOUR RATING")

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(5) { index ->
                    Text(
                        text = "★",
                        fontSize = 32.sp,
                        color = if (index < selectedRating) LbGreen else LbBorder,
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { selectedRating = index + 1 }
                    )
                }
            }

            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                placeholder = {
                    Text(
                        "Add a note about this film...",
                        color = LbTextMuted,
                        fontSize = 13.sp
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = LbTextPrimary,
                    unfocusedTextColor = LbTextPrimary,
                    focusedBorderColor = LbGreen,
                    unfocusedBorderColor = LbBorder,
                    focusedContainerColor = LbSurface,
                    unfocusedContainerColor = LbSurface,
                    cursorColor = LbGreen
                ),
                shape = RoundedCornerShape(4.dp)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        if (selectedRating > 0) {
                            onSaveReview(selectedRating, comment)
                            isEditing = false
                        }
                    },
                    enabled = selectedRating > 0,
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LbGreen,
                        contentColor = Color(0xFF14120E),
                        disabledContainerColor = LbGreen.copy(alpha = 0.3f)
                    )
                ) {
                    Text(
                        text = "SAVE REVIEW",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                if (watchedMovie != null) {
                    TextButton(onClick = { isEditing = false }) {
                        Text("Cancel", color = LbTextMuted, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}