package com.tonyseben.finaxor.domain.usecase.portfolio

import com.tonyseben.finaxor.core.AppError
import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.domain.model.PortfolioRole
import com.tonyseben.finaxor.domain.repository.PortfolioRepository
import com.tonyseben.finaxor.domain.usecase.UseCase

class DeletePortfolioUseCase(
    private val portfolioRepository: PortfolioRepository
) : UseCase<DeletePortfolioUseCase.Params, Unit> {

    data class Params(
        val portfolioId: String,
        val currentUserId: String
    )

    override suspend fun invoke(params: Params): Result<Unit> {
        // Verify user is OWNER
        val roleResult = portfolioRepository.getMemberRole(params.portfolioId, params.currentUserId)
        if (roleResult is Result.Error) return Result.Error(roleResult.error)

        val role = (roleResult as Result.Success).data
        if (role == null) {
            return Result.Error(
                AppError.PermissionError("Not a member of this portfolio")
            )
        }
        if (role != PortfolioRole.OWNER) {
            return Result.Error(
                AppError.PermissionError("Only portfolio owners can delete portfolios")
            )
        }

        return portfolioRepository.deletePortfolio(params.portfolioId)
    }
}