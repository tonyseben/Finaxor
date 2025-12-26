package com.tonyseben.finaxor.data.mapper

import com.tonyseben.finaxor.data.entity.FixedDepositEntity
import com.tonyseben.finaxor.domain.model.FixedDeposit
import com.tonyseben.finaxor.domain.model.PayoutFrequency

fun FixedDepositEntity.toDomain(): FixedDeposit {
    return FixedDeposit(
        id = id,
        portfolioId = portfolioId,
        bankName = bankName,
        accountNumber = accountNumber,
        principalAmount = principalAmount,
        interestRate = interestRate,
        startDate = startDate,
        maturityDate = maturityDate,
        payoutFrequency = payoutFrequency.toPayoutFrequency() ?: PayoutFrequency.YEARLY,
        createdAt = createdAt,
        updatedAt = updatedAt,
        createdBy = createdBy
    )
}

fun FixedDeposit.toEntity(): FixedDepositEntity {
    return FixedDepositEntity(
        id = id,
        portfolioId = portfolioId,
        bankName = bankName,
        accountNumber = accountNumber,
        principalAmount = principalAmount,
        interestRate = interestRate,
        startDate = startDate,
        maturityDate = maturityDate,
        payoutFrequency = payoutFrequency.toFrequencyString(),
        createdAt = createdAt,
        updatedAt = updatedAt,
        createdBy = createdBy
    )
}

fun String.toPayoutFrequency(): PayoutFrequency? {
    return when (this.lowercase()) {
        "monthly" -> PayoutFrequency.MONTHLY
        "quarterly" -> PayoutFrequency.QUARTERLY
        "yearly" -> PayoutFrequency.YEARLY
        else -> null
    }
}

fun PayoutFrequency.toFrequencyString(): String {
    return when (this) {
        PayoutFrequency.MONTHLY -> "monthly"
        PayoutFrequency.QUARTERLY -> "quarterly"
        PayoutFrequency.YEARLY -> "yearly"
    }
}


fun List<FixedDepositEntity>.toDomain(): List<FixedDeposit> = map { it.toDomain() }