package com.tonyseben.finaxor.ui.assetlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.domain.usecase.asset.GetAssetListDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AssetListViewModel(
    private val portfolioId: String,
    private val assetType: String,
    private val getAssetListDataUseCase: GetAssetListDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AssetListUiState())
    val uiState: StateFlow<AssetListUiState> = _uiState.asStateFlow()

    init {
        loadAssets()
    }

    private fun loadAssets() {
        viewModelScope.launch {
            val params = GetAssetListDataUseCase.Params(
                portfolioId = portfolioId,
                assetType = assetType
            )

            getAssetListDataUseCase(params).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.value = AssetListUiState(
                            displayName = result.data.displayName,
                            stats = result.data.stats,
                            items = result.data.items,
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
}
