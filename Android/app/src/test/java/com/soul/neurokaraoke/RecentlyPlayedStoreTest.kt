package com.soul.neurokaraoke

import com.soul.neurokaraoke.data.model.Song
import com.soul.neurokaraoke.data.repository.RecentlyPlayedStore
import org.junit.Assert.assertEquals
import org.junit.Test

class RecentlyPlayedStoreTest {

    private fun song(id: String) = Song(id = id, title = "T$id", artist = "A", coverUrl = "")

    @Test
    fun merge_prepends_played_song() {
        val result = RecentlyPlayedStore.merge(listOf(song("a"), song("b")), song("c"), 50)
        assertEquals(listOf("c", "a", "b"), result.map { it.id })
    }

    @Test
    fun merge_deduplicates_by_id_moving_to_front() {
        val result = RecentlyPlayedStore.merge(listOf(song("a"), song("b"), song("c")), song("b"), 50)
        assertEquals(listOf("b", "a", "c"), result.map { it.id })
    }

    @Test
    fun merge_caps_at_max() {
        val current = (1..50).map { song("s$it") }
        val result = RecentlyPlayedStore.merge(current, song("new"), 50)
        assertEquals(50, result.size)
        assertEquals("new", result.first().id)
        // Oldest entry (s50) dropped after prepend + cap.
        assertEquals(false, result.any { it.id == "s50" })
    }

    @Test
    fun merge_with_nonpositive_max_is_empty() {
        assertEquals(emptyList<Song>(), RecentlyPlayedStore.merge(listOf(song("a")), song("b"), 0))
    }
}
