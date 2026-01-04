package com.tonyseben.finaxor.domain.usecase.auth

import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.domain.model.AuthUser
import com.tonyseben.finaxor.domain.repository.AuthRepository
import com.tonyseben.finaxor.domain.usecase.UseCase

/**
 * Gets the currently authenticated user from Firebase Auth.
 * Returns null if not authenticated.
 * Use this when you only need auth info (uid, email) without Firestore profile.
 */
class GetCurrentAuthUserUseCase(
    private val authRepository: AuthRepository
) : UseCase<Unit, AuthUser?> {

    override suspend fun invoke(params: Unit): Result<AuthUser?> {
        return authRepository.getCurrentUser()
    }
}
