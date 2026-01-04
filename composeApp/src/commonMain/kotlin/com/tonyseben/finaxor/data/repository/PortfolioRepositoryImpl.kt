package com.tonyseben.finaxor.data.repository

import com.tonyseben.finaxor.core.AppError
import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.core.toAppError
import com.tonyseben.finaxor.data.entity.PortfolioAccessEntity
import com.tonyseben.finaxor.data.entity.PortfolioEntity
import com.tonyseben.finaxor.data.entity.PortfolioMemberEntity
import com.tonyseben.finaxor.data.mapper.toDomain
import com.tonyseben.finaxor.data.mapper.toPortfolioRole
import com.tonyseben.finaxor.data.mapper.toRoleString
import com.tonyseben.finaxor.data.source.remote.PortfolioRemoteDataSource
import com.tonyseben.finaxor.data.source.remote.UserRemoteDataSource
import com.tonyseben.finaxor.domain.model.Portfolio
import com.tonyseben.finaxor.domain.model.PortfolioRole
import com.tonyseben.finaxor.domain.model.User
import com.tonyseben.finaxor.domain.model.UserPortfolio
import com.tonyseben.finaxor.domain.repository.PortfolioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class PortfolioRepositoryImpl(
    private val portfolioDataSource: PortfolioRemoteDataSource,
    private val userDataSource: UserRemoteDataSource
) : PortfolioRepository {

    override suspend fun createPortfolio(name: String, userId: String): Result<Portfolio> {
        return try {
            val portfolioEntity = PortfolioEntity(
                name = name,
                createdBy = userId
            )
            val memberEntity = PortfolioMemberEntity(
                userId = userId,
                portfolioId = "", // Will be set by batch operation
                role = PortfolioRole.OWNER.value,
                addedBy = userId
            )
            val accessEntity = PortfolioAccessEntity(
                portfolioId = "", // Will be set by batch operation
                portfolioName = name,
                role = PortfolioRole.OWNER.value
            )

            // Atomic batch operation: creates portfolio, member, and access together
            val portfolioId = portfolioDataSource.createPortfolioWithMember(
                portfolioEntity, memberEntity, accessEntity, userId
            )

            Result.Success(portfolioEntity.copy(id = portfolioId).toDomain())
        } catch (e: Exception) {
            Result.Error(e.toAppError())
        }
    }

    override suspend fun getPortfolio(portfolioId: String): Result<Portfolio> {
        return try {
            val entity = portfolioDataSource.getPortfolio(portfolioId)
            Result.Success(entity.toDomain())
        } catch (e: Exception) {
            Result.Error(e.toAppError())
        }
    }

    override suspend fun updatePortfolio(portfolioId: String, name: String): Result<Unit> {
        return try {
            portfolioDataSource.updatePortfolio(portfolioId, name)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.toAppError())
        }
    }

    override suspend fun deletePortfolio(portfolioId: String): Result<Unit> {
        return try {
            portfolioDataSource.deletePortfolio(portfolioId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.toAppError())
        }
    }

    override fun getUserPortfolios(userId: String): Flow<Result<List<UserPortfolio>>> {
        return portfolioDataSource.getUserPortfolioAccess(userId)
            .map<_, Result<List<UserPortfolio>>> { accessList ->
                val portfolios = accessList.mapNotNull { access ->
                    access.role.toPortfolioRole()?.let { role ->
                        UserPortfolio(
                            id = access.portfolioId,
                            name = access.portfolioName,
                            role = role
                        )
                    }
                }
                Result.Success(portfolios)
            }
            .catch { e ->
                emit(Result.Error((e as? Exception)?.toAppError() ?: AppError.UnknownError()))
            }
    }

    override suspend fun addMember(
        portfolioId: String,
        userId: String,
        role: PortfolioRole,
        addedBy: String
    ): Result<Unit> {
        return try {
            // Get portfolio name first (needed for access entry)
            val portfolio = portfolioDataSource.getPortfolio(portfolioId)

            val memberEntity = PortfolioMemberEntity(
                userId = userId,
                portfolioId = portfolioId,
                role = role.toRoleString(),
                addedBy = addedBy
            )
            val accessEntity = PortfolioAccessEntity(
                portfolioId = portfolioId,
                portfolioName = portfolio.name,
                role = role.toRoleString()
            )

            // Atomic batch operation: adds member and access together
            portfolioDataSource.addMemberWithAccess(portfolioId, memberEntity, accessEntity, userId)

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.toAppError())
        }
    }

    override suspend fun updateMemberRole(
        portfolioId: String,
        userId: String,
        newRole: PortfolioRole
    ): Result<Unit> {
        return try {
            val updates = mapOf("role" to newRole.toRoleString())

            portfolioDataSource.updateMember(portfolioId, userId, updates)
            portfolioDataSource.updatePortfolioAccess(userId, portfolioId, updates)

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.toAppError())
        }
    }

    override suspend fun removeMember(portfolioId: String, userId: String): Result<Unit> {
        return try {
            portfolioDataSource.deleteMember(portfolioId, userId)
            portfolioDataSource.deletePortfolioAccess(userId, portfolioId)

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.toAppError())
        }
    }

    override fun getPortfolioMembers(portfolioId: String): Flow<Result<List<User>>> {
        return portfolioDataSource.getMembers(portfolioId)
            .map { members ->
                try {
                    val users = members.map { member ->
                        // Don't silently filter out failed lookups - propagate errors
                        userDataSource.getUser(member.userId).toDomain()
                    }
                    Result.Success(users)
                } catch (e: Exception) {
                    Result.Error(e.toAppError())
                }
            }
            .catch { e ->
                emit(Result.Error((e as? Exception)?.toAppError() ?: AppError.UnknownError()))
            }
    }

    override suspend fun getMemberRole(
        portfolioId: String,
        userId: String
    ): Result<PortfolioRole?> {
        return try {
            val member = portfolioDataSource.getMember(portfolioId, userId)
            Result.Success(member.role.toPortfolioRole())
        } catch (e: Exception) {
            Result.Error(e.toAppError())
        }
    }

    override suspend fun isLastOwner(portfolioId: String, userId: String): Result<Boolean> {
        return try {
            // Uses single read to avoid race condition
            Result.Success(portfolioDataSource.isLastOwner(portfolioId, userId))
        } catch (e: Exception) {
            Result.Error(e.toAppError())
        }
    }
}