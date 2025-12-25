package com.tonyseben.finaxor.domain.model

enum class PayoutFrequency {
    MONTHLY,
    QUARTERLY,
    YEARLY;

    companion object {
        fun fromString(value: String): PayoutFrequency? {
            return entries.find { it.name.equals(value, ignoreCase = true) }
        }
    }
}