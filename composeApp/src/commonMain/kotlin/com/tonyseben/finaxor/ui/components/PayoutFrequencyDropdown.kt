package com.tonyseben.finaxor.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.tonyseben.finaxor.domain.model.PayoutFrequency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayoutFrequencyDropdown(
    selected: PayoutFrequency,
    onSelect: (PayoutFrequency) -> Unit,
    enabled: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    val displayText = when (selected) {
        PayoutFrequency.MONTHLY -> "Monthly"
        PayoutFrequency.QUARTERLY -> "Quarterly"
        PayoutFrequency.YEARLY -> "Yearly"
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = it }
    ) {
        OutlinedTextField(
            value = displayText,
            onValueChange = {},
            readOnly = true,
            label = { Text("Payout Frequency") },
            enabled = enabled,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            PayoutFrequency.entries.forEach { frequency ->
                val text = when (frequency) {
                    PayoutFrequency.MONTHLY -> "Monthly"
                    PayoutFrequency.QUARTERLY -> "Quarterly"
                    PayoutFrequency.YEARLY -> "Yearly"
                }
                DropdownMenuItem(
                    text = { Text(text) },
                    onClick = {
                        onSelect(frequency)
                        expanded = false
                    }
                )
            }
        }
    }
}