package com.tonyseben.finaxor.domain.asset

import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.core.currentTimeMillis
import com.tonyseben.finaxor.core.formatCurrency
import com.tonyseben.finaxor.domain.calculator.calculateCurrentValue
import com.tonyseben.finaxor.domain.model.AssetListItem
import com.tonyseben.finaxor.domain.model.AssetListStats
import com.tonyseben.finaxor.domain.model.AssetSummary
import com.tonyseben.finaxor.domain.model.FDListItem
import com.tonyseben.finaxor.domain.model.FixedDeposit
import com.tonyseben.finaxor.domain.model.StatEntry
import com.tonyseben.finaxor.domain.model.StatValueColor
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

    override fun toListItems(assets: List<Any>): List<AssetListItem> {
        val currentTime = currentTimeMillis()
        return assets.filterIsInstance<FixedDeposit>().map { fd ->
            val currentValue = fd.calculateCurrentValue(currentTime)
            val returnsPercent = if (fd.principalAmount > 0) {
                ((currentValue - fd.principalAmount) / fd.principalAmount) * 100
            } else 0.0

            FDListItem(
                id = fd.id,
                title = fd.bankName,
                subtitle = fd.accountNumber,
                primaryValue = fd.principalAmount,
                secondaryValue = currentValue,
                returnsPercent = returnsPercent
            )
        }
    }

    override fun calculateListStats(assets: List<Any>): AssetListStats {
        val fds = assets.filterIsInstance<FixedDeposit>()
        val count = fds.size
        val currentTime = currentTimeMillis()

        val totalInvested = fds.sumOf { it.principalAmount }
        val totalCurrentValue = fds.sumOf { it.calculateCurrentValue(currentTime) }
        val returnsPercent = if (totalInvested > 0) {
            ((totalCurrentValue - totalInvested) / totalInvested) * 100
        } else 0.0

        val returnsFormatted = "${if (returnsPercent >= 0) "+" else ""}${"%.2f".format(returnsPercent)}%"

        return AssetListStats(
            headline = "$count Fixed Deposit${if (count != 1) "s" else ""}",
            entries = listOf(
                StatEntry(label = "Total Invested", value = formatCurrency(totalInvested)),
                StatEntry(label = "Current Value", value = formatCurrency(totalCurrentValue)),
                StatEntry(
                    label = "Returns",
                    value = returnsFormatted,
                    valueColor = if (returnsPercent >= 0) StatValueColor.POSITIVE else StatValueColor.NEGATIVE
                )
            )
        )
    }
}
