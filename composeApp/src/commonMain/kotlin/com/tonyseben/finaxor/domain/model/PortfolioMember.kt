package com.tonyseben.finaxor.domain.model

data class PortfolioMember(
    val userId: String,
    val portfolioId: String,
    val role: PortfolioRole,
    val addedBy: String,
    val addedAt: Long
)