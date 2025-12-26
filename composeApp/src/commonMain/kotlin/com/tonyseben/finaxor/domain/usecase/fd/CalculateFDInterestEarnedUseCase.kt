package com.tonyseben.finaxor.domain.usecase.fd

import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.domain.model.FixedDeposit
import com.tonyseben.finaxor.domain.usecase.UseCase

/**
 * Calculate interest earned for a Fixed Deposit
 */
class CalculateFDInterestEarnedUseCase(
    private val calculateMaturityAmountUseCase: CalculateFDMaturityAmountUseCase
) : UseCase<FixedDeposit, Double> {

    override suspend fun invoke(params: FixedDeposit): Result<Double> {
        return when (val maturityResult = calculateMaturityAmountUseCase(params)) {
            is Result.Success -> {
                val interestEarned = maturityResult.data - params.principalAmount
                Result.Success(interestEarned)
            }

            is Result.Error -> maturityResult
            is Result.Loading -> Result.Loading
        }
    }
}