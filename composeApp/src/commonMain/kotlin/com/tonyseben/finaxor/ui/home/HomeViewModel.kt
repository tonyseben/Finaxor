package com.tonyseben.finaxor.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.domain.model.AuthState
import com.tonyseben.finaxor.domain.usecase.auth.ObserveAuthStateUseCase
import com.tonyseben.finaxor.domain.usecase.portfolio.CreatePortfolioUseCase
import com.tonyseben.finaxor.domain.usecase.portfolio.GetUserPortfoliosUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val observeAuthStateUseCase: ObserveAuthStateUseCase,
    private val getUserPortfoliosUseCase: GetUserPortfoliosUseCase,
    private val createPortfolioUseCase: CreatePortfolioUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var currentUserId: String? = null

    init {
        observeAuthAndLoadPortfolios()
    }

    private fun observeAuthAndLoadPortfolios() {
        viewModelScope.launch {
            observeAuthStateUseCase(Unit).collect { result ->
                when (result) {
                    is Result.Success -> {
                        val authState = result.data
                        if (authState is AuthState.Authenticated) {
                            currentUserId = authState.user.uid
                            loadPortfolios(authState.user.uid)
                        }
                    }
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = result.error.message,
                            isLoading = false
                        )
                    }
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }
            }
        }
    }

    private fun loadPortfolios(userId: String) {
        viewModelScope.launch {
            getUserPortfoliosUseCase(userId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            portfolios = result.data,
                            isLoading = false
                        )
                    }
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = result.error.message,
                            isLoading = false
                        )
                    }
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }
            }
        }
    }

    fun showCreateSheet() {
        _uiState.value = _uiState.value.copy(showCreateSheet = true)
    }

    fun hideCreateSheet() {
        _uiState.value = _uiState.value.copy(showCreateSheet = false)
    }

    fun createPortfolio(name: String, onSuccess: (portfolioId: String) -> Unit) {
        val userId = currentUserId ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCreating = true)

            when (val result = createPortfolioUseCase(CreatePortfolioUseCase.Params(name, userId))) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isCreating = false,
                        showCreateSheet = false
                    )
                    onSuccess(result.data.id)
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = result.error.message,
                        isCreating = false
                    )
                }
                is Result.Loading -> { /* handled above */ }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
