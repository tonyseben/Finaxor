package com.tonyseben.finaxor.ui.assetlist

import com.tonyseben.finaxor.domain.model.AssetListItem
import com.tonyseben.finaxor.domain.model.AssetListStats

data class AssetListUiState(
    val displayName: String = "",
    val stats: AssetListStats? = null,
    val items: List<AssetListItem> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
