package com.tonyseben.finaxor.domain.usecase.fd

import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.core.toAppError
import com.tonyseben.finaxor.domain.model.FDStatus
import com.tonyseben.finaxor.domain.model.FixedDeposit
import com.tonyseben.finaxor.domain.usecase.UseCase
import kotlin.time.Clock

/**
 * Get status of a Fixed Deposit
 */
class GetFDStatusUseCase : UseCase<GetFDStatusUseCase.Params, FDStatus> {

    data class Params(
        val fixedDeposit: FixedDeposit,
        val currentTimeMillis: Long = Clock.System.now().toEpochMilliseconds()
    )

    override suspend fun invoke(params: Params): Result<FDStatus> {
        return try {
            val status = when {
                params.currentTimeMillis < params.fixedDeposit.startDate -> FDStatus.UPCOMING
                params.currentTimeMillis >= params.fixedDeposit.maturityDate -> FDStatus.MATURED
                else -> FDStatus.ACTIVE
            }

            Result.Success(status)
        } catch (e: Exception) {
            Result.Error(e.toAppError())
        }
    }
}
