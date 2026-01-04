package com.tonyseben.finaxor.domain.usecase.member

import com.tonyseben.finaxor.core.AppError
import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.domain.model.PortfolioRole
import com.tonyseben.finaxor.domain.repository.PortfolioRepository
import com.tonyseben.finaxor.domain.usecase.UseCase

class RemoveMemberUseCase(
    private val portfolioRepository: PortfolioRepository
) : UseCase<RemoveMemberUseCase.Params, Unit> {

    data class Params(
        val portfolioId: String,
        val userId: String,
        val currentUserId: String
    )

    override suspend fun invoke(params: Params): Result<Unit> {
        // Verify caller is OWNER
        val roleResult = portfolioRepository.getMemberRole(params.portfolioId, params.currentUserId)
        if (roleResult is Result.Error) return Result.Error(roleResult.error)

        val callerRole = (roleResult as Result.Success).data
        if (callerRole == null) {
            return Result.Error(
                AppError.PermissionError("Not a member of this portfolio")
            )
        }
        if (callerRole != PortfolioRole.OWNER) {
            return Result.Error(
                AppError.PermissionError("Only portfolio owners can remove members")
            )
        }

        // Check if user being removed is the last owner
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
