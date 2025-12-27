package com.tonyseben.finaxor.domain.usecase.auth

import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.domain.repository.AuthRepository
import com.tonyseben.finaxor.domain.usecase.UseCase

/**
 * Signs out the current user.
 */
class LogoutUseCase(
    private val authRepository: AuthRepository
) : UseCase<Unit, Unit> {

    override suspend fun invoke(params: Unit): Result<Unit> {
        return authRepository.signOut()
    }
}
