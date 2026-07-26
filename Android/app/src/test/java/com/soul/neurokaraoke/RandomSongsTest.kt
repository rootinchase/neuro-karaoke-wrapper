package com.soul.neurokaraoke

import com.soul.neurokaraoke.data.model.Song
import com.soul.neurokaraoke.data.util.RandomSongs
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class RandomSongsTest {

    private fun song(id: String, audio: String = "http://a/$id.mp3") =
        Song(id = id, title = "T$id", artist = "A", coverUrl = "", audioUrl = audio)

    private val pool = (1..100).map { song("s$it") }

    @Test
    fun pick_returns_requested_count() {
        assertEquals(30, RandomSongs.pick(pool, 30, Random(1)).size)
    }

    @Test
    fun pick_returns_distinct_songs() {
        val result = RandomSongs.pick(pool, 30, Random(1))
        assertEquals(result.size, result.map { it.id }.toSet().size)
    }

    @Test
    fun pick_caps_at_pool_size() {
        val small = pool.take(5)
        assertEquals(5, RandomSongs.pick(small, 30, Random(1)).size)
    }

    @Test
    fun pick_is_deterministic_for_same_seed() {
        assertEquals(
            RandomSongs.pick(pool, 10, Random(42)).map { it.id },
            RandomSongs.pick(pool, 10, Random(42)).map { it.id }
        )
    }

    @Test
    fun pick_prefers_playable_songs() {
        val mixed = listOf(song("a", ""), song("b"), song("c", ""), song("d"))
        val result = RandomSongs.pick(mixed, 4, Random(1))
        // Only b and d have audio urls; unplayable ones excluded.
        assertEquals(setOf("b", "d"), result.map { it.id }.toSet())
    }

    @Test
    fun pick_empty_input_is_empty() {
        assertTrue(RandomSongs.pick(emptyList(), 10).isEmpty())
    }
}
