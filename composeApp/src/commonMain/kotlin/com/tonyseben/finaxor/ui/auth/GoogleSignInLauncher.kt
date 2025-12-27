package com.tonyseben.finaxor.ui.auth

import com.tonyseben.finaxor.core.Result

/**
 * Platform-specific Google Sign-In launcher result.
 */
data class GoogleSignInResult(
    val idToken: String,
    val accessToken: String? = null
)

/**
 * Expected platform-specific Google Sign-In launcher.
 * Each platform provides its own implementation using native Google Sign-In SDK.
 *
 * This belongs in the presentation layer because:
 * - It requires UI context (Activity on Android, ViewController on iOS)
 * - It triggers a user-facing sign-in flow
 * - It's invoked by ViewModels/screens before calling use cases
 */
expect class GoogleSignInLauncher {
    /**
     * Launch the native Google Sign-In flow.
     * Returns the tokens needed for Firebase Auth.
     */
    suspend fun signIn(): Result<GoogleSignInResult>
}
