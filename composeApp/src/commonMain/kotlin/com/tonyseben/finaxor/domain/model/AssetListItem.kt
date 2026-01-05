package com.tonyseben.finaxor.domain.model

/**
 * Sealed interface representing displayable list items for any asset type.
 * Each asset type implements this with its specific data.
 */
sealed interface AssetListItem {
    val id: String
    val title: String
    val subtitle: String
    val primaryValue: Double
    val secondaryValue: Double
    val returnsPercent: Double
}

/**
 * Fixed Deposit list item implementation.
 */
data class FDListItem(
    override val id: String,
    override val title: String,           // bankName
    override val subtitle: String,        // accountNumber
    override val primaryValue: Double,    // principalAmount
    override val secondaryValue: Double,  // currentValue
    override val returnsPercent: Double
) : AssetListItem
