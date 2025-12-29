package com.tonyseben.finaxor.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberGoogleSignInLauncher(): GoogleSignInLauncher {
    return remember { GoogleSignInLauncher() }
}
