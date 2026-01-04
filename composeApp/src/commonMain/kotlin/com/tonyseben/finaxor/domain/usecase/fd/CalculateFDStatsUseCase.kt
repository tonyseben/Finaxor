package com.tonyseben.finaxor.domain.usecase.fd

import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.core.currentTimeMillis
import com.tonyseben.finaxor.core.toAppError
import com.tonyseben.finaxor.domain.model.FDStats
import com.tonyseben.finaxor.domain.model.FixedDeposit
import com.tonyseben.finaxor.domain.usecase.UseCase
import kotlin.time.Clock

class CalculateFDStatsUseCase(
    private val calculateCurrentValueUseCase: CalculateFDCurrentValueUseCase,
    private val calculateMaturityAmountUseCase: CalculateFDMaturityAmountUseCase,
    private val calculateInterestEarnedUseCase: CalculateFDInterestEarnedUseCase,
    private val calculateDaysUntilMaturityUseCase: CalculateFDDaysUntilMaturityUseCase,
    private val getFDStatusUseCase: GetFDStatusUseCase
) : UseCase<CalculateFDStatsUseCase.Params, FDStats> {

    data class Params(
        val fixedDeposit: FixedDeposit,
        val currentTimeMillis: Long = currentTimeMillis()
    )

    override suspend fun invoke(params: Params): Result<FDStats> {
        return try {
            val fd = params.fixedDeposit
            val currentTime = params.currentTimeMillis

            val currentValue = when (val r = calculateCurrentValueUseCase(
                CalculateFDCurrentValueUseCase.Params(fd, currentTime)
            )) {
                is Result.Success -> r.data
                is Result.Error -> return r
                is Result.Loading -> return Result.Loading
            }

            val maturityAmount = when (val r = calculateMaturityAmountUseCase(fd)) {
                is Result.Success -> r.data
                is Result.Error -> return r
                is Result.Loading -> return Result.Loading
            }

            val interestEarned = when (val r = calculateInterestEarnedUseCase(fd)) {
                is Result.Success -> r.data
                is Result.Error -> return r
                is Result.Loading -> return Result.Loading
            }

            val daysUntilMaturity = when (val r = calculateDaysUntilMaturityUseCase(
                CalculateFDDaysUntilMaturityUseCase.Params(fd, currentTime)
            )) {
                is Result.Success -> r.data
                is Result.Error -> return r
                is Result.Loading -> return Result.Loading
            }

            val status = when (val r = getFDStatusUseCase(
                GetFDStatusUseCase.Params(fd, currentTime)
            )) {
                is Result.Success -> r.data
                is Result.Error -> return r
                is Result.Loading -> return Result.Loading
            }

            Result.Success(
                FDStats(
                    currentValue = currentValue,
                    maturityAmount = maturityAmount,
                    interestEarned = interestEarned,
                    daysUntilMaturity = daysUntilMaturity,
                    status = status
                )
            )
        } catch (e: Exception) {
            Result.Error(e.toAppError())
        }
    }
}
