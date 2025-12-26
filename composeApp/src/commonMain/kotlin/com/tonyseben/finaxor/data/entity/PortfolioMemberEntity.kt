package com.tonyseben.finaxor.data.entity

import kotlinx.serialization.Serializable

@Serializable
data class PortfolioMemberEntity(
    val userId: String = "",
    val portfolioId: String = "",
    val role: String = "",
    val addedBy: String = "",
    val addedAt: Long = 0L
)