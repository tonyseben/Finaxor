package com.tonyseben.finaxor.domain.model

data class AssetSummary(
    val type: String,
    val displayName: String,
    val investedAmount: Double,
    val currentValue: Double,
    val returnsPercent: Double
)