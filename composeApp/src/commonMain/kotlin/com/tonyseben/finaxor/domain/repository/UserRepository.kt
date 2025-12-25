package com.tonyseben.finaxor.domain.repository

import com.tonyseben.finaxor.domain.model.User

/**
 * User Repository Interface
 * Defines contract for user-related data operations
 */
interface UserRepository {
    suspend fun createUser(
        userId: String,
        name: String,
        email: String,
        photoURL: String? = null
    ): Result<User>

    suspend fun getUser(userId: String): Result<User>

    suspend fun updateUser(
        userId: String,
        name: String? = null,
        photoURL: String? = null
    ): Result<Unit>

    suspend fun deleteUser(userId: String): Result<Unit>

    suspend fun findUserByEmail(email: String): Result<User?>
}