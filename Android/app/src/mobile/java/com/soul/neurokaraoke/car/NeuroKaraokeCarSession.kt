package com.soul.neurokaraoke.car

import android.content.Intent
import android.support.v4.media.session.MediaSessionCompat
import androidx.car.app.Screen
import androidx.car.app.Session
import androidx.car.app.media.MediaPlaybackManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.soul.neurokaraoke.data.repository.LocaleManager
import com.soul.neurokaraoke.service.GlobalMediaToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

class NeuroKaraokeCarSession : Session() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var homeScreen: HomeCarScreen? = null

    @androidx.annotation.OptIn(androidx.car.app.annotations.ExperimentalCarApi::class)
    override fun onCreateScreen(intent: Intent): Screen {
        val screen = HomeCarScreen(carContext)
        homeScreen = screen

        // Register the media session token with Car App Library.
        // This is required for MediaPlaybackTemplate (Now Playing screen) to work.
        GlobalMediaToken.token?.let { token ->
            try {
                val playbackManager = carContext.getCarService(MediaPlaybackManager::class.java)
                playbackManager.registerMediaPlaybackToken(token)
            } catch (_: Exception) {}
        }

        // Observe locale changes and invalidate car templates so
        // getString() calls re-resolve to the new language.
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                scope.launch {
                    LocaleManager.currentLanguage
                        .drop(1)
                        .collect {
                            // Invalidate home screen (rebuilds tab/grid templates)
                            homeScreen?.invalidateOnMain()
                            // Also invalidate the currently visible screen if it's different
                            // from home (e.g., PlaylistDetailCarScreen)
                            try {
                                val top = carContext.getCarService(
                                    androidx.car.app.ScreenManager::class.java
                                ).top
                                if (top != homeScreen) {
                                    top.invalidate()
                                }
                            } catch (_: Exception) {
                                // ScreenManager access may fail — home invalidation is sufficient
                            }
                        }
                }
            }

            override fun onDestroy(owner: LifecycleOwner) {
                scope.cancel()
            }
        })

        return screen
    }
}
