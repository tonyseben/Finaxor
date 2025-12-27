package com.tonyseben.finaxor.data.source.remote

import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.data.auth.AuthService
import com.tonyseben.finaxor.data.mapper.toAuthUser
import com.tonyseben.finaxor.domain.model.AuthState
import com.tonyseben.finaxor.domain.model.AuthUser
import dev.gitlive.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Auth Remote Data Source
 * Handles Firebase Auth operations
 */
class AuthRemoteDataSource(private val auth: FirebaseAuth) {

    /**
     * Authenticate using the provided service
     */
    suspend fun authenticate(service: AuthService): Result<AuthUser> {
        return service.authenticate(auth)
    }

    /**
     * Get the current Firebase Auth user
     */
    fun getCurrentUser(): AuthUser? {
        return auth.currentUser?.toAuthUser()
    }

    /**
     * Sign out from Firebase Auth
     */
    suspend fun signOut() {
        auth.signOut()
    }

    /**
     * Observe auth state changes
     */
    fun observeAuthState(): Flow<AuthState> {
        return auth.authStateChanged.map { firebaseUser ->
            when {
                firebaseUser == null -> AuthState.Unauthenticated
                else -> AuthState.Authenticated(firebaseUser.toAuthUser())
            }
        }
    }

    /**
     * Check if currently signed in
     */
    fun isSignedIn(): Boolean {
        return auth.currentUser != null
    }
}
