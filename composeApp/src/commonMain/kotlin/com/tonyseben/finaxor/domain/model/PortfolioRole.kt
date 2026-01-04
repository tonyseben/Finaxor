package com.tonyseben.finaxor.domain.model

enum class PortfolioRole(val value: String) {
    OWNER("owner"),
    MEMBER("member"),
    VIEWER("viewer");

    companion object {
        fun fromValue(value: String): PortfolioRole? =
            entries.find { it.value == value.lowercase() }
    }
}
