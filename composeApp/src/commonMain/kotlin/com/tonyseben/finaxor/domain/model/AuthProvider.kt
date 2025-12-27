package com.tonyseben.finaxor.domain.model

/**
 * Sealed class representing authentication providers.
 * Extensible for future providers (Email, Apple, etc.).
 */
sealed class AuthProvider(val providerId: String) {

    data class Google(
        val idToken: String,
        val accessToken: String? = null
    ) : AuthProvider("google.com")

    // Future providers:
    // data class Email(val email: String, val password: String) : AuthProvider("password")
    // data class Apple(val idToken: String, val nonce: String) : AuthProvider("apple.com")
}
