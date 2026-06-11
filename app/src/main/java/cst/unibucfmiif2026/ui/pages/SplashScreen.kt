package cst.unibucfmiif2026.ui.pages

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cst.unibucfmiif2026.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun MovieTrackerSplashScreen(
    posterPaths: List<String> = emptyList(),
    onSplashFinished: () -> Unit = {}
) {
    val alpha = remember { Animatable(0f) }
    val translateY = remember { Animatable(24f) }

    LaunchedEffect(Unit) {
        // Fade in logo
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800)
        )
        translateY.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 600)
        )

        // Wait 2.5 secunde
        delay(2500)

        // Fade out
        alpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 500)
        )

        onSplashFinished()
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // Poster collage fundal
        PosterCollageBackground(posterPaths = posterPaths)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LbBackground.copy(alpha = 0.45f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    this.alpha = alpha.value
                    this.translationY = translateY.value
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "movie tracker",
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                color = LbTextPrimary,
                letterSpacing = (-1).sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "track every film you've ever loved",
                fontSize = 14.sp,
                color = LbTextSecondary,
                letterSpacing = 0.3.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    val dotAlpha = remember { Animatable(0.3f) }
                    LaunchedEffect(Unit) {
                        delay(index * 200L)
                        while (true) {
                            dotAlpha.animateTo(1f, tween(400))
                            dotAlpha.animateTo(0.3f, tween(400))
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(RoundedCornerShape(50))
                            .background(LbGreen.copy(alpha = dotAlpha.value))
                    )
                }
            }
        }
    }
}