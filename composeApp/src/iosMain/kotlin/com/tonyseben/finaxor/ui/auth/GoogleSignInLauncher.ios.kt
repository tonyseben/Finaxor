package com.tonyseben.finaxor.ui.auth

import cocoapods.GoogleSignIn.GIDConfiguration
import cocoapods.GoogleSignIn.GIDSignIn
import com.tonyseben.finaxor.core.AppError
import com.tonyseben.finaxor.core.Result
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSBundle
import platform.Foundation.NSDictionary
import platform.Foundation.dictionaryWithContentsOfFile
import platform.UIKit.UIApplication
import kotlin.coroutines.resume

/**
 * iOS implementation using GoogleSignIn SDK.
 * Reads CLIENT_ID from GoogleService-Info.plist.
 */
@OptIn(ExperimentalForeignApi::class)
actual class GoogleSignInLauncher {

    private val clientId: String by lazy {
        val path = NSBundle.mainBundle.pathForResource("GoogleService-Info", "plist")
        val plist = path?.let { NSDictionary.dictionaryWithContentsOfFile(it) }
        plist?.objectForKey("CLIENT_ID") as? String
            ?: error("CLIENT_ID not found in GoogleService-Info.plist")
    }

    actual suspend fun signIn(): Result<GoogleSignInResult> = suspendCancellableCoroutine { continuation ->
        val config = GIDConfiguration(clientID = clientId)
        GIDSignIn.sharedInstance.configuration = config

        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        if (rootViewController == null) {
            continuation.resume(
                Result.Error(AppError.AuthError("No root view controller available"))
            )
            return@suspendCancellableCoroutine
        }

        GIDSignIn.sharedInstance.signInWithPresentingViewController(rootViewController) { result, error ->
            when {
                error != null -> {
                    continuation.resume(
                        Result.Error(AppError.AuthError(error.localizedDescription ?: "Sign-in failed"))
                    )
                }
                result?.user?.idToken != null -> {
                    continuation.resume(
                        Result.Success(
                            GoogleSignInResult(
                                idToken = result.user.idToken!!.tokenString,
                                accessToken = result.user.accessToken.tokenString
                            )
                        )
                    )
                }
                else -> {
                    continuation.resume(
                        Result.Error(AppError.AuthError("Sign-in cancelled or no token received"))
                    )
                }
            }
        }
    }
}
