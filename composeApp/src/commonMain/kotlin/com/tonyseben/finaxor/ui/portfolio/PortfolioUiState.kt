package com.tonyseben.finaxor.ui.portfolio

import com.tonyseben.finaxor.domain.model.AssetSummary

data class PortfolioUiState(
    val portfolioName: String = "",
    val assetSummaries: List<AssetSummary> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val showAddAssetSheet: Boolean = false
)
