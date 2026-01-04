package com.tonyseben.finaxor.data.source.remote

import com.tonyseben.finaxor.core.generateFirestoreId
import com.tonyseben.finaxor.core.toEpochMillis
import com.tonyseben.finaxor.data.entity.FixedDepositEntity
import dev.gitlive.firebase.firestore.DocumentSnapshot
import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.Timestamp
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

    private fun DocumentSnapshot.toFDEntity(): FixedDepositEntity {
        return FixedDepositEntity(
            id = get<String?>("id") ?: id,
            portfolioId = get<String?>("portfolioId") ?: "",
            bankName = get<String?>("bankName") ?: "",
            accountNumber = get<String?>("accountNumber") ?: "",
            principalAmount = get<Double?>("principalAmount") ?: 0.0,
            interestRate = get<Double?>("interestRate") ?: 0.0,
            startDate = get<Long?>("startDate") ?: 0L,
            maturityDate = get<Long?>("maturityDate") ?: 0L,
            payoutFrequency = get<String?>("payoutFrequency") ?: "",
            createdAt = get<Timestamp?>("createdAt")?.toEpochMillis() ?: 0L,
            updatedAt = get<Timestamp?>("updatedAt")?.toEpochMillis() ?: 0L,
            createdBy = get<String?>("createdBy") ?: ""
        )
    }

    suspend fun create(portfolioId: String, entity: FixedDepositEntity): String {
        val id = generateFirestoreId()
        val ref = firestore
            .collection(COLLECTION_PORTFOLIOS)
            .document(portfolioId)
            .collection(COLLECTION_FDS)
            .document(id)

        val data = mapOf(
            "id" to ref.id,
            "portfolioId" to portfolioId,
            "bankName" to entity.bankName,
            "accountNumber" to entity.accountNumber,
            "principalAmount" to entity.principalAmount,
            "interestRate" to entity.interestRate,
            "startDate" to entity.startDate,
            "maturityDate" to entity.maturityDate,
            "payoutFrequency" to entity.payoutFrequency,
            "createdBy" to entity.createdBy,
            "createdAt" to FieldValue.serverTimestamp,
            "updatedAt" to FieldValue.serverTimestamp
        )

        ref.set(data)
        return ref.id
    }

    suspend fun update(portfolioId: String, fdId: String, entity: FixedDepositEntity) {
        val data = mapOf(
            "bankName" to entity.bankName,
            "accountNumber" to entity.accountNumber,
            "principalAmount" to entity.principalAmount,
            "interestRate" to entity.interestRate,
            "startDate" to entity.startDate,
            "maturityDate" to entity.maturityDate,
            "payoutFrequency" to entity.payoutFrequency,
            "updatedAt" to FieldValue.serverTimestamp
        )

        firestore
            .collection(COLLECTION_PORTFOLIOS)
            .document(portfolioId)
            .collection(COLLECTION_FDS)
            .document(fdId)
            .update(data)
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
            .toFDEntity()
    }

    fun getByPortfolio(portfolioId: String): Flow<List<FixedDepositEntity>> {
        return firestore
            .collection(COLLECTION_PORTFOLIOS)
            .document(portfolioId)
            .collection(COLLECTION_FDS)
            .snapshots
            .map { snapshot ->
                snapshot.documents
                    .map { it.toFDEntity() }
                    .sortedByDescending { it.createdAt }
            }
    }

    suspend fun getAllByPortfolio(portfolioId: String): List<FixedDepositEntity> {
        val snapshot = firestore
            .collection(COLLECTION_PORTFOLIOS)
            .document(portfolioId)
            .collection(COLLECTION_FDS)
            .get()

        return snapshot.documents.map { it.toFDEntity() }
    }
}