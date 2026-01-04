package com.tonyseben.finaxor.domain.usecase.fd

import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.core.currentTimeMillis
import com.tonyseben.finaxor.core.toAppError
import com.tonyseben.finaxor.domain.calculator.calculateCurrentValue
import com.tonyseben.finaxor.domain.model.FixedDeposit
import com.tonyseben.finaxor.domain.usecase.UseCase

/**
 * Calculate current value of a Fixed Deposit.
 */
class CalculateFDCurrentValueUseCase : UseCase<CalculateFDCurrentValueUseCase.Params, Double> {

    data class Params(
        val fixedDeposit: FixedDeposit,
        val currentTimeMillis: Long = currentTimeMillis()
    )

    override suspend fun invoke(params: Params): Result<Double> {
        return try {
            Result.Success(params.fixedDeposit.calculateCurrentValue(params.currentTimeMillis))
        } catch (e: Exception) {
            Result.Error(e.toAppError())
        }
    }
}