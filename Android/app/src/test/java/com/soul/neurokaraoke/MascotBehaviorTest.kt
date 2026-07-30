package com.soul.neurokaraoke
import com.soul.neurokaraoke.ui.tv.neurolings.*
import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

class MascotBehaviorTest {
    private fun anim(img: String) = Animation(null, listOf(Pose(img, 64, 128, 0, 0, 10)))
    private val set = MascotSet(
        name = "T",
        actions = mapOf(
            "Stand" to StayAction("Stand", BorderType.FLOOR, listOf(anim("/s.png"))),
            "Walk" to MoveAction("Walk", BorderType.FLOOR, listOf(Animation(null, listOf(Pose("/w.png",64,128,-2,0,7))))),
            "ChaseMouse" to StayAction("ChaseMouse", BorderType.FLOOR, listOf(anim("/c.png")))
        ),
        behaviors = listOf(
            Behavior("Stand", 200, false, "#{mascot.environment.floor.isOn(mascot.anchor)}", null),
            Behavior("Walk", 100, false, "#{mascot.environment.floor.isOn(mascot.anchor)}", null),
            // Mouse behavior: condition references cursor -> MISSING -> never chosen
            Behavior("ChaseMouse", 10000, false, "#{mascot.environment.cursor.x > 0}", null)
        ),
        imgDir = "x"
    )

    @Test fun mouse_behavior_never_selected_and_floor_behaviors_are() {
        val env = ShimejiEnvironment(0.0, 0.0, 1000.0, 500.0)
        val m = Mascot(set, 500.0, 500.0, Random(42))   // on the floor (y == bottom)
        val picks = (1..200).map { m.selectBehavior(env, totalCount = 1) }.toSet()
        assertTrue("Stand" in picks)
        assertTrue("Walk" in picks)
        assertFalse("ChaseMouse" in picks)   // cursor condition is MISSING -> false
    }
}
