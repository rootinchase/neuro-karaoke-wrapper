package com.soul.neurokaraoke.ui.tv.neurolings

object NeurolingsCounts {
    const val MIN = 0
    const val MAX = 10
    val CHARACTERS = listOf("Neuron", "Weuron", "Eviling", "Vedaling", "Cerber", "Tuteling")

    fun clamp(n: Int): Int = n.coerceIn(MIN, MAX)

    fun step(current: Int, delta: Int): Int = clamp(current + delta)

    fun serialize(counts: Map<String, Int>): String {
        return counts
            .filter { (_, count) -> count > 0 }
            .map { (name, count) -> "$name=$count" }
            .joinToString(";")
    }

    fun parse(s: String?): Map<String, Int> {
        if (s.isNullOrEmpty()) return emptyMap()
        return s.split(";")
            .mapNotNull { entry ->
                val parts = entry.split("=")
                if (parts.size == 2) {
                    val name = parts[0]
                    val countStr = parts[1]
                    val count = countStr.toIntOrNull()
                    if (name in CHARACTERS && count != null) {
                        val clamped = clamp(count)
                        if (clamped > 0) {
                            name to clamped
                        } else {
                            null
                        }
                    } else {
                        null
                    }
                } else {
                    null
                }
            }
            .toMap()
    }

    fun total(counts: Map<String, Int>): Int = counts.values.sum()
}
