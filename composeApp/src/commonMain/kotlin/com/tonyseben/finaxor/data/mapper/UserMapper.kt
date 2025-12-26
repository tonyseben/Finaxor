package com.tonyseben.finaxor.data.mapper

import com.tonyseben.finaxor.data.entity.UserEntity
import com.tonyseben.finaxor.domain.model.User

fun UserEntity.toDomain(): User {
    return User(
        id = id,
        name = name,
        email = email,
        photoURL = photoURL,
        createdAt = createdAt
    )
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        name = name,
        email = email,
        photoURL = photoURL,
        createdAt = createdAt
    )
}


fun List<UserEntity>.toDomain(): List<User> = map { it.toDomain() }