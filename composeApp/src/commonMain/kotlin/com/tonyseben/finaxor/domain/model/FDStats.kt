package com.tonyseben.finaxor.domain.model

data class FDStats(
    val currentValue: Double,
    val maturityAmount: Double,
    val interestEarned: Double,
    val daysUntilMaturity: Long,
    val status: FDStatus
)
