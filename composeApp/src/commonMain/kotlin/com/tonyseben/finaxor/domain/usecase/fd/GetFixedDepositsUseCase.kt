package com.tonyseben.finaxor.domain.usecase.fd

import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.domain.model.FixedDeposit
import com.tonyseben.finaxor.domain.repository.FixedDepositRepository
import com.tonyseben.finaxor.domain.usecase.FlowUseCase
import kotlinx.coroutines.flow.Flow

class GetFixedDepositsUseCase(
    private val fdRepository: FixedDepositRepository
) : FlowUseCase<String, List<FixedDeposit>> {

    override fun invoke(params: String): Flow<Result<List<FixedDeposit>>> {
        return fdRepository.getByPortfolio(params)
    }
}