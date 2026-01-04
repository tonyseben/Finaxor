package com.tonyseben.finaxor.ui.fd

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.domain.model.FixedDeposit
import com.tonyseben.finaxor.domain.usecase.auth.GetCurrentAuthUserUseCase
import com.tonyseben.finaxor.domain.usecase.fd.CalculateFDStatsUseCase
import com.tonyseben.finaxor.domain.usecase.fd.CreateFixedDepositUseCase
import com.tonyseben.finaxor.domain.usecase.fd.DeleteFixedDepositUseCase
import com.tonyseben.finaxor.domain.usecase.fd.GetFixedDepositUseCase
import com.tonyseben.finaxor.domain.usecase.fd.UpdateFixedDepositUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FDViewModel(
    private val portfolioId: String,
    private val fdId: String?,
    private val getFDUseCase: GetFixedDepositUseCase,
    private val createFDUseCase: CreateFixedDepositUseCase,
    private val updateFDUseCase: UpdateFixedDepositUseCase,
    private val deleteFDUseCase: DeleteFixedDepositUseCase,
    private val getCurrentAuthUserUseCase: GetCurrentAuthUserUseCase,
    private val calculateFDStatsUseCase: CalculateFDStatsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FDUiState(portfolioId = portfolioId))
    val uiState: StateFlow<FDUiState> = _uiState.asStateFlow()

    private var originalFormData: FDFormData? = null

    init {
        if (fdId != null && fdId != "new") {
            loadFD()
        } else {
            initCreate()
        }
    }

    private fun loadFD() {
        val id = fdId ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val params = GetFixedDepositUseCase.Params(portfolioId, id)
            when (val result = getFDUseCase(params)) {
                is Result.Success -> {
                    val fd = result.data
                    val formData = fd.toFormData()
                    originalFormData = formData
                    _uiState.value = _uiState.value.copy(
                        fdId = fd.id,
                        isEditMode = false,
                        isLoading = false,
                        formData = formData
                    )
                    loadStats(fd)
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.error.message
                    )
                }
                is Result.Loading -> {}
            }
        }
    }

    private fun initCreate() {
        _uiState.value = _uiState.value.copy(
            isEditMode = true,
            fdId = null,
            isLoading = false
        )
    }

    private suspend fun loadStats(fd: FixedDeposit) {
        when (val result = calculateFDStatsUseCase(CalculateFDStatsUseCase.Params(fd))) {
            is Result.Success -> {
                _uiState.value = _uiState.value.copy(stats = result.data)
            }
            is Result.Error -> {
                _uiState.value = _uiState.value.copy(errorMessage = result.error.message)
            }
            is Result.Loading -> {}
        }
    }

    fun enterEditMode() {
        _uiState.value = _uiState.value.copy(isEditMode = true)
    }

    fun exitEditMode() {
        originalFormData?.let { formData ->
            _uiState.value = _uiState.value.copy(
                isEditMode = false,
                formData = formData
            )
        }
    }

    fun updateForm(formData: FDFormData) {
        _uiState.value = _uiState.value.copy(formData = formData)
    }

    fun save(onCreated: (fdId: String) -> Unit = {}) {
        viewModelScope.launch {
            val state = _uiState.value
            val formData = state.formData

            // Validate form data before proceeding
            val principalAmount = formData.principalAmount.toDoubleOrNull()
            if (principalAmount == null) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Please enter a valid principal amount"
                )
                return@launch
            }

            val interestRate = formData.interestRate.toDoubleOrNull()
            if (interestRate == null) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Please enter a valid interest rate"
                )
                return@launch
            }

            val startDate = formData.startDate
            if (startDate == null) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Please select a start date"
                )
                return@launch
            }

            val maturityDate = formData.maturityDate
            if (maturityDate == null) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Please select a maturity date"
                )
                return@launch
            }

            _uiState.value = _uiState.value.copy(isSaving = true)

            if (state.fdId == null) {
                createFD(formData, principalAmount, interestRate, startDate, maturityDate, onCreated)
            } else {
                updateFD(state.fdId, formData, principalAmount, interestRate, startDate, maturityDate)
            }
        }
    }

    private suspend fun createFD(
        formData: FDFormData,
        principalAmount: Double,
        interestRate: Double,
        startDate: Long,
        maturityDate: Long,
        onCreated: (fdId: String) -> Unit
    ) {
        val authUser = when (val r = getCurrentAuthUserUseCase(Unit)) {
            is Result.Success -> r.data
            else -> null
        }

        if (authUser == null) {
            _uiState.value = _uiState.value.copy(
                isSaving = false,
                errorMessage = "User not authenticated"
            )
            return
        }

        val params = CreateFixedDepositUseCase.Params(
            portfolioId = _uiState.value.portfolioId,
            bankName = formData.bankName,
            accountNumber = formData.accountNumber,
            principalAmount = principalAmount,
            interestRate = interestRate,
            startDate = startDate,
            maturityDate = maturityDate,
            interestPayoutFreq = formData.payoutFrequency,
            createdBy = authUser.uid
        )

        when (val result = createFDUseCase(params)) {
            is Result.Success -> {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    fdId = result.data,
                    successMessage = "Fixed deposit created"
                )
                onCreated(result.data)
                loadFDById(result.data)
            }
            is Result.Error -> {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = result.error.message
                )
            }
            is Result.Loading -> {}
        }
    }

    private suspend fun updateFD(
        fdId: String,
        formData: FDFormData,
        principalAmount: Double,
        interestRate: Double,
        startDate: Long,
        maturityDate: Long
    ) {
        val authUser = when (val r = getCurrentAuthUserUseCase(Unit)) {
            is Result.Success -> r.data
            else -> null
        }

        if (authUser == null) {
            _uiState.value = _uiState.value.copy(
                isSaving = false,
                errorMessage = "User not authenticated"
            )
            return
        }

        val params = UpdateFixedDepositUseCase.Params(
            portfolioId = _uiState.value.portfolioId,
            fdId = fdId,
            bankName = formData.bankName,
            accountNumber = formData.accountNumber,
            principalAmount = principalAmount,
            interestRate = interestRate,
            startDate = startDate,
            maturityDate = maturityDate,
            payoutFrequency = formData.payoutFrequency,
            currentUserId = authUser.uid
        )

        when (val result = updateFDUseCase(params)) {
            is Result.Success -> {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    successMessage = "Fixed deposit updated"
                )
                loadFDById(fdId)
            }
            is Result.Error -> {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = result.error.message
                )
            }
            is Result.Loading -> {}
        }
    }

    private suspend fun loadFDById(id: String) {
        val params = GetFixedDepositUseCase.Params(_uiState.value.portfolioId, id)
        when (val result = getFDUseCase(params)) {
            is Result.Success -> {
                val fd = result.data
                val formData = fd.toFormData()
                originalFormData = formData
                _uiState.value = _uiState.value.copy(
                    fdId = fd.id,
                    isEditMode = false,
                    formData = formData
                )
                loadStats(fd)
            }
            is Result.Error -> {
                _uiState.value = _uiState.value.copy(errorMessage = result.error.message)
            }
            is Result.Loading -> {}
        }
    }

    fun showDeleteDialog() {
        _uiState.value = _uiState.value.copy(showDeleteDialog = true)
    }

    fun hideDeleteDialog() {
        _uiState.value = _uiState.value.copy(showDeleteDialog = false)
    }

    fun delete(onDeleted: () -> Unit) {
        viewModelScope.launch {
            val fdId = _uiState.value.fdId ?: return@launch

            val authUser = when (val r = getCurrentAuthUserUseCase(Unit)) {
                is Result.Success -> r.data
                else -> null
            }

            if (authUser == null) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "User not authenticated"
                )
                return@launch
            }

            _uiState.value = _uiState.value.copy(
                showDeleteDialog = false,
                isSaving = true
            )

            val params = DeleteFixedDepositUseCase.Params(
                portfolioId = _uiState.value.portfolioId,
                fdId = fdId,
                currentUserId = authUser.uid
            )
            when (val result = deleteFDUseCase(params)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        successMessage = "Fixed deposit deleted"
                    )
                    onDeleted()
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        errorMessage = result.error.message
                    )
                }
                is Result.Loading -> {}
            }
        }
    }

    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }

    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    private fun FixedDeposit.toFormData() = FDFormData(
        bankName = bankName,
        accountNumber = accountNumber,
        principalAmount = principalAmount.toString(),
        interestRate = interestRate.toString(),
        startDate = startDate,
        maturityDate = maturityDate,
        payoutFrequency = payoutFrequency
    )
}
