package com.soul.neurokaraoke.ui.tv

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.zIndex

/** Grows the composable when D-pad focus lands on it. */
fun Modifier.tvFocusScale(focused: Boolean, scale: Float = 1.1f): Modifier = composed {
    val s = animateFloatAsState(if (focused) scale else 1f, label = "tvFocusScale").value
    this.zIndex(if (focused) 1f else 0f).scale(s)
}
