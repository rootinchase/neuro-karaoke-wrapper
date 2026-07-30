package com.soul.neurokaraoke.ui.tv

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.soul.neurokaraoke.data.model.Playlist
import com.soul.neurokaraoke.data.repository.SettingsRepository
import com.soul.neurokaraoke.viewmodel.AuthViewModel
import com.soul.neurokaraoke.viewmodel.PlayerViewModel

@Composable
fun TvApp(playerViewModel: PlayerViewModel, authViewModel: AuthViewModel) {
    var tab by remember { mutableStateOf(TvTab.HOME) }
    // Holds the playlist the user drilled into from a rail's cover card.
    var selectedPlaylist by remember { mutableStateOf<Playlist?>(null) }
    // Immersive fullscreen for Radio / Now Playing — hides the nav bar so art +
    // lyrics fill the screen. Back exits it.
    var fullscreen by remember { mutableStateOf(false) }
    BackHandler(enabled = fullscreen) { fullscreen = false }

    // Settings overlay (corner gear). Gated the same way as the detail overlay.
    var showSettings by remember { mutableStateOf(false) }
    val gearFocusRequester = remember { FocusRequester() }
    // Only re-grab focus onto the gear when the panel actually *closes* (not on first
    // composition, where a nav tab should hold focus instead).
    var settingsWasOpen by remember { mutableStateOf(false) }
    LaunchedEffect(showSettings) {
        if (showSettings) {
            settingsWasOpen = true
        } else if (settingsWasOpen) {
            settingsWasOpen = false
            runCatching { gearFocusRequester.requestFocus() }
        }
    }
    val accessToken = authViewModel.getAccessToken()

    // Dev-only walking mascots, shown over Radio / Now Playing when enabled in Developer Options.
    val neurolingsEnabled by SettingsRepository.neurolingsEnabled.collectAsStateWithLifecycle()

    // Hoisted so TvApp can re-request nav focus after the detail overlay closes (TvNavBar
    // still does its own one-shot initial-focus request on first composition).
    val navFocusRequester = remember { FocusRequester() }

    // The detail overlay (TvDetailScreen) owns its own initial focus on its Play button when it
    // opens (selectedPlaylist becomes non-null), so this effect intentionally does nothing then —
    // it only fires on the transition back to null, i.e. when the overlay's BackHandler closes it
    // and nothing else in the tree still holds D-pad focus.
    LaunchedEffect(selectedPlaylist) {
        if (selectedPlaylist == null) {
            runCatching { navFocusRequester.requestFocus() }
        }
    }

    // TvApp is the TV entry point — unlike the phone, there is no NavGraph
    // composable(Screen.Search.route) { LaunchedEffect(Unit) { onLoadAllSongs() } }
    // to trigger this, so Home's "Trending"/"New Releases" rails and the Search
    // screen would otherwise stay empty forever on a fresh install.
    // availablePlaylists doesn't need a matching call here: PlayerViewModel.init()
    // already calls loadAvailablePlaylists() unconditionally on construction.
    LaunchedEffect(Unit) {
        playerViewModel.loadAllSongs()
    }

    Box(Modifier.fillMaxSize()) {
        // tvOS-style immersive backdrop: the current song's cover art, blurred and
        // dimmed, sits behind the whole UI so it doesn't read as flat black.
        TvImmersiveBackground(playerViewModel)

        Column(
            Modifier
                .fillMaxSize()
                .focusProperties { canFocus = selectedPlaylist == null && !showSettings }
                .focusGroup()
        ) {
            if (!fullscreen) {
                Box(Modifier.fillMaxWidth().padding(24.dp)) {
                    TvNavBar(
                        selected = tab,
                        onSelect = { tab = it },
                        focusRequester = navFocusRequester
                    )
                    TvSettingsButton(
                        onClick = { showSettings = true },
                        focusRequester = gearFocusRequester,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }
            }
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                when (tab) {
                    TvTab.HOME -> TvHomeScreen(
                        onPlay = { song -> playerViewModel.playSong(song) },
                        onOpenDetail = { playlist -> selectedPlaylist = playlist },
                        playerViewModel = playerViewModel
                    )
                    TvTab.LIBRARY -> TvLibraryScreen(
                        onOpenDetail = { playlist -> selectedPlaylist = playlist },
                        playerViewModel = playerViewModel
                    )
                    TvTab.NOW_PLAYING -> TvNowPlayingScreen(
                        playerViewModel = playerViewModel,
                        accessToken = accessToken,
                        fullscreen = fullscreen,
                        onToggleFullscreen = { fullscreen = !fullscreen }
                    )
                    TvTab.SEARCH -> TvSearchScreen(
                        onPlay = { song, results -> playerViewModel.playSongWithQueue(song, results) },
                        playerViewModel = playerViewModel
                    )
                    TvTab.RADIO -> TvRadioScreen(
                        playerViewModel = playerViewModel,
                        accessToken = accessToken,
                        fullscreen = fullscreen,
                        onToggleFullscreen = { fullscreen = !fullscreen }
                    )
                    TvTab.ACCOUNT -> TvAccountScreen(authViewModel = authViewModel)
                }
            }
        }

        // For-fun mascots walk on top of Radio / Now Playing (dev-only). Purely visual — no focus,
        // no input capture — and drawn under the settings/detail overlays so it never covers them.
        if (neurolingsEnabled && (tab == TvTab.RADIO || tab == TvTab.NOW_PLAYING)) {
            TvNeurolings()
        }

        selectedPlaylist?.let { playlist ->
            Box(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                TvImmersiveBackground(playerViewModel)
                TvDetailScreen(
                    playlist = playlist,
                    onPlayAll = { songs -> songs.firstOrNull()?.let { playerViewModel.playSongWithQueue(it, songs) } },
                    onPlaySong = { song, songs -> playerViewModel.playSongWithQueue(song, songs) },
                    onBack = { selectedPlaylist = null }
                )
            }
        }

        if (showSettings) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                TvImmersiveBackground(playerViewModel)
                TvSettingsScreen(onClose = { showSettings = false })
            }
        }
    }
}

/**
 * Full-screen ambient backdrop: the now-playing cover art, heavily blurred and
 * covered by a light-to-medium scrim so foreground text and the nav bar stay
 * legible while the UI still reads as a rich tvOS-style surface rather than flat
 * black. Collects player state in its own composable so only this layer — not the
 * nav bar or content — recomposes on playback ticks. (blur is a no-op below API 31;
 * the scrim still applies, so it degrades gracefully to a dimmed cover.)
 */
@Composable
private fun TvImmersiveBackground(playerViewModel: PlayerViewModel) {
    val playerState by playerViewModel.uiState.collectAsStateWithLifecycle()
    val cover = playerState.currentSong?.coverUrl
    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (!cover.isNullOrBlank()) {
            AsyncImage(
                model = cover,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().blur(80.dp)
            )
        }
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.background.copy(alpha = 0.50f),
                            MaterialTheme.colorScheme.background.copy(alpha = 0.80f)
                        )
                    )
                )
        )
    }
}
