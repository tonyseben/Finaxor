package com.tonyseben.finaxor.data.repository

import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.core.toAppError
import com.tonyseben.finaxor.data.auth.AuthServiceFactory
import com.tonyseben.finaxor.data.source.remote.AuthRemoteDataSource
import com.tonyseben.finaxor.domain.model.AuthProvider
import com.tonyseben.finaxor.domain.model.AuthState
import com.tonyseben.finaxor.domain.model.AuthUser
import com.tonyseben.finaxor.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class AuthRepositoryImpl(
    private val authRemoteDataSource: AuthRemoteDataSource
) : AuthRepository {

    override suspend fun signIn(provider: AuthProvider): Result<AuthUser> {
        return try {
            val service = AuthServiceFactory.create(provider)
            authRemoteDataSource.authenticate(service)
        } catch (e: Exception) {
            Result.Error(e.toAppError())
        }
    }

    override suspend fun getCurrentUser(): Result<AuthUser?> {
        return try {
            Result.Success(authRemoteDataSource.getCurrentUser())
        } catch (e: Exception) {
            Result.Error(e.toAppError())
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            authRemoteDataSource.signOut()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.toAppError())
        }
    }

    override fun observeAuthState(): Flow<AuthState> {
        return authRemoteDataSource.observeAuthState()
    }

    override fun isSignedIn(): Boolean {
        return authRemoteDataSource.isSignedIn()
    }
}
