package com.tonyseben.finaxor.domain.usecase.portfolio

import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.domain.model.Portfolio
import com.tonyseben.finaxor.domain.repository.PortfolioRepository
import com.tonyseben.finaxor.domain.usecase.FlowUseCase
import kotlinx.coroutines.flow.Flow

class GetUserPortfoliosUseCase(
    private val portfolioRepository: PortfolioRepository
) : FlowUseCase<String, List<Portfolio>> {

    override fun invoke(params: String): Flow<Result<List<Portfolio>>> {
        return portfolioRepository.getUserPortfolios(params)
    }
}