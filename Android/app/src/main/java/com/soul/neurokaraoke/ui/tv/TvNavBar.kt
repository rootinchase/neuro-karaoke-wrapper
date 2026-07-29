package com.soul.neurokaraoke.ui.tv

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Tab
import androidx.tv.material3.TabRow
import androidx.tv.material3.Text

enum class TvTab(val label: String) {
    HOME("Home"), SEARCH("Search"), LIBRARY("Library"),
    ACCOUNT("Account"), NOW_PLAYING("Now Playing")
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvNavBar(selected: TvTab, onSelect: (TvTab) -> Unit, modifier: Modifier = Modifier) {
    // Nothing in the tv-material3 TabRow/Tab grabs default D-pad focus on its own, so on a
    // fresh launch the emulator's D-pad has no starting point and every key press is a no-op.
    // Request focus once (keyed on Unit, not on `selected`) so this fires exactly one time per
    // TvNavBar composition and never fights the user by re-grabbing focus on later tab switches.
    val initialFocusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        runCatching { initialFocusRequester.requestFocus() }
    }

    TabRow(selectedTabIndex = selected.ordinal, modifier = modifier) {
        TvTab.entries.forEach { tab ->
            Tab(
                selected = tab == selected,
                onFocus = { onSelect(tab) },
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .let { base -> if (tab == selected) base.focusRequester(initialFocusRequester) else base }
            ) {
                Text(tab.label, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }
        }
    }
}
