package com.tonyseben.finaxor.domain.usecase.fd

import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.core.toAppError
import com.tonyseben.finaxor.domain.model.FixedDeposit
import com.tonyseben.finaxor.domain.model.PayoutFrequency
import com.tonyseben.finaxor.domain.usecase.UseCase
import kotlin.math.pow

/**
 * Calculate maturity amount for a Fixed Deposit
 */
class CalculateFDMaturityAmountUseCase : UseCase<FixedDeposit, Double> {

    override suspend fun invoke(params: FixedDeposit): Result<Double> {
        return try {
            val years = (params.maturityDate - params.startDate) / (365.25 * 24 * 60 * 60 * 1000.0)

            val maturityAmount = when (params.payoutFrequency) {
                PayoutFrequency.MONTHLY -> {
                    val n = 12.0
                    params.principalAmount * (1 + (params.interestRate / 100) / n).pow(n * years)
                }

                PayoutFrequency.QUARTERLY -> {
                    val n = 4.0
                    params.principalAmount * (1 + (params.interestRate / 100) / n).pow(n * years)
                }

                PayoutFrequency.YEARLY -> {
                    params.principalAmount * (1 + (params.interestRate / 100)).pow(years)
                }
            }

            Result.Success(maturityAmount)
        } catch (e: Exception) {
            Result.Error(e.toAppError())
        }
    }
}