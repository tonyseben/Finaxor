package com.tonyseben.finaxor.domain.repository

import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.domain.model.AuthProvider
import com.tonyseben.finaxor.domain.model.AuthState
import com.tonyseben.finaxor.domain.model.AuthUser
import kotlinx.coroutines.flow.Flow

/**
 * Auth Repository Interface
 * Defines contract for authentication operations
 */
interface AuthRepository {

    /**
     * Sign in with the specified auth provider
     */
    suspend fun signIn(provider: AuthProvider): Result<AuthUser>

    /**
     * Get the current authenticated user (null if not signed in)
     */
    suspend fun getCurrentUser(): Result<AuthUser?>

    /**
     * Sign out the current user
     */
    suspend fun signOut(): Result<Unit>

    /**
     * Observe authentication state changes
     */
    fun observeAuthState(): Flow<AuthState>

    /**
     * Check if user is currently signed in
     */
    fun isSignedIn(): Boolean
}
