package com.tonyseben.finaxor.domain.usecase.auth

import com.tonyseben.finaxor.core.AppError
import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.domain.model.AuthProvider
import com.tonyseben.finaxor.domain.model.User
import com.tonyseben.finaxor.domain.repository.AuthRepository
import com.tonyseben.finaxor.domain.repository.UserRepository
import com.tonyseben.finaxor.domain.usecase.UseCase

/**
 * Signs in with Google and ensures user profile exists in Firestore.
 * Creates a new user profile if first-time sign-in.
 */
class SignInWithGoogleUseCase(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : UseCase<SignInWithGoogleUseCase.Params, User> {

    data class Params(
        val idToken: String,
        val accessToken: String? = null
    )

    override suspend fun invoke(params: Params): Result<User> {
        // 1. Sign in with Firebase Auth
        val authResult = authRepository.signIn(
            AuthProvider.Google(
                idToken = params.idToken,
                accessToken = params.accessToken
            )
        )

        if (authResult is Result.Error) {
            return authResult
        }

        val authUser = (authResult as Result.Success).data

        // 2. Check if user profile exists in Firestore
        return when (val existingUser = userRepository.getUser(authUser.uid)) {
            is Result.Success -> existingUser
            is Result.Error -> {
                // 3. Create new user profile if it doesn't exist
                userRepository.createUser(
                    userId = authUser.uid,
                    name = authUser.displayName ?: "Unknown",
                    email = authUser.email ?: "",
                    photoURL = authUser.photoUrl
                )
            }

            else -> Result.Error(AppError.UnknownError("Unexpected state"))
        }
    }
}
