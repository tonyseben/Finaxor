package com.tonyseben.finaxor.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.tonyseben.finaxor.BuildConfig

@Composable
actual fun rememberGoogleSignInLauncher(): GoogleSignInLauncher {
    val context = LocalContext.current
    return remember {
        GoogleSignInLauncher(
            context = context,
            webClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID
        )
    }
}
