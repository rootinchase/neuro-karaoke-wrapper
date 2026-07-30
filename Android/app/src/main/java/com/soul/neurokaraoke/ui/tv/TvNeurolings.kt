package com.soul.neurokaraoke.ui.tv

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.soul.neurokaraoke.R
import kotlin.math.PI
import kotlin.math.sin

/**
 * A purely-cosmetic overlay of little mascots that wander back and forth along the bottom of the
 * screen — a for-fun, dev-only toy (toggled from Developer Options). Uses the shipped launcher
 * character art as a placeholder sprite; it holds no focus and intercepts no input, so it never
 * interferes with D-pad navigation. Trivially removable.
 */
private data class Walker(
    val sizeDp: Dp,
    val periodMs: Int,
    /** 0..1 offset into the walk loop so they don't all move in lockstep. */
    val phase: Float,
    val bobHeightPx: Float,
    /** Start the loop from the right edge instead of the left. */
    val startFromRight: Boolean
)

@Composable
fun TvNeurolings(modifier: Modifier = Modifier, count: Int = 4) {
    val walkers = remember(count) {
        val sizes = listOf(96.dp, 120.dp, 108.dp, 132.dp, 104.dp)
        val periods = listOf(11000, 15000, 13000, 17000, 12500)
        val bobs = listOf(14f, 20f, 10f, 24f, 16f)
        List(count) { i ->
            Walker(
                sizeDp = sizes[i % sizes.size],
                periodMs = periods[i % periods.size],
                phase = i / count.toFloat(),
                bobHeightPx = bobs[i % bobs.size],
                startFromRight = i % 2 == 0
            )
        }
    }
    BoxWithConstraints(modifier.fillMaxSize()) {
        val laneWidthPx = constraints.maxWidth.toFloat()
        walkers.forEach { w -> WalkerSprite(w, laneWidthPx) }
    }
}

@Composable
private fun BoxScope.WalkerSprite(walker: Walker, laneWidthPx: Float) {
    val spritePx = with(LocalDensity.current) { walker.sizeDp.toPx() }
    val transition = rememberInfiniteTransition(label = "neuroling")
    val t by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(walker.periodMs, easing = LinearEasing)),
        label = "walk"
    )

    // Ping-pong across the lane: phase p in 0..1, triangle tri goes 0→1→0.
    val p = (t + walker.phase) % 1f
    val tri = if (p < 0.5f) p * 2f else 2f - p * 2f
    val travel = (laneWidthPx - spritePx).coerceAtLeast(0f)
    val x = if (walker.startFromRight) travel * (1f - tri) else travel * tri

    // Facing = direction of travel. tri rises in the first half of the loop; flip that when the
    // walker starts from the right (its x runs the opposite way).
    val movingRight = (p < 0.5f) != walker.startFromRight
    // Little vertical hop, two bounces per lane crossing.
    val bob = sin(p * 4f * PI.toFloat()) * walker.bobHeightPx

    Image(
        painter = painterResource(R.mipmap.neuro_foreground),
        contentDescription = null,
        modifier = Modifier
            .align(Alignment.BottomStart)
            .size(walker.sizeDp)
            .graphicsLayer {
                translationX = x
                translationY = bob
                scaleX = if (movingRight) 1f else -1f
            }
    )
}
