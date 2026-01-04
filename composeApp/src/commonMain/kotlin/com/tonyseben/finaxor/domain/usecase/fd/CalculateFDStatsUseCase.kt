package com.tonyseben.finaxor.domain.usecase.fd

import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.core.currentTimeMillis
import com.tonyseben.finaxor.core.toAppError
import com.tonyseben.finaxor.domain.calculator.calculateCurrentValue
import com.tonyseben.finaxor.domain.calculator.calculateDaysUntilMaturity
import com.tonyseben.finaxor.domain.calculator.calculateInterestEarned
import com.tonyseben.finaxor.domain.calculator.calculateMaturityAmount
import com.tonyseben.finaxor.domain.calculator.getStatus
import com.tonyseben.finaxor.domain.model.FDStats
import com.tonyseben.finaxor.domain.model.FixedDeposit
import com.tonyseben.finaxor.domain.usecase.UseCase

/**
 * Calculate all stats for a Fixed Deposit.
 */
class CalculateFDStatsUseCase : UseCase<CalculateFDStatsUseCase.Params, FDStats> {

    data class Params(
        val fixedDeposit: FixedDeposit,
        val currentTimeMillis: Long = currentTimeMillis()
    )

    override suspend fun invoke(params: Params): Result<FDStats> {
        return try {
            val fd = params.fixedDeposit
            val currentTime = params.currentTimeMillis

            Result.Success(
                FDStats(
                    currentValue = fd.calculateCurrentValue(currentTime),
                    maturityAmount = fd.calculateMaturityAmount(),
                    interestEarned = fd.calculateInterestEarned(),
                    daysUntilMaturity = fd.calculateDaysUntilMaturity(currentTime),
                    status = fd.getStatus(currentTime)
                )
            )
        } catch (e: Exception) {
            Result.Error(e.toAppError())
        }
    }
}
