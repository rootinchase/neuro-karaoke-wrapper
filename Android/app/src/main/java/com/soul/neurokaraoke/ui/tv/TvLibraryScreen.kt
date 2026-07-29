package com.soul.neurokaraoke.ui.tv

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.soul.neurokaraoke.data.model.Playlist
import com.soul.neurokaraoke.viewmodel.PlayerViewModel

/**
 * TV Library screen: grid of setlist/playlist cover cards sourced from the same
 * data the phone SetlistScreen loads via [PlayerUiState.availablePlaylists].
 */
@Composable
fun TvLibraryScreen(
    onOpenDetail: (Playlist) -> Unit,
    playerViewModel: PlayerViewModel = viewModel()
) {
    val playerState by playerViewModel.uiState.collectAsStateWithLifecycle()
    val playlists = playerState.availablePlaylists

    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        contentPadding = PaddingValues(horizontal = 48.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(playlists, key = { it.id }) { playlist ->
            var focused by remember { mutableStateOf(false) }
            Column(
                Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focused = it.isFocused }
                    .focusable()
                    .onKeyEvent {
                        if (it.type == KeyEventType.KeyUp &&
                            (it.key == Key.Enter || it.key == Key.DirectionCenter)
                        ) {
                            onOpenDetail(playlist); true
                        } else false
                    }
                    .tvFocusScale(focused)
            ) {
                AsyncImage(
                    model = playlist.coverUrl,
                    contentDescription = playlist.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(12.dp))
                )
                Text(
                    playlist.title,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1
                )
                val songCount = if (playlist.songCount > 0) playlist.songCount else playlist.songs.size
                Text(
                    "$songCount songs",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
