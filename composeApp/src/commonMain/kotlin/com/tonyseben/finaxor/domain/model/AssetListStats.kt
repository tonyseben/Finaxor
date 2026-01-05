package com.tonyseben.finaxor.domain.model

/**
 * Stats model with dynamic key-value pairs for asset list screen.
 */
data class AssetListStats(
    val headline: String,
    val entries: List<StatEntry>
)

/**
 * A single stat entry with label, value, and optional color.
 */
data class StatEntry(
    val label: String,
    val value: String,
    val valueColor: StatValueColor = StatValueColor.DEFAULT
)

/**
 * Color options for stat values.
 */
enum class StatValueColor {
    DEFAULT,
    POSITIVE,
    NEGATIVE
}
