package com.tonyseben.finaxor.domain.usecase.portfolio

import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.domain.repository.PortfolioRepository
import com.tonyseben.finaxor.domain.usecase.UseCase

class DeletePortfolioUseCase(
    private val portfolioRepository: PortfolioRepository
) : UseCase<String, Unit> {

    override suspend fun invoke(params: String): Result<Unit> {
        return portfolioRepository.deletePortfolio(params)
    }
}