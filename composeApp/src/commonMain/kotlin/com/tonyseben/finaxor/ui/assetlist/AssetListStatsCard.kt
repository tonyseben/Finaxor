package com.tonyseben.finaxor.ui.assetlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tonyseben.finaxor.domain.model.AssetListStats
import com.tonyseben.finaxor.domain.model.StatEntry
import com.tonyseben.finaxor.domain.model.StatValueColor

@Composable
fun AssetListStatsCard(
    stats: AssetListStats,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Headline: "5 Fixed Deposits"
            Text(
                text = stats.headline,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Dynamic stat entries in rows of 2
            stats.entries.chunked(2).forEach { rowEntries ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    rowEntries.forEachIndexed { index, entry ->
                        StatEntryColumn(
                            entry = entry,
                            alignment = if (index == 0) Alignment.Start else Alignment.End,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // Fill remaining space if odd number of entries
                    if (rowEntries.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun StatEntryColumn(
    entry: StatEntry,
    alignment: Alignment.Horizontal,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = alignment
    ) {
        Text(
            text = entry.label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
        Text(
            text = entry.value,
            style = MaterialTheme.typography.bodyLarge,
            color = when (entry.valueColor) {
                StatValueColor.POSITIVE -> MaterialTheme.colorScheme.primary
                StatValueColor.NEGATIVE -> MaterialTheme.colorScheme.error
                StatValueColor.DEFAULT -> MaterialTheme.colorScheme.onPrimaryContainer
            }
        )
    }
}
