package com.tonyseben.finaxor.domain.usecase.portfolio

import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.domain.model.Portfolio
import com.tonyseben.finaxor.domain.repository.PortfolioRepository
import com.tonyseben.finaxor.domain.usecase.UseCase

class GetPortfolioUseCase(
    private val portfolioRepository: PortfolioRepository
) : UseCase<String, Portfolio> {

    override suspend fun invoke(params: String): Result<Portfolio> {
        return portfolioRepository.getPortfolio(params)
    }
}
