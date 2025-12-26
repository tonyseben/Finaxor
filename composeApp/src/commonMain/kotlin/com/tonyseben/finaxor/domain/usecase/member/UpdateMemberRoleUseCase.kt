package com.tonyseben.finaxor.domain.usecase.member

import com.tonyseben.finaxor.core.AppError
import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.domain.model.PortfolioRole
import com.tonyseben.finaxor.domain.repository.PortfolioRepository
import com.tonyseben.finaxor.domain.usecase.UseCase

class UpdateMemberRoleUseCase(
    private val portfolioRepository: PortfolioRepository
) : UseCase<UpdateMemberRoleUseCase.Params, Unit> {

    data class Params(
        val portfolioId: String,
        val userId: String,
        val newRole: PortfolioRole
    )

    override suspend fun invoke(params: Params): Result<Unit> {
        // Check if user is the last admin
        if (params.newRole != PortfolioRole.ADMIN) {
            val isLastAdmin = portfolioRepository.isLastAdmin(params.portfolioId, params.userId)
            if (isLastAdmin is Result.Success && isLastAdmin.data) {
                return Result.Error(
                    AppError.BusinessError(
                        "Cannot change role of the last admin. Add another admin first."
                    )
                )
            }
        }

        return portfolioRepository.updateMemberRole(
            params.portfolioId,
            params.userId,
            params.newRole
        )
    }
}