package com.tonyseben.finaxor.data.repository

import com.tonyseben.finaxor.core.toAppError
import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.data.entity.UserEntity
import com.tonyseben.finaxor.data.mapper.toDomain
import com.tonyseben.finaxor.data.source.remote.UserRemoteDataSource
import com.tonyseben.finaxor.domain.model.User
import com.tonyseben.finaxor.domain.repository.UserRepository
import kotlin.time.Clock

class UserRepositoryImpl(
    private val remoteDataSource: UserRemoteDataSource
) : UserRepository {

    override suspend fun createUser(
        userId: String,
        name: String,
        email: String,
        photoURL: String?
    ): Result<User> {
        return try {
            val now = Clock.System.now().toEpochMilliseconds()
            val entity = UserEntity(
                id = userId,
                name = name,
                email = email,
                photoURL = photoURL,
                createdAt = now
            )

            remoteDataSource.createUser(entity)
            Result.Success(entity.toDomain())
        } catch (e: Exception) {
            Result.Error(e.toAppError())
        }
    }

    override suspend fun getUser(userId: String): Result<User> {
        return try {
            val entity = remoteDataSource.getUser(userId)
            Result.Success(entity.toDomain())
        } catch (e: Exception) {
            Result.Error(e.toAppError())
        }
    }

    override suspend fun updateUser(
        userId: String,
        name: String?,
        photoURL: String?
    ): Result<Unit> {
        return try {
            val updates = mutableMapOf<String, Any>()
            name?.let { updates["name"] = it }
            photoURL?.let { updates["photoURL"] = it }

            if (updates.isNotEmpty()) {
                remoteDataSource.updateUser(userId, updates)
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.toAppError())
        }
    }

    override suspend fun deleteUser(userId: String): Result<Unit> {
        return try {
            remoteDataSource.deleteUser(userId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.toAppError())
        }
    }

    override suspend fun findUserByEmail(email: String): Result<User?> {
        return try {
            val entity = remoteDataSource.findUserByEmail(email)
            Result.Success(entity?.toDomain())
        } catch (e: Exception) {
            Result.Error(e.toAppError())
        }
    }
}