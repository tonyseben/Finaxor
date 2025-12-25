package com.tonyseben.finaxor.domain.model

data class FixedDeposit(
    val id: String,
    val portfolioId: String,
    val bankName: String,
    val accountNumber: String,
    val principalAmount: Double,
    val interestRate: Double,
    val startDate: Long,
    val maturityDate: Long,
    val interestPayoutFreq: PayoutFrequency,
    val createdAt: Long,
    val updatedAt: Long,
    val createdBy: String
)