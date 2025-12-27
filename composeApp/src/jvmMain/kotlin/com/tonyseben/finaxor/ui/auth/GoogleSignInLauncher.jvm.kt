package com.tonyseben.finaxor.ui.auth

import com.tonyseben.finaxor.core.AppError
import com.tonyseben.finaxor.core.Result

/**
 * JVM/Desktop stub - Google Sign-In not supported.
 * Desktop authentication would require an embedded browser OAuth flow.
 */
actual class GoogleSignInLauncher {
    actual suspend fun signIn(): Result<GoogleSignInResult> {
        return Result.Error(
            AppError.BusinessError("Google Sign-In is not supported on Desktop. Please use mobile app.")
        )
    }
}
