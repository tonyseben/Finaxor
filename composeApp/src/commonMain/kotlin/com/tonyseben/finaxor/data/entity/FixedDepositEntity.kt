package com.tonyseben.finaxor.data.entity

import kotlinx.serialization.Serializable

@Serializable
data class FixedDepositEntity(
    val id: String = "",
    val portfolioId: String = "",
    val bankName: String = "",
    val accountNumber: String = "",
    val principalAmount: Double = 0.0,
    val interestRate: Double = 0.0,
    val startDate: Long = 0L,
    val maturityDate: Long = 0L,
    val payoutFrequency: String = "",
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val createdBy: String = ""
)