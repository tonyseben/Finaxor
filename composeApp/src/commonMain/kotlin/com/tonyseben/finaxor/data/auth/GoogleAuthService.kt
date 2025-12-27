package com.tonyseben.finaxor.data.auth

import com.tonyseben.finaxor.core.AppError
import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.data.mapper.toAuthUser
import com.tonyseben.finaxor.domain.model.AuthUser
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.GoogleAuthProvider

/**
 * Google Sign-In authentication service.
 */
class GoogleAuthService(
    private val idToken: String,
    private val accessToken: String?
) : AuthService {

    override suspend fun authenticate(auth: FirebaseAuth): Result<AuthUser> {
        return try {
            val credential = GoogleAuthProvider.credential(idToken, accessToken)
            val authResult = auth.signInWithCredential(credential)
            val firebaseUser = authResult.user

            if (firebaseUser != null) {
                Result.Success(firebaseUser.toAuthUser())
            } else {
                Result.Error(AppError.AuthError("Sign-in succeeded but no user returned"))
            }
        } catch (e: Exception) {
            Result.Error(AppError.AuthError(e.message ?: "Google sign-in failed", e))
        }
    }
}
