package com.soul.neurokaraoke

import com.soul.neurokaraoke.data.api.LyricLine
import com.soul.neurokaraoke.data.api.LyricsApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LyricsTranslationTest {

    private val lines = listOf(
        LyricLine(0, "挙げ句の果て静脈を刺しちゃって"),
        LyricLine(1000, "病弱な愛が飛び出すもんで"),
        LyricLine(2000, "")
    )

    @Test
    fun merge_fills_translated_text_when_counts_match() {
        val out = LyricsApi.mergeTranslations(
            lines,
            listOf("Ended up stabbing through my veins", "So a sickly love bursts out", "")
        )
        assertEquals("Ended up stabbing through my veins", out[0].translatedText)
        assertEquals("So a sickly love bursts out", out[1].translatedText)
        assertNull(out[2].translatedText) // blank translation ignored
    }

    @Test
    fun merge_returns_unchanged_on_count_mismatch() {
        val out = LyricsApi.mergeTranslations(lines, listOf("only one"))
        assertEquals(lines, out)
    }

    @Test
    fun merge_ignores_translation_identical_to_source() {
        val out = LyricsApi.mergeTranslations(
            listOf(LyricLine(0, "Hello")),
            listOf("Hello")
        )
        assertNull(out[0].translatedText)
    }
}
