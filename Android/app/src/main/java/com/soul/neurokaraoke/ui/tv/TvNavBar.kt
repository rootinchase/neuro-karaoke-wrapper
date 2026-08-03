package com.soul.neurokaraoke.ui.tv

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Tab
import androidx.tv.material3.TabRow
import androidx.tv.material3.Text

enum class TvTab(val label: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.Home),
    SEARCH("Search", Icons.Default.Search),
    LIBRARY("Library", Icons.Default.LibraryMusic),
    RADIO("Radio", Icons.Default.Radio),
    VIDEOS("Videos", Icons.Default.Movie),
    ACCOUNT("Account", Icons.Default.AccountCircle),
    NOW_PLAYING("Now Playing", Icons.Default.PlayArrow)
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvNavBar(
    selected: TvTab,
    onSelect: (TvTab) -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = remember { FocusRequester() }
) {
    // Nothing in the tv-material3 TabRow/Tab grabs default D-pad focus on its own, so on a
    // fresh launch the emulator's D-pad has no starting point and every key press is a no-op.
    // Request focus once (keyed on Unit, not on `selected`) so this fires exactly one time per
    // TvNavBar composition and never fights the user by re-grabbing focus on later tab switches.
    // `focusRequester` is hoistable so callers (TvApp) can re-trigger it later, e.g. to restore
    // focus after the playlist-detail overlay closes.
    LaunchedEffect(Unit) {
        runCatching { focusRequester.requestFocus() }
    }

    // Group the tabs into one centered translucent pill (tvOS-style floating nav bar).
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .clip(RoundedCornerShape(percent = 50))
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.4f))
                .padding(horizontal = 6.dp, vertical = 4.dp)
        ) {
            TabRow(selectedTabIndex = selected.ordinal) {
                TvTab.entries.forEach { tab ->
                    Tab(
                        selected = tab == selected,
                        onFocus = { onSelect(tab) },
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .let { base -> if (tab == selected) base.focusRequester(focusRequester) else base }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(tab.label)
                        }
                    }
                }
            }
        }
    }
}
