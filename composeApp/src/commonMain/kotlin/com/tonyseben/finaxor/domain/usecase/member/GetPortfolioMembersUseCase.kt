package com.tonyseben.finaxor.domain.usecase.member

import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.domain.model.User
import com.tonyseben.finaxor.domain.repository.PortfolioRepository
import com.tonyseben.finaxor.domain.usecase.FlowUseCase
import kotlinx.coroutines.flow.Flow

class GetPortfolioMembersUseCase(
    private val portfolioRepository: PortfolioRepository
) : FlowUseCase<String, List<User>> {

    override fun invoke(params: String): Flow<Result<List<User>>> {
        return portfolioRepository.getPortfolioMembers(params)
    }
}