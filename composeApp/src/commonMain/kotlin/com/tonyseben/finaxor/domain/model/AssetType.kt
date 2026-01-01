package com.tonyseben.finaxor.domain.model

enum class AssetType {
    FIXED_DEPOSIT,
    US_STOCK,
    IN_STOCK,
    MUTUAL_FUND,
    PPF,
    BOND,
    CRYPTOCURRENCY;

    fun displayName(): String = when (this) {
        FIXED_DEPOSIT -> "Fixed Deposits"
        US_STOCK -> "US Stocks"
        IN_STOCK -> "IN Stocks"
        MUTUAL_FUND -> "Mutual Funds"
        PPF -> "PPFs"
        BOND -> "Bonds"
        CRYPTOCURRENCY -> "Cryptocurrency"
    }
}
