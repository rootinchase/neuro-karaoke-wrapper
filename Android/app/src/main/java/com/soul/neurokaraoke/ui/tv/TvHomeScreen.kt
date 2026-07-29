package com.soul.neurokaraoke.ui.tv

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.soul.neurokaraoke.data.model.Playlist
import com.soul.neurokaraoke.data.model.Song
import com.soul.neurokaraoke.data.repository.RecentlyPlayedStore
import com.soul.neurokaraoke.data.repository.SongRepository
import com.soul.neurokaraoke.viewmodel.PlayerViewModel

@Composable
fun TvRail(title: String, songs: List<Song>, onPlay: (Song) -> Unit) {
    Column(Modifier.padding(vertical = 12.dp)) {
        Text(
            title, style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(start = 48.dp, bottom = 8.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(songs, key = { it.id }) { song ->
                var focused by remember { mutableStateOf(false) }
                Column(
                    Modifier
                        .width(180.dp)
                        .onFocusChanged { focused = it.isFocused }
                        .focusable()
                        .onKeyEvent {
                            if (it.type == KeyEventType.KeyUp &&
                                (it.key == Key.Enter || it.key == Key.DirectionCenter)
                            ) {
                                onPlay(song); true
                            } else false
                        }
                        .tvFocusScale(focused)
                ) {
                    AsyncImage(
                        model = song.coverUrl, contentDescription = song.title,
                        modifier = Modifier.size(180.dp).clip(RoundedCornerShape(12.dp))
                    )
                    if (focused) {
                        Text(song.title, style = MaterialTheme.typography.bodyLarge, maxLines = 1)
                        Text(song.artist, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
                    }
                }
            }
        }
    }
}

/**
 * TV Home screen: rails sourced from the same data the phone Home screen
 * has access to via [PlayerViewModel] — there is no dedicated "trending" or
 * "new releases" API, so those rails are derived from [PlayerUiState.allSongs]
 * and the newest setlist in [PlayerUiState.availablePlaylists].
 */
@Composable
fun TvHomeScreen(
    onPlay: (Song) -> Unit,
    onOpenDetail: (Playlist) -> Unit,
    playerViewModel: PlayerViewModel = viewModel()
) {
    val playerState by playerViewModel.uiState.collectAsStateWithLifecycle()
    val recentlyPlayed by RecentlyPlayedStore.songs.collectAsStateWithLifecycle()
    val songRepository = remember { SongRepository() }

    val allSongs = playerState.allSongs
    val newestPlaylist = remember(playerState.availablePlaylists) {
        playerState.availablePlaylists.firstOrNull()
    }
    var newReleases by remember { mutableStateOf<List<Song>>(emptyList()) }

    LaunchedEffect(newestPlaylist?.id) {
        val playlistId = newestPlaylist?.id
        newReleases = if (playlistId != null) {
            songRepository.getPlaylistSongs(playlistId).getOrNull() ?: allSongs.takeLast(20)
        } else {
            allSongs.takeLast(20)
        }
    }

    val trending = remember(allSongs) { allSongs.take(20) }

    LazyColumn(Modifier.fillMaxSize()) {
        if (recentlyPlayed.isNotEmpty()) {
            item {
                // Scope playback to the Recently Played list itself (matches the phone's
                // ui/screens/library/RecentlyPlayedScreen.kt, which uses playSongWithQueue
                // so "up next" stays within recents instead of falling back to allSongs).
                TvRail(
                    title = "Recently Played",
                    songs = recentlyPlayed,
                    onPlay = { song -> playerViewModel.playSongWithQueue(song, recentlyPlayed) }
                )
            }
        }
        item {
            TvRail(title = "Trending", songs = trending, onPlay = onPlay)
        }
        item {
            TvRail(title = "New Releases", songs = newReleases, onPlay = onPlay)
        }
    }
}
