package com.tonyseben.finaxor.data.source.remote

import com.tonyseben.finaxor.core.generateFirestoreId
import com.tonyseben.finaxor.core.toEpochMillis
import com.tonyseben.finaxor.data.entity.PortfolioAccessEntity
import com.tonyseben.finaxor.data.entity.PortfolioEntity
import com.tonyseben.finaxor.data.entity.PortfolioMemberEntity
import com.tonyseben.finaxor.domain.model.PortfolioRole
import dev.gitlive.firebase.firestore.DocumentSnapshot
import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.Timestamp
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

    // Manual mapping functions to handle Firestore Timestamps
    private fun DocumentSnapshot.toPortfolioEntity(): PortfolioEntity {
        return PortfolioEntity(
            id = get<String?>("id") ?: id,
            name = get<String?>("name") ?: "",
            createdBy = get<String?>("createdBy") ?: "",
            createdAt = get<Timestamp?>("createdAt")?.toEpochMillis() ?: 0L,
            updatedAt = get<Timestamp?>("updatedAt")?.toEpochMillis() ?: 0L
        )
    }

    private fun DocumentSnapshot.toPortfolioAccessEntity(): PortfolioAccessEntity {
        return PortfolioAccessEntity(
            portfolioId = get<String?>("portfolioId") ?: id,
            portfolioName = get<String?>("portfolioName") ?: "",
            role = get<String?>("role") ?: "",
            addedAt = get<Timestamp?>("addedAt")?.toEpochMillis() ?: 0L
        )
    }

    private fun DocumentSnapshot.toPortfolioMemberEntity(): PortfolioMemberEntity {
        return PortfolioMemberEntity(
            userId = get<String?>("userId") ?: id,
            portfolioId = get<String?>("portfolioId") ?: "",
            role = get<String?>("role") ?: "",
            addedBy = get<String?>("addedBy") ?: "",
            addedAt = get<Timestamp?>("addedAt")?.toEpochMillis() ?: 0L
        )
    }

    // Portfolio operations
    suspend fun createPortfolio(entity: PortfolioEntity): String {
        val id = generateFirestoreId()
        val ref = firestore.collection(COLLECTION_PORTFOLIOS).document(id)
        ref.set(
            mapOf(
                "id" to ref.id,
                "name" to entity.name,
                "createdBy" to entity.createdBy,
                "createdAt" to FieldValue.serverTimestamp,
                "updatedAt" to FieldValue.serverTimestamp
            )
        )
        return ref.id
    }

    suspend fun getPortfolio(portfolioId: String): PortfolioEntity {
        return firestore
            .collection(COLLECTION_PORTFOLIOS)
            .document(portfolioId)
            .get()
            .toPortfolioEntity()
    }

    suspend fun updatePortfolio(portfolioId: String, name: String) {
        firestore
            .collection(COLLECTION_PORTFOLIOS)
            .document(portfolioId)
            .update(
                mapOf(
                    "name" to name,
                    "updatedAt" to FieldValue.serverTimestamp
                )
            )
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
                snapshot.documents.map { doc ->
                    doc.toPortfolioAccessEntity()
                }
            }
    }

    // Member operations
    suspend fun addMember(portfolioId: String, entity: PortfolioMemberEntity) {
        firestore
            .collection(COLLECTION_PORTFOLIOS)
            .document(portfolioId)
            .collection(COLLECTION_MEMBERS)
            .document(entity.userId)
            .set(
                mapOf(
                    "userId" to entity.userId,
                    "portfolioId" to entity.portfolioId,
                    "role" to entity.role,
                    "addedBy" to entity.addedBy,
                    "addedAt" to FieldValue.serverTimestamp
                )
            )
    }

    suspend fun getMember(portfolioId: String, userId: String): PortfolioMemberEntity {
        return firestore
            .collection(COLLECTION_PORTFOLIOS)
            .document(portfolioId)
            .collection(COLLECTION_MEMBERS)
            .document(userId)
            .get()
            .toPortfolioMemberEntity()
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
                snapshot.documents.map { it.toPortfolioMemberEntity() }
            }
    }

    suspend fun getOwnerCount(portfolioId: String): Int {
        val snapshot = firestore
            .collection(COLLECTION_PORTFOLIOS)
            .document(portfolioId)
            .collection(COLLECTION_MEMBERS)
            .get()

        return snapshot.documents.count { doc ->
            val member = doc.toPortfolioMemberEntity()
            member.role == PortfolioRole.OWNER.value
        }
    }

    /**
     * Atomically checks if the given user is the last owner of the portfolio.
     * Uses a single read to avoid race conditions.
     */
    suspend fun isLastOwner(portfolioId: String, userId: String): Boolean {
        val snapshot = firestore
            .collection(COLLECTION_PORTFOLIOS)
            .document(portfolioId)
            .collection(COLLECTION_MEMBERS)
            .get()

        val members = snapshot.documents.map { it.toPortfolioMemberEntity() }
        val owners = members.filter { it.role == PortfolioRole.OWNER.value }

        return owners.size == 1 && owners.first().userId == userId
    }

    // Portfolio access operations
    suspend fun addPortfolioAccess(userId: String, entity: PortfolioAccessEntity) {
        firestore
            .collection(COLLECTION_USERS)
            .document(userId)
            .collection(COLLECTION_PORTFOLIO_ACCESS)
            .document(entity.portfolioId)
            .set(
                mapOf(
                    "portfolioId" to entity.portfolioId,
                    "portfolioName" to entity.portfolioName,
                    "role" to entity.role,
                    "addedAt" to FieldValue.serverTimestamp
                )
            )
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

    // Batch operations for transactional consistency
    suspend fun createPortfolioWithMember(
        portfolioEntity: PortfolioEntity,
        memberEntity: PortfolioMemberEntity,
        accessEntity: PortfolioAccessEntity,
        userId: String
    ): String {
        val portfolioId = generateFirestoreId()

        val portfolioRef = firestore.collection(COLLECTION_PORTFOLIOS).document(portfolioId)
        val memberRef = portfolioRef.collection(COLLECTION_MEMBERS).document(memberEntity.userId)
        val accessRef = firestore.collection(COLLECTION_USERS)
            .document(userId)
            .collection(COLLECTION_PORTFOLIO_ACCESS)
            .document(portfolioId)

        firestore.batch().apply {
            // Create portfolio
            set(
                portfolioRef,
                mapOf(
                    "id" to portfolioId,
                    "name" to portfolioEntity.name,
                    "createdBy" to portfolioEntity.createdBy,
                    "createdAt" to FieldValue.serverTimestamp,
                    "updatedAt" to FieldValue.serverTimestamp
                )
            )

            // Add member
            set(
                memberRef,
                mapOf(
                    "userId" to memberEntity.userId,
                    "portfolioId" to portfolioId,
                    "role" to memberEntity.role,
                    "addedBy" to memberEntity.addedBy,
                    "addedAt" to FieldValue.serverTimestamp
                )
            )

            // Add portfolio access
            set(
                accessRef,
                mapOf(
                    "portfolioId" to portfolioId,
                    "portfolioName" to accessEntity.portfolioName,
                    "role" to accessEntity.role,
                    "addedAt" to FieldValue.serverTimestamp
                )
            )
        }.commit()

        return portfolioId
    }

    suspend fun addMemberWithAccess(
        portfolioId: String,
        memberEntity: PortfolioMemberEntity,
        accessEntity: PortfolioAccessEntity,
        userId: String
    ) {
        val memberRef = firestore.collection(COLLECTION_PORTFOLIOS)
            .document(portfolioId)
            .collection(COLLECTION_MEMBERS)
            .document(memberEntity.userId)
        val accessRef = firestore.collection(COLLECTION_USERS)
            .document(userId)
            .collection(COLLECTION_PORTFOLIO_ACCESS)
            .document(portfolioId)

        firestore.batch().apply {
            // Add member
            set(
                memberRef,
                mapOf(
                    "userId" to memberEntity.userId,
                    "portfolioId" to portfolioId,
                    "role" to memberEntity.role,
                    "addedBy" to memberEntity.addedBy,
                    "addedAt" to FieldValue.serverTimestamp
                )
            )

            // Add portfolio access
            set(
                accessRef,
                mapOf(
                    "portfolioId" to accessEntity.portfolioId,
                    "portfolioName" to accessEntity.portfolioName,
                    "role" to accessEntity.role,
                    "addedAt" to FieldValue.serverTimestamp
                )
            )
        }.commit()
    }
}