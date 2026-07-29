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

@Composable
fun TvApp() {
    var tab by remember { mutableStateOf(TvTab.HOME) }
    Column(Modifier.fillMaxSize()) {
        TvNavBar(selected = tab, onSelect = { tab = it }, modifier = Modifier.padding(24.dp))
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(tab.label, style = MaterialTheme.typography.headlineMedium)
        }
    }
}
