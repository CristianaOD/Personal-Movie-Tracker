package cst.unibucfmiif2026.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import cst.unibucfmiif2026.R
import cst.unibucfmiif2026.movie.model.Movie
import cst.unibucfmiif2026.movie.network.TmdbImageUrlBuilder

@Composable
fun MovieCard(
    movie: Movie,
    onMovieClick: () -> Unit,
    onToggleWatchlist: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            MoviePoster(
                posterPath = movie.posterPath,
                title = movie.title,
                modifier = Modifier
                    .width(96.dp)
                    .height(144.dp)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onToggleWatchlist) {
                        Icon(
                            imageVector = if (movie.isInWatchlist) {
                                Icons.Rounded.Bookmark
                            } else {
                                Icons.Outlined.Bookmark
                            },
                            contentDescription = stringResource(R.string.toggle_watchlist_label),
                            tint = if (movie.isInWatchlist) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }

                    OutlinedButton(onClick = onMovieClick) {
                        Text(stringResource(R.string.movie_details_cta))
                    }
                }
            }
        }
    }
}

@Composable
fun MoviePoster(
    posterPath: String?,
    title: String,
    modifier: Modifier = Modifier
) {
    val posterUrl = TmdbImageUrlBuilder.posterUrl(posterPath)

    if (posterUrl != null) {
        AsyncImage(
            model = posterUrl,
            contentDescription = title,
            modifier = modifier
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.no_poster_label),
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}
