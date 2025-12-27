package com.tonyseben.finaxor.domain.usecase.auth

import com.tonyseben.finaxor.core.AppError
import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.domain.model.User
import com.tonyseben.finaxor.domain.repository.AuthRepository
import com.tonyseben.finaxor.domain.repository.UserRepository
import com.tonyseben.finaxor.domain.usecase.UseCase

/**
 * Gets the currently authenticated user's profile from Firestore.
 * Returns null if not authenticated.
 */
class GetCurrentUserUseCase(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : UseCase<Unit, User?> {

    override suspend fun invoke(params: Unit): Result<User?> {
        return when (val authUser = authRepository.getCurrentUser()) {
            is Result.Success -> {
                if (authUser.data == null) {
                    Result.Success(null)
                } else {
                    when (val result = userRepository.getUser(authUser.data.uid)) {
                        is Result.Success -> Result.Success(result.data)
                        is Result.Error -> Result.Success(null) // User profile doesn't exist
                        else -> Result.Error(AppError.UnknownError("Unexpected state"))
                    }
                }
            }

            is Result.Error -> authUser
            else -> Result.Error(AppError.UnknownError("Unexpected state"))
        }
    }
}
