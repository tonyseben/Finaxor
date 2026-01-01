package com.tonyseben.finaxor.domain.usecase.member

import com.tonyseben.finaxor.core.AppError
import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.domain.repository.PortfolioRepository
import com.tonyseben.finaxor.domain.usecase.UseCase

class RemoveMemberUseCase(
    private val portfolioRepository: PortfolioRepository
) : UseCase<RemoveMemberUseCase.Params, Unit> {

    data class Params(val portfolioId: String, val userId: String)

    override suspend fun invoke(params: Params): Result<Unit> {
        // Check if user is the last owner
        val isLastOwner = portfolioRepository.isLastOwner(params.portfolioId, params.userId)
        if (isLastOwner is Result.Success && isLastOwner.data) {
            return Result.Error(
                AppError.BusinessError(
                    "Cannot remove the last owner from the portfolio"
                )
            )
        }

        return portfolioRepository.removeMember(params.portfolioId, params.userId)
    }
}
