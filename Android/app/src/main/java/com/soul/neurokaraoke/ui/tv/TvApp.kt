package com.soul.neurokaraoke.ui.tv

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.soul.neurokaraoke.data.model.Playlist
import com.soul.neurokaraoke.viewmodel.PlayerViewModel

@Composable
fun TvApp(playerViewModel: PlayerViewModel) {
    var tab by remember { mutableStateOf(TvTab.HOME) }
    // Holds the playlist the user drilled into from a rail's cover card.
    // Not yet rendered — detail navigation lands in Task 8.
    var selectedPlaylist by remember { mutableStateOf<Playlist?>(null) }

    Column(Modifier.fillMaxSize()) {
        TvNavBar(selected = tab, onSelect = { tab = it }, modifier = Modifier.padding(24.dp))
        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
            when (tab) {
                TvTab.HOME -> TvHomeScreen(
                    onPlay = { song -> playerViewModel.playSong(song) },
                    onOpenDetail = { playlist -> selectedPlaylist = playlist },
                    playerViewModel = playerViewModel
                )
                else -> Text(tab.label, style = MaterialTheme.typography.headlineMedium)
            }
        }
    }
}
