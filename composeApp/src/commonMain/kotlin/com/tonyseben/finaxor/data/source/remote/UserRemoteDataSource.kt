package com.tonyseben.finaxor.data.source.remote

import com.tonyseben.finaxor.data.entity.UserEntity
import dev.gitlive.firebase.firestore.FirebaseFirestore

/**
 * User Remote Data Source
 * Handles Firestore operations for users
 */
class UserRemoteDataSource(private val firestore: FirebaseFirestore) {

    private companion object {
        const val COLLECTION_USERS = "users"
    }

    suspend fun createUser(entity: UserEntity) {
        firestore
            .collection(COLLECTION_USERS)
            .document(entity.id)
            .set(entity)
    }

    suspend fun getUser(userId: String): UserEntity {
        return firestore
            .collection(COLLECTION_USERS)
            .document(userId)
            .get()
            .data()
    }

    suspend fun updateUser(userId: String, updates: Map<String, Any>) {
        firestore
            .collection(COLLECTION_USERS)
            .document(userId)
            .update(updates)
    }

    suspend fun deleteUser(userId: String) {
        firestore
            .collection(COLLECTION_USERS)
            .document(userId)
            .delete()
    }

    suspend fun findUserByEmail(email: String): UserEntity? {
        val snapshot = firestore
            .collection(COLLECTION_USERS)
            .where { "email" equalTo email }
            .get()

        return snapshot.documents.firstOrNull()?.data()
    }
}