package com.tonyseben.finaxor.domain.usecase.fd

import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.domain.model.FixedDeposit
import com.tonyseben.finaxor.domain.repository.FixedDepositRepository
import com.tonyseben.finaxor.domain.usecase.UseCase

class GetFixedDepositUseCase(
    private val fdRepository: FixedDepositRepository
) : UseCase<GetFixedDepositUseCase.Params, FixedDeposit> {

    data class Params(
        val portfolioId: String,
        val fdId: String
    )

    override suspend fun invoke(params: Params): Result<FixedDeposit> {
        return fdRepository.getById(params.portfolioId, params.fdId)
    }
}
