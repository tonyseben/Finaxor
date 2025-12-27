package com.tonyseben.finaxor.ui.auth

import com.tonyseben.finaxor.core.AppError
import com.tonyseben.finaxor.core.Result

/**
 * iOS implementation placeholder.
 * Requires GoogleSignIn CocoaPod to be configured.
 * TODO: Implement with GIDSignIn when CocoaPods are set up.
 */
actual class GoogleSignInLauncher(
    private val clientId: String
) {
    actual suspend fun signIn(): Result<GoogleSignInResult> {
        // TODO: Implement iOS Google Sign-In using GIDSignIn
        // This requires:
        // 1. Add GoogleSignIn pod to Podfile
        // 2. Configure GoogleService-Info.plist
        // 3. Use GIDSignIn.sharedInstance to trigger sign-in flow
        return Result.Error(
            AppError.BusinessError("iOS Google Sign-In not yet implemented. Configure GoogleSignIn pod first.")
        )
    }
}
