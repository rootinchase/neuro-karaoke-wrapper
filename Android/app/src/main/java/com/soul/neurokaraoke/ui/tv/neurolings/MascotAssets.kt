package com.soul.neurokaraoke.ui.tv.neurolings

import android.content.res.AssetManager
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

/**
 * Android-only glue: loads a Shimeji mascot pack from `assets/mascots/<name>/` and decodes/
 * caches its frame bitmaps.
 */
class MascotAssets(private val assets: AssetManager) {

    private val frameCache = HashMap<String, ImageBitmap>()

    /** Load & parse a pack from assets/mascots/<name>/. */
    fun loadSet(name: String): MascotSet {
        val actionsStream = assets.open("mascots/$name/actions.xml")
        val behaviorsStream = assets.open("mascots/$name/behaviors.xml")
        return actionsStream.use { actions ->
            behaviorsStream.use { behaviors ->
                ShimejiXmlLoader.parse(name, "mascots/$name/img", actions, behaviors)
            }
        }
    }

    /** Decode a frame PNG (cached) as an ImageBitmap. image is like "/shime1.png". */
    fun frame(setName: String, image: String): ImageBitmap {
        val key = "$setName$image"
        frameCache[key]?.let { return it }

        val fileName = image.removePrefix("/")
        val path = "mascots/$setName/img/$fileName"
        val bitmap = assets.open(path).use { stream ->
            BitmapFactory.decodeStream(stream)
        }
        val imageBitmap = bitmap.asImageBitmap()
        frameCache[key] = imageBitmap
        return imageBitmap
    }
}
