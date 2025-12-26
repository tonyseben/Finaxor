package com.tonyseben.finaxor.domain.usecase.fd

import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.domain.repository.FixedDepositRepository
import com.tonyseben.finaxor.domain.usecase.UseCase

class DeleteFixedDepositUseCase(
    private val fdRepository: FixedDepositRepository
) : UseCase<DeleteFixedDepositUseCase.Params, Unit> {

    data class Params(val portfolioId: String, val fdId: String)

    override suspend fun invoke(params: Params): Result<Unit> {
        return fdRepository.delete(params.portfolioId, params.fdId)
    }
}