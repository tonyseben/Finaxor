package com.tonyseben.finaxor.domain.usecase.auth

import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.domain.model.AuthState
import com.tonyseben.finaxor.domain.repository.AuthRepository
import com.tonyseben.finaxor.domain.usecase.FlowUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Observes authentication state changes.
 */
class ObserveAuthStateUseCase(
    private val authRepository: AuthRepository
) : FlowUseCase<Unit, AuthState> {

    override fun invoke(params: Unit): Flow<Result<AuthState>> {
        return authRepository.observeAuthState()
            .map { authState -> Result.Success(authState) }
    }
}
