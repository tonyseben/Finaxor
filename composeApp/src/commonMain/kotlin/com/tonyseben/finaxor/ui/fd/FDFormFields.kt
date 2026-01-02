package com.tonyseben.finaxor.ui.fd

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tonyseben.finaxor.core.formatCurrency
import com.tonyseben.finaxor.domain.model.PayoutFrequency
import com.tonyseben.finaxor.ui.components.DateField
import com.tonyseben.finaxor.ui.components.LabeledText
import com.tonyseben.finaxor.ui.components.PayoutFrequencyDropdown

@Composable
fun FDFormFields(
    formData: FDFormData,
    isEditMode: Boolean,
    isSaving: Boolean,
    onFormChange: (FDFormData) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Bank Name
        if (isEditMode) {
            OutlinedTextField(
                value = formData.bankName,
                onValueChange = { onFormChange(formData.copy(bankName = it)) },
                label = { Text("Bank Name") },
                singleLine = true,
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            LabeledText("Bank Name", formData.bankName)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Account Number
        if (isEditMode) {
            OutlinedTextField(
                value = formData.accountNumber,
                onValueChange = { onFormChange(formData.copy(accountNumber = it)) },
                label = { Text("Account Number") },
                singleLine = true,
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            LabeledText("Account Number", formData.accountNumber)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Principal Amount
        if (isEditMode) {
            OutlinedTextField(
                value = formData.principalAmount,
                onValueChange = { onFormChange(formData.copy(principalAmount = it)) },
                label = { Text("Principal Amount") },
                singleLine = true,
                enabled = !isSaving,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                prefix = { Text("₹") },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            LabeledText("Principal Amount", formatCurrency(formData.principalAmount.toDoubleOrNull() ?: 0.0))
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Interest Rate
        if (isEditMode) {
            OutlinedTextField(
                value = formData.interestRate,
                onValueChange = { onFormChange(formData.copy(interestRate = it)) },
                label = { Text("Interest Rate") },
                singleLine = true,
                enabled = !isSaving,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                suffix = { Text("%") },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            LabeledText("Interest Rate", "${formData.interestRate}%")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Start Date
        DateField(
            label = "Start Date",
            value = formData.startDate,
            isEditMode = isEditMode,
            enabled = !isSaving,
            onDateChange = { onFormChange(formData.copy(startDate = it)) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Maturity Date
        DateField(
            label = "Maturity Date",
            value = formData.maturityDate,
            isEditMode = isEditMode,
            enabled = !isSaving,
            onDateChange = { onFormChange(formData.copy(maturityDate = it)) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Payout Frequency
        if (isEditMode) {
            PayoutFrequencyDropdown(
                selected = formData.payoutFrequency,
                onSelect = { onFormChange(formData.copy(payoutFrequency = it)) },
                enabled = !isSaving
            )
        } else {
            val frequencyText = when (formData.payoutFrequency) {
                PayoutFrequency.MONTHLY -> "Monthly"
                PayoutFrequency.QUARTERLY -> "Quarterly"
                PayoutFrequency.YEARLY -> "Yearly"
            }
            LabeledText("Payout Frequency", frequencyText)
        }

        Spacer(modifier = Modifier.height(80.dp)) // Space for FAB
    }
}
