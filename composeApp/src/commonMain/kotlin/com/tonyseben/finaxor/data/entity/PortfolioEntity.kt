package com.tonyseben.finaxor.data.entity

import kotlinx.serialization.Serializable

@Serializable
data class PortfolioEntity(
    val id: String = "",
    val name: String = "",
    val createdBy: String = "",
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)