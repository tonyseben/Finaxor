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
        // Check if user is the last admin
        val isLastAdmin = portfolioRepository.isLastAdmin(params.portfolioId, params.userId)
        if (isLastAdmin is Result.Success && isLastAdmin.data) {
            return Result.Error(
                AppError.BusinessError(
                    "Cannot remove the last admin from the portfolio"
                )
            )
        }

        return portfolioRepository.removeMember(params.portfolioId, params.userId)
    }
}
