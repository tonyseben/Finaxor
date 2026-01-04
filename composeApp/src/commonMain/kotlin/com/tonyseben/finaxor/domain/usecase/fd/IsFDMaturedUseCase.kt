package com.tonyseben.finaxor.domain.usecase.fd

import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.core.currentTimeMillis
import com.tonyseben.finaxor.core.toAppError
import com.tonyseben.finaxor.domain.model.FixedDeposit
import com.tonyseben.finaxor.domain.usecase.UseCase
import kotlin.time.Clock

/**
 * Check if a Fixed Deposit has matured
 */
class IsFDMaturedUseCase : UseCase<IsFDMaturedUseCase.Params, Boolean> {

    data class Params(
        val fixedDeposit: FixedDeposit,
        val currentTimeMillis: Long = currentTimeMillis()
    )

    override suspend fun invoke(params: Params): Result<Boolean> {
        return try {
            val isMatured = params.currentTimeMillis >= params.fixedDeposit.maturityDate
            Result.Success(isMatured)
        } catch (e: Exception) {
            Result.Error(e.toAppError())
        }
    }
}