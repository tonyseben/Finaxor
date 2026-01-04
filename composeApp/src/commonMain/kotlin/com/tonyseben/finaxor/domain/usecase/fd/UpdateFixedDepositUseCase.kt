package com.tonyseben.finaxor.domain.usecase.fd

import com.tonyseben.finaxor.core.AppError
import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.domain.model.PayoutFrequency
import com.tonyseben.finaxor.domain.repository.FixedDepositRepository
import com.tonyseben.finaxor.domain.repository.PortfolioRepository
import com.tonyseben.finaxor.domain.usecase.UseCase

class UpdateFixedDepositUseCase(
    private val fdRepository: FixedDepositRepository,
    private val portfolioRepository: PortfolioRepository
) : UseCase<UpdateFixedDepositUseCase.Params, Unit> {

    data class Params(
        val portfolioId: String,
        val fdId: String,
        val bankName: String,
        val accountNumber: String,
        val principalAmount: Double,
        val interestRate: Double,
        val startDate: Long,
        val maturityDate: Long,
        val payoutFrequency: PayoutFrequency,
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

        // Validation
        if (params.bankName.isBlank()) {
            return Result.Error(AppError.ValidationError("bankName", "Bank name cannot be empty"))
        }

        if (params.principalAmount <= 0) {
            return Result.Error(
                AppError.ValidationError(
                    "principalAmount",
                    "Principal amount must be positive"
                )
            )
        }

        if (params.interestRate <= 0 || params.interestRate > 100) {
            return Result.Error(
                AppError.ValidationError(
                    "interestRate",
                    "Interest rate must be between 0 and 100"
                )
            )
        }

        if (params.startDate >= params.maturityDate) {
            return Result.Error(
                AppError.ValidationError(
                    "maturityDate",
                    "Maturity date must be after start date"
                )
            )
        }

        return fdRepository.update(
            portfolioId = params.portfolioId,
            fdId = params.fdId,
            bankName = params.bankName,
            accountNumber = params.accountNumber,
            principalAmount = params.principalAmount,
            interestRate = params.interestRate,
            startDate = params.startDate,
            maturityDate = params.maturityDate,
            payoutFrequency = params.payoutFrequency
        )
    }
}