package com.tonyseben.finaxor.domain.repository

import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.domain.model.Portfolio
import com.tonyseben.finaxor.domain.model.PortfolioRole
import com.tonyseben.finaxor.domain.model.User
import com.tonyseben.finaxor.domain.model.UserPortfolio
import kotlinx.coroutines.flow.Flow

/**
 * Portfolio Repository Interface
 * Defines contract for portfolio-related data operations
 */
interface PortfolioRepository {

    // Portfolio CRUD
    suspend fun createPortfolio(name: String, userId: String): Result<Portfolio>
    suspend fun getPortfolio(portfolioId: String): Result<Portfolio>
    suspend fun updatePortfolio(portfolioId: String, name: String): Result<Unit>
    suspend fun deletePortfolio(portfolioId: String): Result<Unit>

    // Portfolio queries
    fun getUserPortfolios(userId: String): Flow<Result<List<UserPortfolio>>>

    // Member management
    suspend fun addMember(
        portfolioId: String,
        userId: String,
        role: PortfolioRole,
        addedBy: String
    ): Result<Unit>

    suspend fun updateMemberRole(
        portfolioId: String,
        userId: String,
        newRole: PortfolioRole
    ): Result<Unit>

    suspend fun removeMember(portfolioId: String, userId: String): Result<Unit>

    fun getPortfolioMembers(portfolioId: String): Flow<Result<List<User>>>

    suspend fun getMemberRole(portfolioId: String, userId: String): Result<PortfolioRole?>

    suspend fun isLastOwner(portfolioId: String, userId: String): Result<Boolean>
}