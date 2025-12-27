package com.tonyseben.finaxor.ui.auth

import com.tonyseben.finaxor.core.AppError
import com.tonyseben.finaxor.core.Result

/**
 * iOS implementation - requires native Swift integration.
 *
 * To complete iOS Google Sign-In:
 * 1. Create a Swift helper class that wraps GIDSignIn
 * 2. Expose it to Kotlin via @objc
 * 3. Call it from here
 *
 * For now, returns an error prompting native implementation.
 */
actual class GoogleSignInLauncher {

    actual suspend fun signIn(): Result<GoogleSignInResult> {
        // TODO: Implement via Swift interop
        // The GoogleSignIn SDK is available via CocoaPods (already in Podfile)
        // but requires Swift/ObjC bridging to call from Kotlin
        return Result.Error(
            AppError.BusinessError("iOS Google Sign-In requires native Swift integration. See GoogleSignInLauncher.ios.kt for instructions.")
        )
    }
}
