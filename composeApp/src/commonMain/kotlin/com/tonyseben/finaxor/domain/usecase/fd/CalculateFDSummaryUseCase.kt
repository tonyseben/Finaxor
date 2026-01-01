package com.tonyseben.finaxor.domain.usecase.fd

import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.domain.model.AssetSummary
import com.tonyseben.finaxor.domain.model.AssetType
import com.tonyseben.finaxor.domain.model.FixedDeposit
import com.tonyseben.finaxor.domain.usecase.UseCase

class CalculateFDSummaryUseCase(
    private val calculateFDCurrentValueUseCase: CalculateFDCurrentValueUseCase
) : UseCase<List<FixedDeposit>, AssetSummary?> {

    override suspend fun invoke(params: List<FixedDeposit>): Result<AssetSummary?> {
        if (params.isEmpty()) {
            return Result.Success(null)
        }

        val investedAmount = params.sumOf { it.principalAmount }
        var currentValue = 0.0

        for (fd in params) {
            val calcParams = CalculateFDCurrentValueUseCase.Params(fd)
            when (val result = calculateFDCurrentValueUseCase(calcParams)) {
                is Result.Success -> currentValue += result.data
                is Result.Error -> currentValue += fd.principalAmount
                is Result.Loading -> { /* skip */ }
            }
        }

        val returnsPercent = if (investedAmount > 0) {
            ((currentValue - investedAmount) / investedAmount) * 100
        } else {
            0.0
        }

        return Result.Success(
            AssetSummary(
                assetType = AssetType.FIXED_DEPOSIT,
                investedAmount = investedAmount,
                currentValue = currentValue,
                returnsPercent = returnsPercent
            )
        )
    }
}
