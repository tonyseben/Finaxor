package com.tonyseben.finaxor.domain.usecase.fd

import com.tonyseben.finaxor.core.AppError
import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.domain.repository.FixedDepositRepository
import com.tonyseben.finaxor.domain.repository.PortfolioRepository
import com.tonyseben.finaxor.domain.usecase.UseCase

class DeleteFixedDepositUseCase(
    private val fdRepository: FixedDepositRepository,
    private val portfolioRepository: PortfolioRepository
) : UseCase<DeleteFixedDepositUseCase.Params, Unit> {

    data class Params(
        val portfolioId: String,
        val fdId: String,
        val currentUserId: String
    )

    override suspend fun invoke(params: Params): Result<Unit> {
        // Verify user is a member of the portfolio
        val roleResult = portfolioRepository.getMemberRole(params.portfolioId, params.currentUserId)
        if (roleResult is Result.Error) return Result.Error(roleResult.error)

        val role = (roleResult as Result.Success).data
        if (role == null) {
            return Result.Error(
                AppError.PermissionError("Not a member of this portfolio")
            )
        }

        return fdRepository.delete(params.portfolioId, params.fdId)
    }
}