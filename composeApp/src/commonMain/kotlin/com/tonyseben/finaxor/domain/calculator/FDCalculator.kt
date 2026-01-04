package com.tonyseben.finaxor.domain.calculator

import com.tonyseben.finaxor.domain.model.FDStatus
import com.tonyseben.finaxor.domain.model.FixedDeposit
import com.tonyseben.finaxor.domain.model.PayoutFrequency
import kotlin.math.pow

private const val MILLIS_PER_DAY = 24 * 60 * 60 * 1000L
private const val DAYS_PER_YEAR = 365.25

fun FixedDeposit.calculateMaturityAmount(): Double {
    val years = (maturityDate - startDate) / (DAYS_PER_YEAR * MILLIS_PER_DAY)

    return when (payoutFrequency) {
        PayoutFrequency.MONTHLY -> {
            val n = 12.0
            principalAmount * (1 + (interestRate / 100) / n).pow(n * years)
        }

        PayoutFrequency.QUARTERLY -> {
            val n = 4.0
            principalAmount * (1 + (interestRate / 100) / n).pow(n * years)
        }

        PayoutFrequency.YEARLY -> {
            principalAmount * (1 + (interestRate / 100)).pow(years)
        }
    }
}

fun FixedDeposit.calculateInterestEarned(): Double =
    calculateMaturityAmount() - principalAmount

fun FixedDeposit.calculateCurrentValue(currentTimeMillis: Long): Double {
    return when {
        currentTimeMillis < startDate -> principalAmount
        currentTimeMillis >= maturityDate -> calculateMaturityAmount()
        else -> {
            val elapsed = (currentTimeMillis - startDate) / (DAYS_PER_YEAR * MILLIS_PER_DAY)

            when (payoutFrequency) {
                PayoutFrequency.MONTHLY -> {
                    val n = 12.0
                    principalAmount * (1 + (interestRate / 100) / n).pow(n * elapsed)
                }

                PayoutFrequency.QUARTERLY -> {
                    val n = 4.0
                    principalAmount * (1 + (interestRate / 100) / n).pow(n * elapsed)
                }

                PayoutFrequency.YEARLY -> {
                    principalAmount * (1 + (interestRate / 100)).pow(elapsed)
                }
            }
        }
    }
}

fun FixedDeposit.calculateDaysUntilMaturity(currentTimeMillis: Long): Long =
    if (currentTimeMillis >= maturityDate) 0L
    else (maturityDate - currentTimeMillis) / MILLIS_PER_DAY

fun FixedDeposit.getStatus(currentTimeMillis: Long): FDStatus = when {
    currentTimeMillis < startDate -> FDStatus.UPCOMING
    currentTimeMillis >= maturityDate -> FDStatus.MATURED
    else -> FDStatus.ACTIVE
}

fun FixedDeposit.isActive(currentTimeMillis: Long): Boolean =
    currentTimeMillis >= startDate && currentTimeMillis < maturityDate

fun FixedDeposit.isMatured(currentTimeMillis: Long): Boolean =
    currentTimeMillis >= maturityDate
