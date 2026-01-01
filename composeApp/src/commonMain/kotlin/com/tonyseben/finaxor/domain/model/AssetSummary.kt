package com.tonyseben.finaxor.domain.model

data class AssetSummary(
    val assetType: AssetType,
    val investedAmount: Double,
    val currentValue: Double,
    val returnsPercent: Double
)