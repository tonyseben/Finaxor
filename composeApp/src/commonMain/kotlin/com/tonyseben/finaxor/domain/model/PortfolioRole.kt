package com.tonyseben.finaxor.domain.model

enum class PortfolioRole {
    ADMIN,
    EDITOR,
    VIEWER;

    companion object Companion {
        fun fromString(value: String): PortfolioRole? {
            return entries.find { it.name.equals(value, ignoreCase = true) }
        }
    }
}
