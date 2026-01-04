package com.tonyseben.finaxor.data.repository

import com.tonyseben.finaxor.core.AppError
import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.core.currentTimeMillis
import com.tonyseben.finaxor.core.toAppError
import com.tonyseben.finaxor.data.entity.FixedDepositEntity
import com.tonyseben.finaxor.data.mapper.toDomain
import com.tonyseben.finaxor.data.mapper.toFrequencyString
import com.tonyseben.finaxor.data.source.remote.FixedDepositRemoteDataSource
import com.tonyseben.finaxor.domain.model.FixedDeposit
import com.tonyseben.finaxor.domain.model.PayoutFrequency
import com.tonyseben.finaxor.domain.repository.FixedDepositRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlin.time.Clock

class FixedDepositRepositoryImpl(
    private val dataSource: FixedDepositRemoteDataSource
) : FixedDepositRepository {

    override suspend fun create(
        portfolioId: String,
        bankName: String,
        accountNumber: String,
        principalAmount: Double,
        interestRate: Double,
        startDate: Long,
        maturityDate: Long,
        payoutFrequency: PayoutFrequency,
        createdBy: String
    ): Result<String> {
        return try {
            val entity = FixedDepositEntity(
                portfolioId = portfolioId,
                bankName = bankName,
                accountNumber = accountNumber,
                principalAmount = principalAmount,
                interestRate = interestRate,
                startDate = startDate,
                maturityDate = maturityDate,
                payoutFrequency = payoutFrequency.toFrequencyString(),
                createdBy = createdBy
            )

            val id = dataSource.create(portfolioId, entity)
            Result.Success(id)
        } catch (e: Exception) {
            Result.Error(e.toAppError())
        }
    }

    override suspend fun update(
        portfolioId: String,
        fdId: String,
        bankName: String,
        accountNumber: String,
        principalAmount: Double,
        interestRate: Double,
        startDate: Long,
        maturityDate: Long,
        payoutFrequency: PayoutFrequency
    ): Result<Unit> {
        return try {
            val entity = FixedDepositEntity(
                id = fdId,
                portfolioId = portfolioId,
                bankName = bankName,
                accountNumber = accountNumber,
                principalAmount = principalAmount,
                interestRate = interestRate,
                startDate = startDate,
                maturityDate = maturityDate,
                payoutFrequency = payoutFrequency.toFrequencyString()
            )

            dataSource.update(portfolioId, fdId, entity)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.toAppError())
        }
    }

    override suspend fun delete(portfolioId: String, fdId: String): Result<Unit> {
        return try {
            dataSource.delete(portfolioId, fdId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.toAppError())
        }
    }

    override suspend fun getById(portfolioId: String, fdId: String): Result<FixedDeposit> {
        return try {
            val entity = dataSource.getById(portfolioId, fdId)
            Result.Success(entity.toDomain())
        } catch (e: Exception) {
            Result.Error(e.toAppError())
        }
    }

    override fun getByPortfolio(portfolioId: String): Flow<Result<List<FixedDeposit>>> {
        return dataSource.getByPortfolio(portfolioId)
            .map { entities ->
                Result.Success(entities.toDomain()) as Result<List<FixedDeposit>>
            }
            .catch { e ->
                emit(Result.Error((e as? Exception)?.toAppError() ?: AppError.UnknownError()))
            }
    }

    override fun getActiveByPortfolio(portfolioId: String): Flow<Result<List<FixedDeposit>>> {
        return dataSource.getByPortfolio(portfolioId)
            .map { entities ->
                val now = currentTimeMillis()
                val activeFDs = entities.toDomain().filter { fd ->
                    now >= fd.startDate && now < fd.maturityDate
                }
                Result.Success(activeFDs) as Result<List<FixedDeposit>>
            }
            .catch { e ->
                emit(Result.Error((e as? Exception)?.toAppError() ?: AppError.UnknownError()))
            }
    }

    override fun getMaturedByPortfolio(portfolioId: String): Flow<Result<List<FixedDeposit>>> {
        return dataSource.getByPortfolio(portfolioId)
            .map { entities ->
                val now = currentTimeMillis()
                val maturedFDs = entities.toDomain().filter { fd ->
                    now >= fd.maturityDate
                }
                Result.Success(maturedFDs) as Result<List<FixedDeposit>>
            }
            .catch { e ->
                emit(Result.Error((e as? Exception)?.toAppError() ?: AppError.UnknownError()))
            }
    }
}