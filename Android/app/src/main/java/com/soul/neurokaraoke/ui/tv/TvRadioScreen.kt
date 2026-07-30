package com.soul.neurokaraoke.ui.tv

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.soul.neurokaraoke.data.api.RadioApi
import com.soul.neurokaraoke.data.api.RadioSong
import com.soul.neurokaraoke.data.api.RadioState
import com.soul.neurokaraoke.viewmodel.PlayerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

private fun radioDuration(seconds: Int): String = "%d:%02d".format(seconds / 60, seconds % 60)

private fun RadioSong.subtitle(): String =
    "${originalArtists.joinToString(", ")} • $coverArtistDisplay"

/**
 * TV Radio: the live "NEURO 21 STATION" (parity with the phone RadioScreen and the
 * car Radio tab). Polls [RadioApi] for now-playing / up-next / history / listeners,
 * and toggles the live stream via [PlayerViewModel.playRadio]/[PlayerViewModel.stopRadio].
 */
@Composable
fun TvRadioScreen(playerViewModel: PlayerViewModel = viewModel()) {
    val playerState by playerViewModel.uiState.collectAsStateWithLifecycle()
    val isRadioPlaying = playerState.isRadioMode

    val radioApi = remember { RadioApi() }
    var radioState by remember { mutableStateOf<RadioState?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val listenFocus = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        while (isActive) {
            radioApi.fetchCurrentState().fold(
                onSuccess = { radioState = it; isLoading = false; error = null },
                onFailure = {
                    if (radioState == null) error = it.message ?: "Failed to connect"
                    isLoading = false
                }
            )
            delay(15_000L)
        }
    }
    LaunchedEffect(Unit) { runCatching { listenFocus.requestFocus() } }

    if (isLoading && radioState == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    val state = radioState
    if (state == null || state.offline || error != null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "NEURO 21 STATION",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    if (error != null) "Couldn't reach the station — try again shortly." else "The station is offline right now.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    Row(modifier = Modifier.fillMaxSize().padding(horizontal = 56.dp, vertical = 32.dp)) {
        // LEFT: station + current track + Listen/Stop
        Column(
            modifier = Modifier.weight(0.45f).fillMaxHeight().padding(end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "NEURO 21 STATION",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(10.dp).clip(CircleShape).background(MaterialTheme.colorScheme.error)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    "LIVE",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
                if (state.listenerCount > 0) {
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "${state.listenerCount} listening",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            state.current?.let { song ->
                AsyncImage(
                    model = song.coverUrl,
                    contentDescription = song.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(160.dp)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(16.dp))
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    song.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    song.subtitle(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.height(18.dp))
            ListenButton(
                isPlaying = isRadioPlaying,
                focusRequester = listenFocus,
                onClick = { if (isRadioPlaying) playerViewModel.stopRadio() else playerViewModel.playRadio() }
            )
        }

        // RIGHT: Up Next + Recently Played
        LazyColumn(modifier = Modifier.weight(0.55f).fillMaxHeight().padding(start = 24.dp)) {
            if (state.upcoming.isNotEmpty()) {
                item { RadioSectionHeader("Up Next") }
                itemsIndexed(state.upcoming, key = { i, s -> "up_${i}_${s.id}" }) { _, song ->
                    RadioSongRow(song)
                }
            }
            if (state.history.isNotEmpty()) {
                item { RadioSectionHeader("Recently Played") }
                itemsIndexed(state.history, key = { i, s -> "hist_${i}_${s.id}" }) { _, song ->
                    RadioSongRow(song)
                }
            }
        }
    }
}

@Composable
private fun ListenButton(isPlaying: Boolean, focusRequester: FocusRequester, onClick: () -> Unit) {
    var focused by remember { mutableStateOf(false) }
    val bg = when {
        isPlaying -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.primary
    }
    Row(
        modifier = Modifier
            .focusRequester(focusRequester)
            .clip(RoundedCornerShape(28.dp))
            .background(if (focused) MaterialTheme.colorScheme.primary else bg)
            .onFocusChanged { focused = it.isFocused }
            .clickable { onClick() }
            .tvFocusScale(focused, scale = 1.08f)
            .padding(horizontal = 28.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
            contentDescription = null,
            tint = if (focused || !isPlaying) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(26.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            if (isPlaying) "Stop" else "Listen Live",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (focused || !isPlaying) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun RadioSectionHeader(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    )
}

@Composable
private fun RadioSongRow(song: RadioSong) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = song.coverUrl,
            contentDescription = song.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)).background(Color.DarkGray)
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                song.title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                song.subtitle(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(Modifier.width(16.dp))
        Text(
            radioDuration(song.duration),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
