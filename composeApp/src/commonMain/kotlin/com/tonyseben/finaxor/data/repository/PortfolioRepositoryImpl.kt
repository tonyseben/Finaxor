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
            // Create portfolio
            val portfolioEntity = PortfolioEntity(
                name = name,
                createdBy = userId
            )

            val portfolioId = portfolioDataSource.createPortfolio(portfolioEntity)

            // Add creator as owner member
            val memberEntity = PortfolioMemberEntity(
                userId = userId,
                portfolioId = portfolioId,
                role = "owner",
                addedBy = userId
            )
            portfolioDataSource.addMember(portfolioId, memberEntity)

            // Add to user's portfolio access
            val accessEntity = PortfolioAccessEntity(
                portfolioId = portfolioId,
                portfolioName = name,
                role = "owner"
            )
            portfolioDataSource.addPortfolioAccess(userId, accessEntity)

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
            val memberEntity = PortfolioMemberEntity(
                userId = userId,
                portfolioId = portfolioId,
                role = role.toRoleString(),
                addedBy = addedBy
            )

            portfolioDataSource.addMember(portfolioId, memberEntity)

            // Add to user's portfolio access
            val portfolio = portfolioDataSource.getPortfolio(portfolioId)
            val accessEntity = PortfolioAccessEntity(
                portfolioId = portfolioId,
                portfolioName = portfolio.name,
                role = role.toRoleString()
            )

            portfolioDataSource.addPortfolioAccess(userId, accessEntity)

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
                    val users = members.mapNotNull { member ->
                        try {
                            userDataSource.getUser(member.userId).toDomain()
                        } catch (e: Exception) {
                            null
                        }
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
            val ownerCount = portfolioDataSource.getOwnerCount(portfolioId)
            val member = portfolioDataSource.getMember(portfolioId, userId)
            val isOwner = member.role == "owner"

            Result.Success(ownerCount == 1 && isOwner)
        } catch (e: Exception) {
            Result.Error(e.toAppError())
        }
    }
}