package com.tonyseben.finaxor.ui.fd

import com.tonyseben.finaxor.domain.model.FDStats
import com.tonyseben.finaxor.domain.model.PayoutFrequency

data class FDFormData(
    val bankName: String = "",
    val accountNumber: String = "",
    val principalAmount: String = "",
    val interestRate: String = "",
    val startDate: Long? = null,
    val maturityDate: Long? = null,
    val payoutFrequency: PayoutFrequency = PayoutFrequency.YEARLY
)

data class FDUiState(
    val isEditMode: Boolean = false,
    val fdId: String? = null,
    val portfolioId: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val formData: FDFormData = FDFormData(),
    val stats: FDStats? = null,
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val showDeleteDialog: Boolean = false
)
