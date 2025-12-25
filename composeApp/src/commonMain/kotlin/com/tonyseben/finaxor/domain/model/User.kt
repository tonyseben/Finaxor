package com.tonyseben.finaxor.domain.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val photoURL: String?,
    val createdAt: Long
)
