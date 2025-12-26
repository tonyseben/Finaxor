package com.tonyseben.finaxor.data.source.remote

import com.tonyseben.finaxor.core.generateFirestoreId
import com.tonyseben.finaxor.data.entity.FixedDepositEntity
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Fixed Deposit Remote Data Source
 * Handles Firestore operations for fixed deposits
 */
class FixedDepositRemoteDataSource(private val firestore: FirebaseFirestore) {

    private companion object {
        const val COLLECTION_PORTFOLIOS = "portfolios"
        const val COLLECTION_FDS = "fixedDeposits"
    }

    suspend fun create(portfolioId: String, entity: FixedDepositEntity): String {
        val id = generateFirestoreId()
        val ref = firestore
            .collection(COLLECTION_PORTFOLIOS)
            .document(portfolioId)
            .collection(COLLECTION_FDS)
            .document(id)

        val fdWithId = entity.copy(
            id = ref.id,
            portfolioId = portfolioId
        )

        ref.set(fdWithId)
        return ref.id
    }

    suspend fun update(portfolioId: String, fdId: String, updates: Map<String, Any>) {
        firestore
            .collection(COLLECTION_PORTFOLIOS)
            .document(portfolioId)
            .collection(COLLECTION_FDS)
            .document(fdId)
            .update(updates)
    }

    suspend fun delete(portfolioId: String, fdId: String) {
        firestore
            .collection(COLLECTION_PORTFOLIOS)
            .document(portfolioId)
            .collection(COLLECTION_FDS)
            .document(fdId)
            .delete()
    }

    suspend fun getById(portfolioId: String, fdId: String): FixedDepositEntity {
        return firestore
            .collection(COLLECTION_PORTFOLIOS)
            .document(portfolioId)
            .collection(COLLECTION_FDS)
            .document(fdId)
            .get()
            .data()
    }

    fun getByPortfolio(portfolioId: String): Flow<List<FixedDepositEntity>> {
        return firestore
            .collection(COLLECTION_PORTFOLIOS)
            .document(portfolioId)
            .collection(COLLECTION_FDS)
            .snapshots
            .map { snapshot ->
                snapshot.documents
                    .map { it.data<FixedDepositEntity>() }
                    .sortedByDescending { it.createdAt }
            }
    }

    suspend fun getAllByPortfolio(portfolioId: String): List<FixedDepositEntity> {
        val snapshot = firestore
            .collection(COLLECTION_PORTFOLIOS)
            .document(portfolioId)
            .collection(COLLECTION_FDS)
            .get()

        return snapshot.documents.map { it.data() }
    }
}