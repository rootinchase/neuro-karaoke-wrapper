package com.soul.neurokaraoke.ui.tv.neurolings

/**
 * Immutable data model for a parsed Shimeji mascot pack (actions.xml + behaviors.xml).
 *
 * No Android APIs are used here so this can be unit-tested on the plain JVM.
 */

data class Pose(
    val image: String,
    val anchorX: Int,
    val anchorY: Int,
    val velX: Int,
    val velY: Int,
    val duration: Int,
)

data class Animation(val condition: String?, val poses: List<Pose>)

enum class BorderType { FLOOR, WALL, CEILING, NONE }

/** A reference to another action by name, with optional per-use overrides. */
data class ActionRef(val name: String, val overrides: Map<String, String>) // Duration/InitialVX/InitialVY/Condition

sealed interface ShimejiAction {
    val name: String?
}

data class StayAction(
    override val name: String?,
    val border: BorderType,
    val animations: List<Animation>,
) : ShimejiAction

data class MoveAction(
    override val name: String?,
    val border: BorderType,
    val animations: List<Animation>,
) : ShimejiAction

data class AnimateAction(
    override val name: String?,
    val border: BorderType,
    val animations: List<Animation>,
) : ShimejiAction

data class SequenceAction(
    override val name: String?,
    val loop: Boolean,
    val refs: List<ActionRef>,
) : ShimejiAction

/** children: (condition, ref) pairs, in order. */
data class SelectAction(
    override val name: String?,
    val children: List<Pair<String?, ActionRef>>,
) : ShimejiAction

data class EmbeddedAction(
    override val name: String?,
    val className: String,
) : ShimejiAction

data class BehaviorRef(val name: String, val frequency: Int, val condition: String?, val hidden: Boolean)

data class NextBehaviorList(val add: Boolean, val refs: List<BehaviorRef>)

data class Behavior(
    val name: String,
    val frequency: Int,
    val hidden: Boolean,
    val condition: String?,
    val next: NextBehaviorList?,
)

data class MascotSet(
    val name: String,
    val actions: Map<String, ShimejiAction>,
    val behaviors: List<Behavior>,
    val imgDir: String,
)
