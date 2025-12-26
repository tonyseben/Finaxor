package com.tonyseben.finaxor.data.source.remote

import com.tonyseben.finaxor.core.generateFirestoreId
import com.tonyseben.finaxor.data.entity.PortfolioAccessEntity
import com.tonyseben.finaxor.data.entity.PortfolioEntity
import com.tonyseben.finaxor.data.entity.PortfolioMemberEntity
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Portfolio Remote Data Source
 * Handles Firestore operations for portfolios
 */
class PortfolioRemoteDataSource(private val firestore: FirebaseFirestore) {

    private companion object {
        const val COLLECTION_PORTFOLIOS = "portfolios"
        const val COLLECTION_MEMBERS = "members"
        const val COLLECTION_USERS = "users"
        const val COLLECTION_PORTFOLIO_ACCESS = "portfolioAccess"
    }

    // Portfolio operations
    suspend fun createPortfolio(entity: PortfolioEntity): String {
        val id = generateFirestoreId()
        val ref = firestore.collection(COLLECTION_PORTFOLIOS).document(id)
        val portfolioWithId = entity.copy(id = ref.id)
        ref.set(portfolioWithId)
        return ref.id
    }

    suspend fun getPortfolio(portfolioId: String): PortfolioEntity {
        return firestore
            .collection(COLLECTION_PORTFOLIOS)
            .document(portfolioId)
            .get()
            .data()
    }

    suspend fun updatePortfolio(portfolioId: String, updates: Map<String, Any>) {
        firestore
            .collection(COLLECTION_PORTFOLIOS)
            .document(portfolioId)
            .update(updates)
    }

    suspend fun deletePortfolio(portfolioId: String) {
        firestore
            .collection(COLLECTION_PORTFOLIOS)
            .document(portfolioId)
            .delete()
    }

    fun getUserPortfolioAccess(userId: String): Flow<List<PortfolioAccessEntity>> {
        return firestore
            .collection(COLLECTION_USERS)
            .document(userId)
            .collection(COLLECTION_PORTFOLIO_ACCESS)
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { it.data<PortfolioAccessEntity>() }
            }
    }

    // Member operations
    suspend fun addMember(portfolioId: String, entity: PortfolioMemberEntity) {
        firestore
            .collection(COLLECTION_PORTFOLIOS)
            .document(portfolioId)
            .collection(COLLECTION_MEMBERS)
            .document(entity.userId)
            .set(entity)
    }

    suspend fun getMember(portfolioId: String, userId: String): PortfolioMemberEntity {
        return firestore
            .collection(COLLECTION_PORTFOLIOS)
            .document(portfolioId)
            .collection(COLLECTION_MEMBERS)
            .document(userId)
            .get()
            .data()
    }

    suspend fun updateMember(portfolioId: String, userId: String, updates: Map<String, Any>) {
        firestore
            .collection(COLLECTION_PORTFOLIOS)
            .document(portfolioId)
            .collection(COLLECTION_MEMBERS)
            .document(userId)
            .update(updates)
    }

    suspend fun deleteMember(portfolioId: String, userId: String) {
        firestore
            .collection(COLLECTION_PORTFOLIOS)
            .document(portfolioId)
            .collection(COLLECTION_MEMBERS)
            .document(userId)
            .delete()
    }

    fun getMembers(portfolioId: String): Flow<List<PortfolioMemberEntity>> {
        return firestore
            .collection(COLLECTION_PORTFOLIOS)
            .document(portfolioId)
            .collection(COLLECTION_MEMBERS)
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { it.data() }
            }
    }

    suspend fun getAdminCount(portfolioId: String): Int {
        val snapshot = firestore
            .collection(COLLECTION_PORTFOLIOS)
            .document(portfolioId)
            .collection(COLLECTION_MEMBERS)
            .get()

        return snapshot.documents.count { doc ->
            val member = doc.data<PortfolioMemberEntity>()
            member.role == "admin"
        }
    }

    // Portfolio access operations
    suspend fun addPortfolioAccess(userId: String, entity: PortfolioAccessEntity) {
        firestore
            .collection(COLLECTION_USERS)
            .document(userId)
            .collection(COLLECTION_PORTFOLIO_ACCESS)
            .document(entity.portfolioId)
            .set(entity)
    }

    suspend fun updatePortfolioAccess(
        userId: String,
        portfolioId: String,
        updates: Map<String, Any>
    ) {
        firestore
            .collection(COLLECTION_USERS)
            .document(userId)
            .collection(COLLECTION_PORTFOLIO_ACCESS)
            .document(portfolioId)
            .update(updates)
    }

    suspend fun deletePortfolioAccess(userId: String, portfolioId: String) {
        firestore
            .collection(COLLECTION_USERS)
            .document(userId)
            .collection(COLLECTION_PORTFOLIO_ACCESS)
            .document(portfolioId)
            .delete()
    }
}