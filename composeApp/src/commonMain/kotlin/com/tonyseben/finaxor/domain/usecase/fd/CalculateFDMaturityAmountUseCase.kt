package com.tonyseben.finaxor.domain.usecase.fd

import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.core.toAppError
import com.tonyseben.finaxor.domain.calculator.calculateMaturityAmount
import com.tonyseben.finaxor.domain.model.FixedDeposit
import com.tonyseben.finaxor.domain.usecase.UseCase

/**
 * Calculate maturity amount for a Fixed Deposit.
 */
class CalculateFDMaturityAmountUseCase : UseCase<FixedDeposit, Double> {

    override suspend fun invoke(params: FixedDeposit): Result<Double> {
        return try {
            Result.Success(params.calculateMaturityAmount())
        } catch (e: Exception) {
            Result.Error(e.toAppError())
        }
    }
}
