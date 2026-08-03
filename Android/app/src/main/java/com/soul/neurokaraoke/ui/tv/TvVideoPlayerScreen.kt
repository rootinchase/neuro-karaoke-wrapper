package com.soul.neurokaraoke.ui.tv

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.soul.neurokaraoke.data.api.Video
import com.soul.neurokaraoke.viewmodel.PlayerViewModel

/**
 * Fullscreen native video player overlay. Hosts a Media3 [PlayerView] (built-in D-pad
 * transport via `useController`) bound to a dedicated [ExoPlayer] streaming the Bunny HLS
 * URL. Pauses app audio on open and releases the player on dispose.
 */
@Composable
fun TvVideoPlayerScreen(
    video: Video,
    playerViewModel: PlayerViewModel,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    BackHandler { onBack() }

    // Don't let app audio play underneath the video.
    LaunchedEffect(Unit) { playerViewModel.pausePlayback() }

    val exo = remember(video.id) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(video.hlsUrl))
            prepare()
            playWhenReady = true
        }
    }
    var error by remember { mutableStateOf(false) }

    DisposableEffect(exo) {
        val listener = object : Player.Listener {
            override fun onPlayerError(e: PlaybackException) { error = true }
        }
        exo.addListener(listener)
        onDispose {
            exo.removeListener(listener)
            exo.release()
        }
    }

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exo
                    useController = true
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        Text(
            video.name,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.TopStart).padding(24.dp)
        )
        if (error) {
            Text(
                "Playback failed — press Back",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
