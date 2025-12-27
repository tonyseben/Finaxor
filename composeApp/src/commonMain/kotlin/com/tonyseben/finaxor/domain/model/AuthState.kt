package com.tonyseben.finaxor.domain.model

/**
 * Represents the current authentication state.
 */
sealed class AuthState {
    data object Loading : AuthState()
    data object Unauthenticated : AuthState()
    data class Authenticated(val user: AuthUser) : AuthState()
}
