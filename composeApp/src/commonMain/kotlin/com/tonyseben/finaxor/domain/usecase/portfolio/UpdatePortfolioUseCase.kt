package com.tonyseben.finaxor.domain.usecase.portfolio

import com.tonyseben.finaxor.core.AppError
import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.domain.repository.PortfolioRepository
import com.tonyseben.finaxor.domain.usecase.UseCase

class UpdatePortfolioUseCase(
    private val portfolioRepository: PortfolioRepository
) : UseCase<UpdatePortfolioUseCase.Params, Unit> {

    data class Params(val portfolioId: String, val name: String)

    override suspend fun invoke(params: Params): Result<Unit> {
        if (params.name.isBlank()) {
            return Result.Error(
                AppError.ValidationError(
                    field = "name",
                    message = "Portfolio name cannot be empty"
                )
            )
        }

        return portfolioRepository.updatePortfolio(params.portfolioId, params.name)
    }
}