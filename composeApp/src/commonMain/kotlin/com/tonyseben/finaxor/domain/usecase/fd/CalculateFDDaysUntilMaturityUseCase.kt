package com.tonyseben.finaxor.domain.usecase.fd

import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.core.currentTimeMillis
import com.tonyseben.finaxor.core.toAppError
import com.tonyseben.finaxor.domain.model.FixedDeposit
import com.tonyseben.finaxor.domain.usecase.UseCase
import kotlin.time.Clock

/**
 * Calculate days until maturity for a Fixed Deposit
 */
class CalculateFDDaysUntilMaturityUseCase :
    UseCase<CalculateFDDaysUntilMaturityUseCase.Params, Long> {

    data class Params(
        val fixedDeposit: FixedDeposit,
        val currentTimeMillis: Long = currentTimeMillis()
    )

    override suspend fun invoke(params: Params): Result<Long> {
        return try {
            val daysUntilMaturity =
                if (params.currentTimeMillis >= params.fixedDeposit.maturityDate) {
                    0L
                } else {
                    (params.fixedDeposit.maturityDate - params.currentTimeMillis) / (24 * 60 * 60 * 1000)
                }

            Result.Success(daysUntilMaturity)
        } catch (e: Exception) {
            Result.Error(e.toAppError())
        }
    }
}
