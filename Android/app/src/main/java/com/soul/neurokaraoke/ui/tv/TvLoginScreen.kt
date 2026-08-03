package com.soul.neurokaraoke.ui.tv

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.soul.neurokaraoke.ui.components.LoginCard
import com.soul.neurokaraoke.viewmodel.AuthViewModel

private const val SITE_URL = "https://neurokaraoke.com"

/**
 * TV username/password sign-in, styled as the web sign-in card ([LoginCard]). Uses
 * focusable text fields (system IME → full character set for passwords). "Continue with
 * Discord" routes to device pairing; account/legal links open the website.
 */
@Composable
fun TvLoginScreen(
    authViewModel: AuthViewModel,
    onDiscord: () -> Unit,
) {
    val context = LocalContext.current
    val state by authViewModel.uiState.collectAsStateWithLifecycle()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val usernameFocus = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        authViewModel.clearError()
        runCatching { usernameFocus.requestFocus() }
    }

    fun open(url: String) {
        runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        LoginCard(
            username = username,
            onUsername = { username = it },
            password = password,
            onPassword = { password = it },
            error = state.error,
            loading = state.isLoading,
            onSignIn = { authViewModel.loginWithPassword(username, password) },
            onDiscord = onDiscord,
            onCreateAccount = { open(SITE_URL) },
            onTerms = { open(SITE_URL) },
            onPrivacy = { open(SITE_URL) },
            usernameFocusRequester = usernameFocus,
        )
    }
}
