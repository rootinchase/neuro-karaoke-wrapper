package com.soul.neurokaraoke.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.soul.neurokaraoke.data.model.Singer
import com.soul.neurokaraoke.data.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

/**
 * Tracks recently played songs, most-recent first, deduped by id and capped.
 * Singleton, mirroring [SettingsRepository]. Ported from Twinskaraoke (iOS)
 * RecentlyPlayedStore.
 */
object RecentlyPlayedStore {

    const val MAX_ENTRIES = 50

    private var prefs: SharedPreferences? = null

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs.asStateFlow()

    @Synchronized
    fun initialize(context: Context) {
        if (prefs != null) return
        prefs = context.applicationContext
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        _songs.value = load()
    }

    /** Record a song as just-played. No-op for songs without an id. */
    fun record(song: Song) {
        if (song.id.isBlank()) return
        val updated = merge(_songs.value, song, MAX_ENTRIES)
        _songs.value = updated
        save(updated)
    }

    fun clear() {
        _songs.value = emptyList()
        prefs?.edit()?.remove(KEY_SONGS)?.apply()
    }

    /**
     * Pure ordering logic: prepend [played], drop any prior entry with the same
     * id, cap at [max]. Android-free so it can be unit tested on the JVM.
     */
    fun merge(current: List<Song>, played: Song, max: Int): List<Song> {
        if (max <= 0) return emptyList()
        val withoutDup = current.filterNot { it.id == played.id }
        return (listOf(played) + withoutDup).take(max)
    }

    private fun load(): List<Song> {
        val json = prefs?.getString(KEY_SONGS, null) ?: return emptyList()
        return try {
            val arr = JSONArray(json)
            (0 until arr.length()).map { parseSong(arr.getJSONObject(it)) }
        } catch (e: Exception) {
            if (com.soul.neurokaraoke.BuildConfig.DEBUG) e.printStackTrace()
            emptyList()
        }
    }

    private fun save(songs: List<Song>) {
        val arr = JSONArray()
        songs.forEach { arr.put(songToJson(it)) }
        prefs?.edit()?.putString(KEY_SONGS, arr.toString())?.apply()
    }

    private fun parseSong(json: JSONObject): Song = Song(
        id = json.getString("id"),
        title = json.getString("title"),
        artist = json.getString("artist"),
        coverUrl = json.optString("coverUrl", ""),
        audioUrl = json.optString("audioUrl", ""),
        duration = json.optLong("duration", 0L),
        singer = try {
            Singer.valueOf(json.optString("singer", "NEURO"))
        } catch (e: Exception) {
            Singer.NEURO
        },
        coverArtists = json.optString("coverArtists", ""),
        artCredit = json.optString("artCredit", "").takeIf { it.isNotBlank() }
    )

    private fun songToJson(song: Song): JSONObject = JSONObject().apply {
        put("id", song.id)
        put("title", song.title)
        put("artist", song.artist)
        put("coverUrl", song.coverUrl)
        put("audioUrl", song.audioUrl)
        put("duration", song.duration)
        put("singer", song.singer.name)
        put("coverArtists", song.coverArtists)
        put("artCredit", song.artCredit ?: "")
    }

    private const val PREFS_NAME = "recently_played"
    private const val KEY_SONGS = "songs"
}
