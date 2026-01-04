package com.tonyseben.finaxor.domain.usecase.fd

import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.core.currentTimeMillis
import com.tonyseben.finaxor.core.toAppError
import com.tonyseben.finaxor.domain.model.FixedDeposit
import com.tonyseben.finaxor.domain.usecase.UseCase

/**
 * Calculate current value of a Fixed Deposit (pro-rated)
 */
class CalculateFDCurrentValueUseCase(
    private val calculateMaturityAmountUseCase: CalculateFDMaturityAmountUseCase
) : UseCase<CalculateFDCurrentValueUseCase.Params, Double> {

    data class Params(
        val fixedDeposit: FixedDeposit,
        val currentTimeMillis: Long = currentTimeMillis()
    )

    override suspend fun invoke(params: Params): Result<Double> {
        return try {
            val fd = params.fixedDeposit
            val currentTime = params.currentTimeMillis

            val currentValue = when {
                currentTime < fd.startDate -> fd.principalAmount
                currentTime >= fd.maturityDate -> {
                    when (val maturityResult = calculateMaturityAmountUseCase(fd)) {
                        is Result.Success -> maturityResult.data
                        is Result.Error -> return maturityResult
                        is Result.Loading -> return Result.Loading
                    }
                }

                else -> {
                    val maturityAmount =
                        when (val maturityResult = calculateMaturityAmountUseCase(fd)) {
                            is Result.Success -> maturityResult.data
                            is Result.Error -> return maturityResult
                            is Result.Loading -> return Result.Loading
                        }

                    val totalDuration = fd.maturityDate - fd.startDate
                    val elapsedDuration = currentTime - fd.startDate
                    val progress = elapsedDuration.toDouble() / totalDuration.toDouble()

                    fd.principalAmount + (maturityAmount - fd.principalAmount) * progress
                }
            }

            Result.Success(currentValue)
        } catch (e: Exception) {
            Result.Error(e.toAppError())
        }
    }
}