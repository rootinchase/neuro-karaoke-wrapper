package com.soul.neurokaraoke.ui.tv.neurolings

import kotlin.random.Random

/**
 * One frame's worth of render data for a single live mascot.
 */
data class MascotRender(
    val setName: String,
    val image: String,
    val x: Double,
    val y: Double,
    val anchorX: Int,
    val anchorY: Int,
    val mirrored: Boolean,
)

/**
 * Owns the live [Mascot] population: seeds/culls to match requested per-character counts,
 * ticks every live mascot each frame, and produces a render snapshot.
 *
 * Pure Kotlin (no Android APIs) so it can be unit-tested on the plain JVM.
 */
class MascotManager(
    private val sets: Map<String, MascotSet>,
    private val rng: Random = Random.Default,
) {
    private val mascots = LinkedHashMap<String, MutableList<Mascot>>()

    /** Add/remove mascots so each character's live count matches `counts` (clamped 0..10). */
    fun sync(counts: Map<String, Int>, env: ShimejiEnvironment) {
        for ((setName, set) in sets) {
            val requested = NeurolingsCounts.clamp(counts[setName] ?: 0)
            val live = mascots.getOrPut(setName) { mutableListOf() }
            when {
                live.size < requested -> {
                    repeat(requested - live.size) {
                        val startX = env.left + rng.nextDouble() * env.width
                        live.add(Mascot(set, startX, env.bottom, rng))
                    }
                }
                live.size > requested -> {
                    val toRemove = live.size - requested
                    repeat(toRemove) { live.removeAt(live.size - 1) }
                }
            }
        }
    }

    fun tick(env: ShimejiEnvironment) {
        val totalCount = mascots.values.sumOf { it.size }
        for (live in mascots.values) {
            for (mascot in live) {
                mascot.tick(env, totalCount)
            }
        }
    }

    fun snapshot(): List<MascotRender> {
        val result = mutableListOf<MascotRender>()
        for ((setName, live) in mascots) {
            for (mascot in live) {
                val (anchorX, anchorY) = mascot.currentAnchor()
                result.add(
                    MascotRender(
                        setName = setName,
                        image = mascot.currentFrameImage(),
                        x = mascot.x,
                        y = mascot.y,
                        anchorX = anchorX,
                        anchorY = anchorY,
                        mirrored = mascot.lookRight,
                    )
                )
            }
        }
        return result
    }

    /** for tests */
    fun liveCountOf(setName: String): Int = mascots[setName]?.size ?: 0
}
