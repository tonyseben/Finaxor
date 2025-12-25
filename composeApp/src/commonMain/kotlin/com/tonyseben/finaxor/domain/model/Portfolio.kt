package com.tonyseben.finaxor.domain.model

data class Portfolio(
    val id: String,
    val name: String,
    val createdBy: String,
    val createdAt: Long,
    val updatedAt: Long
)
