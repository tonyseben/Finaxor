package com.tonyseben.finaxor.domain.usecase.fd

import com.tonyseben.finaxor.core.AppError
import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.domain.model.PayoutFrequency
import com.tonyseben.finaxor.domain.repository.FixedDepositRepository
import com.tonyseben.finaxor.domain.usecase.UseCase

class CreateFixedDepositUseCase(
    private val fdRepository: FixedDepositRepository
) : UseCase<CreateFixedDepositUseCase.Params, String> {

    data class Params(
        val portfolioId: String,
        val bankName: String,
        val accountNumber: String,
        val principalAmount: Double,
        val interestRate: Double,
        val startDate: Long,
        val maturityDate: Long,
        val interestPayoutFreq: PayoutFrequency,
        val createdBy: String
    )

    override suspend fun invoke(params: Params): Result<String> {
        // Validate
        if (params.bankName.isBlank()) {
            return Result.Error(AppError.ValidationError("bankName", "Bank name cannot be empty"))
        }

        if (params.accountNumber.isBlank()) {
            return Result.Error(
                AppError.ValidationError(
                    "accountNumber",
                    "Account number cannot be empty"
                )
            )
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

        return fdRepository.create(
            portfolioId = params.portfolioId,
            bankName = params.bankName,
            accountNumber = params.accountNumber,
            principalAmount = params.principalAmount,
            interestRate = params.interestRate,
            startDate = params.startDate,
            maturityDate = params.maturityDate,
            interestPayoutFreq = params.interestPayoutFreq,
            createdBy = params.createdBy
        )
    }
}