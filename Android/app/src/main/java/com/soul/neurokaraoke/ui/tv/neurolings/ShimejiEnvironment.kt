package com.soul.neurokaraoke.ui.tv.neurolings

data class Anchor(val x: Double, val y: Double)

/** Screen/work-area rect. Borders: floor=bottom, ceiling=top, walls=left|right. */
class ShimejiEnvironment(val left: Double, val top: Double, val right: Double, val bottom: Double) {
    val width get() = right - left
    val height get() = bottom - top

    fun onFloor(a: Anchor, eps: Double = 1.0): Boolean = a.y >= bottom - eps

    fun onCeiling(a: Anchor, eps: Double = 1.0): Boolean = a.y <= top + eps

    fun onLeftWall(a: Anchor, eps: Double = 1.0): Boolean = a.x <= left + eps

    fun onRightWall(a: Anchor, eps: Double = 1.0): Boolean = a.x >= right - eps

    fun onWall(a: Anchor, eps: Double = 1.0): Boolean = onLeftWall(a, eps) || onRightWall(a, eps)
}
