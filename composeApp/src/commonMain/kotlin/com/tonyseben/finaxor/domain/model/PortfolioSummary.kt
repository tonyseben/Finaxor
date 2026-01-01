package com.tonyseben.finaxor.domain.model

data class PortfolioSummary(
    val portfolio: Portfolio,
    val assetSummaries: List<AssetSummary>
)
