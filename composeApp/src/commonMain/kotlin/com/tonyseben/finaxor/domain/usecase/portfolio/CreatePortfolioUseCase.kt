package com.tonyseben.finaxor.domain.usecase.portfolio

import com.tonyseben.finaxor.core.AppError
import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.domain.model.Portfolio
import com.tonyseben.finaxor.domain.repository.PortfolioRepository
import com.tonyseben.finaxor.domain.usecase.UseCase

class CreatePortfolioUseCase(
    private val portfolioRepository: PortfolioRepository
) : UseCase<CreatePortfolioUseCase.Params, Portfolio> {

    data class Params(val name: String, val userId: String)

    override suspend fun invoke(params: Params): Result<Portfolio> {
        // Validation
        if (params.name.isBlank()) {
            return Result.Error(
                AppError.ValidationError(
                    field = "name",
                    message = "Portfolio name cannot be empty"
                )
            )
        }

        if (params.name.length > 100) {
            return Result.Error(
                AppError.ValidationError(
                    field = "name",
                    message = "Portfolio name is too long (max 100 characters)"
                )
            )
        }

        if (params.userId.isBlank()) {
            return Result.Error(
                AppError.ValidationError(
                    field = "userId",
                    message = "User ID cannot be empty"
                )
            )
        }

        return portfolioRepository.createPortfolio(params.name, params.userId)
    }
}