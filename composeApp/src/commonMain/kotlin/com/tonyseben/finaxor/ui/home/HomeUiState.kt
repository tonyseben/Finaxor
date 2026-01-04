package com.tonyseben.finaxor.ui.home

import com.tonyseben.finaxor.domain.model.UserPortfolio

data class HomeUiState(
    val currentUserId: String? = null,
    val portfolios: List<UserPortfolio> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val showCreateSheet: Boolean = false,
    val isCreating: Boolean = false
)
