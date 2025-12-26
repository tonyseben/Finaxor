package com.tonyseben.finaxor.data.entity

import kotlinx.serialization.Serializable

@Serializable
data class UserEntity(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val photoURL: String? = null,
    val createdAt: Long = 0L
)