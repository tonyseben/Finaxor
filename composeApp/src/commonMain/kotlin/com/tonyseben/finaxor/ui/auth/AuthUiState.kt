package com.tonyseben.finaxor.ui.auth

import com.tonyseben.finaxor.domain.model.AuthState
import com.tonyseben.finaxor.domain.model.User

data class AuthUiState(
    val authState: AuthState = AuthState.Loading,
    val user: User? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
