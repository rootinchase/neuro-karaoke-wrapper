package com.soul.neurokaraoke.data.util

import com.soul.neurokaraoke.data.model.Song
import kotlin.random.Random

/**
 * Random song selection, ported from Twinskaraoke (iOS) RandomSongsViewModel.
 * Pure and Android-free so it can be unit tested on the JVM.
 */
object RandomSongs {

    const val DEFAULT_COUNT = 30

    /**
     * Pick up to [count] distinct random songs from [all]. Prefers songs with a
     * playable audio url; falls back to the full list only if none are playable.
     */
    fun pick(
        all: List<Song>,
        count: Int = DEFAULT_COUNT,
        random: Random = Random.Default
    ): List<Song> {
        if (all.isEmpty() || count <= 0) return emptyList()
        val playable = all.filter { it.audioUrl.isNotBlank() }
        val pool = playable.ifEmpty { all }
        return pool.shuffled(random).take(count.coerceAtMost(pool.size))
    }
}
