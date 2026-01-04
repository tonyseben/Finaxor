package com.tonyseben.finaxor.domain.usecase.fd

import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.domain.model.FixedDeposit
import com.tonyseben.finaxor.domain.usecase.UseCase

/**
 * Calculates summary values for a list of Fixed Deposits.
 * Returns raw calculation values - caller is responsible for adding type info.
 */
class CalculateFDSummaryUseCase(
    private val calculateFDCurrentValueUseCase: CalculateFDCurrentValueUseCase
) : UseCase<List<FixedDeposit>, CalculateFDSummaryUseCase.SummaryValues?> {

    data class SummaryValues(
        val investedAmount: Double,
        val currentValue: Double,
        val returnsPercent: Double
    )

    override suspend fun invoke(params: List<FixedDeposit>): Result<SummaryValues?> {
        if (params.isEmpty()) {
            return Result.Success(null)
        }

        val investedAmount = params.sumOf { it.principalAmount }
        var currentValue = 0.0

        for (fd in params) {
            val calcParams = CalculateFDCurrentValueUseCase.Params(fd)
            when (val result = calculateFDCurrentValueUseCase(calcParams)) {
                is Result.Success -> currentValue += result.data
                is Result.Error -> return Result.Error(result.error)
                is Result.Loading -> { /* skip */ }
            }
        }

        val returnsPercent = if (investedAmount > 0) {
            ((currentValue - investedAmount) / investedAmount) * 100
        } else {
            0.0
        }

        return Result.Success(
            SummaryValues(
                investedAmount = investedAmount,
                currentValue = currentValue,
                returnsPercent = returnsPercent
            )
        )
    }
}
