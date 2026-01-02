package com.tonyseben.finaxor.domain.asset

import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.domain.model.AssetSummary
import com.tonyseben.finaxor.domain.model.FixedDeposit
import com.tonyseben.finaxor.domain.repository.FixedDepositRepository
import com.tonyseben.finaxor.domain.usecase.fd.CalculateFDSummaryUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FixedDepositStrategy(
    private val fdRepository: FixedDepositRepository,
    private val calculateSummaryUseCase: CalculateFDSummaryUseCase
) : AssetStrategy {

    override val type = "FIXED_DEPOSIT"
    override val displayName = "Fixed Deposits"

    override fun getAssets(portfolioId: String): Flow<Result<List<Any>>> {
        return fdRepository.getByPortfolio(portfolioId).map { result ->
            when (result) {
                is Result.Success -> Result.Success(result.data as List<Any>)
                is Result.Error -> result
                is Result.Loading -> Result.Loading
            }
        }
    }

    override suspend fun calculateSummary(assets: List<Any>): Result<AssetSummary?> {
        val fixedDeposits = assets.filterIsInstance<FixedDeposit>()

        return when (val result = calculateSummaryUseCase(fixedDeposits)) {
            is Result.Success -> {
                val values = result.data
                if (values == null) {
                    Result.Success(null)
                } else {
                    Result.Success(
                        AssetSummary(
                            type = type,
                            displayName = displayName,
                            investedAmount = values.investedAmount,
                            currentValue = values.currentValue,
                            returnsPercent = values.returnsPercent
                        )
                    )
                }
            }
            is Result.Error -> result
            is Result.Loading -> Result.Loading
        }
    }
}
