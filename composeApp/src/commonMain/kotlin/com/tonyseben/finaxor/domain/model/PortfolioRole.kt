package com.tonyseben.finaxor.domain.model

enum class PortfolioRole {
    OWNER,
    MEMBER,
    VIEWER;

    companion object Companion {
        fun fromString(value: String): PortfolioRole? {
            return entries.find { it.name.equals(value, ignoreCase = true) }
        }
    }
}
