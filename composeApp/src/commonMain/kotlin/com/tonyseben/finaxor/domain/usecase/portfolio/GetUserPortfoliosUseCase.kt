package com.tonyseben.finaxor.domain.usecase.portfolio

import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.domain.model.UserPortfolio
import com.tonyseben.finaxor.domain.repository.PortfolioRepository
import com.tonyseben.finaxor.domain.usecase.FlowUseCase
import kotlinx.coroutines.flow.Flow

class GetUserPortfoliosUseCase(
    private val portfolioRepository: PortfolioRepository
) : FlowUseCase<String, List<UserPortfolio>> {

    override fun invoke(params: String): Flow<Result<List<UserPortfolio>>> {
        return portfolioRepository.getUserPortfolios(params)
    }
}