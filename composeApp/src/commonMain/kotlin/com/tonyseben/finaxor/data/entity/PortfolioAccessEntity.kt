package com.tonyseben.finaxor.data.entity

import kotlinx.serialization.Serializable

@Serializable
data class PortfolioAccessEntity(
    val portfolioId: String = "",
    val portfolioName: String = "",
    val role: String = "",
    val addedAt: Long = 0L
)