package com.soul.neurokaraoke.ui.tv

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    TabRow(selectedTabIndex = selected.ordinal, modifier = modifier) {
        TvTab.entries.forEach { tab ->
            Tab(
                selected = tab == selected,
                onFocus = { onSelect(tab) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(tab.label, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }
        }
    }
}
