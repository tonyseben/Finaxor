package com.tonyseben.finaxor.domain.usecase.fd

import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.core.toAppError
import com.tonyseben.finaxor.domain.calculator.calculateInterestEarned
import com.tonyseben.finaxor.domain.model.FixedDeposit
import com.tonyseben.finaxor.domain.usecase.UseCase

/**
 * Calculate interest earned for a Fixed Deposit.
 */
class CalculateFDInterestEarnedUseCase : UseCase<FixedDeposit, Double> {

    override suspend fun invoke(params: FixedDeposit): Result<Double> {
        return try {
            Result.Success(params.calculateInterestEarned())
        } catch (e: Exception) {
            Result.Error(e.toAppError())
        }
    }
}
