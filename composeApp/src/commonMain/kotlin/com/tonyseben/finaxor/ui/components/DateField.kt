package com.tonyseben.finaxor.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.tonyseben.finaxor.core.formatDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateField(
    label: String,
    value: Long?,
    isEditMode: Boolean,
    enabled: Boolean,
    onDateChange: (Long) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val dateText = value?.let { formatDate(it) } ?: ""

    if (isEditMode) {
        OutlinedTextField(
            value = dateText,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }, enabled = enabled) {
                    Text("📅")
                }
            }
        )

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(initialSelectedDateMillis = value)
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { onDateChange(it) }
                        showDatePicker = false
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    } else {
        LabeledText(label, dateText)
    }
}