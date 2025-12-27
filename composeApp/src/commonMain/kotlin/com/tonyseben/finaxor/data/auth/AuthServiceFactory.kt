package com.tonyseben.finaxor.data.auth

import com.tonyseben.finaxor.domain.model.AuthProvider

/**
 * Factory for creating auth services based on provider type.
 */
object AuthServiceFactory {

    fun create(provider: AuthProvider): AuthService {
        return when (provider) {
            is AuthProvider.Google -> GoogleAuthService(
                idToken = provider.idToken,
                accessToken = provider.accessToken
            )
            // Future providers:
            // is AuthProvider.Email -> EmailAuthService(provider.email, provider.password)
            // is AuthProvider.Apple -> AppleAuthService(provider.idToken, provider.nonce)
        }
    }
}
