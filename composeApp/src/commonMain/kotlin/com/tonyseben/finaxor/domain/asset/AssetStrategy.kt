package com.tonyseben.finaxor.domain.asset

import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.domain.model.AssetSummary
import kotlinx.coroutines.flow.Flow

interface AssetStrategy {
    val type: String
    val displayName: String

    fun getAssets(portfolioId: String): Flow<Result<List<Any>>>
    suspend fun calculateSummary(assets: List<Any>): Result<AssetSummary?>
}
