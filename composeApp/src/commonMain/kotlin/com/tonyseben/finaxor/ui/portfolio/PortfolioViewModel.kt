package com.tonyseben.finaxor.ui.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.domain.usecase.portfolio.GetPortfolioSummaryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PortfolioViewModel(
    private val portfolioId: String,
    private val getPortfolioSummaryUseCase: GetPortfolioSummaryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PortfolioUiState())
    val uiState: StateFlow<PortfolioUiState> = _uiState.asStateFlow()

    init {
        loadPortfolioSummary()
    }

    private fun loadPortfolioSummary() {
        viewModelScope.launch {
            getPortfolioSummaryUseCase(portfolioId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            portfolioName = result.data.portfolio.name,
                            assetSummaries = result.data.assetSummaries,
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

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun showAddAssetSheet() {
        _uiState.value = _uiState.value.copy(showAddAssetSheet = true)
    }

    fun hideAddAssetSheet() {
        _uiState.value = _uiState.value.copy(showAddAssetSheet = false)
    }
}
