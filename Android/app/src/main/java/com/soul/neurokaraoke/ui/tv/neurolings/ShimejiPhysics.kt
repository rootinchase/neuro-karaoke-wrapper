package com.soul.neurokaraoke.ui.tv.neurolings

object ShimejiPhysics {
    const val TICK_MS = 40L
    const val GRAVITY = 2.0
    const val DRAG_X = 0.05
    const val DRAG_Y = 0.1
    const val MAX_VEL_Y = 20.0

    data class Vel(val x: Double, val y: Double)

    /** One falling integration step: apply drag then gravity, clamp vy to terminal. */
    fun fallStep(v: Vel): Vel {
        val vx = v.x * (1 - DRAG_X)
        val vy = minOf(v.y * (1 - DRAG_Y) + GRAVITY, MAX_VEL_Y)
        return Vel(vx, vy)
    }
}
