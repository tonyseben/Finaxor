package com.tonyseben.finaxor.domain.repository

import com.tonyseben.finaxor.domain.model.FixedDeposit
import com.tonyseben.finaxor.domain.model.PayoutFrequency
import kotlinx.coroutines.flow.Flow

/**
 * Fixed Deposit Repository Interface
 * Defines contract for fixed deposit data operations
 */
interface FixedDepositRepository {

    suspend fun create(
        portfolioId: String,
        bankName: String,
        accountNumber: String,
        principalAmount: Double,
        interestRate: Double,
        startDate: Long,
        maturityDate: Long,
        interestPayoutFreq: PayoutFrequency,
        createdBy: String
    ): Result<String>

    suspend fun update(
        portfolioId: String,
        fdId: String,
        bankName: String,
        accountNumber: String,
        principalAmount: Double,
        interestRate: Double,
        startDate: Long,
        maturityDate: Long,
        interestPayoutFreq: PayoutFrequency
    ): Result<Unit>

    suspend fun delete(portfolioId: String, fdId: String): Result<Unit>

    suspend fun getById(portfolioId: String, fdId: String): Result<FixedDeposit>

    fun getByPortfolio(portfolioId: String): Flow<Result<List<FixedDeposit>>>

    fun getActiveByPortfolio(portfolioId: String): Flow<Result<List<FixedDeposit>>>

    fun getMaturedByPortfolio(portfolioId: String): Flow<Result<List<FixedDeposit>>>
}