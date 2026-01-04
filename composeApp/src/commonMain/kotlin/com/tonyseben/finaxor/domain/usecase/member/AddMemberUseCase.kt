package com.tonyseben.finaxor.domain.usecase.member

import com.tonyseben.finaxor.core.AppError
import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.domain.model.PortfolioRole
import com.tonyseben.finaxor.domain.repository.PortfolioRepository
import com.tonyseben.finaxor.domain.repository.UserRepository
import com.tonyseben.finaxor.domain.usecase.UseCase

class AddMemberUseCase(
    private val portfolioRepository: PortfolioRepository,
    private val userRepository: UserRepository
) : UseCase<AddMemberUseCase.Params, Unit> {

    data class Params(
        val portfolioId: String,
        val userEmail: String,
        val role: PortfolioRole,
        val addedBy: String,
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
                AppError.PermissionError("Only portfolio owners can add members")
            )
        }

        // Validate email
        if (!params.userEmail.contains("@")) {
            return Result.Error(
                AppError.ValidationError(
                    field = "email",
                    message = "Invalid email format"
                )
            )
        }

        // Find user by email
        val userResult = userRepository.findUserByEmail(params.userEmail)
        if (userResult is Result.Error) {
            return Result.Error(userResult.error)
        }

        val user = (userResult as Result.Success).data
            ?: return Result.Error(
                AppError.NotFoundError(
                    resourceType = "User",
                    resourceId = params.userEmail
                )
            )

        // Add member
        return portfolioRepository.addMember(
            portfolioId = params.portfolioId,
            userId = user.id,
            role = params.role,
            addedBy = params.addedBy
        )
    }
}