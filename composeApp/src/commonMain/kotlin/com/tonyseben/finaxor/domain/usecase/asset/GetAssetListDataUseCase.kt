package com.tonyseben.finaxor.domain.usecase.asset

import com.tonyseben.finaxor.core.AppError
import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.domain.asset.AssetStrategy
import com.tonyseben.finaxor.domain.model.AssetListItem
import com.tonyseben.finaxor.domain.model.AssetListStats
import com.tonyseben.finaxor.domain.usecase.FlowUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Use case to get asset list data (items + stats) for a specific asset type.
 */
class GetAssetListDataUseCase(
    private val assetStrategies: List<AssetStrategy>
) : FlowUseCase<GetAssetListDataUseCase.Params, AssetListData> {

    data class Params(
        val portfolioId: String,
        val assetType: String
    )

    override fun invoke(params: Params): Flow<Result<AssetListData>> = flow {
        emit(Result.Loading)

        val strategy = assetStrategies.find { it.type == params.assetType }

        if (strategy == null) {
            emit(Result.Error(AppError.NotFoundError("Asset type", params.assetType)))
            return@flow
        }

        strategy.getAssets(params.portfolioId).collect { result ->
            when (result) {
                is Result.Success -> {
                    val items = strategy.toListItems(result.data)
                    val stats = strategy.calculateListStats(result.data)
                    emit(
                        Result.Success(
                            AssetListData(
                                displayName = strategy.displayName,
                                stats = stats,
                                items = items
                            )
                        )
                    )
                }
                is Result.Error -> emit(result)
                is Result.Loading -> emit(Result.Loading)
            }
        }
    }
}

/**
 * Data class containing all data needed for the asset list screen.
 */
data class AssetListData(
    val displayName: String,
    val stats: AssetListStats,
    val items: List<AssetListItem>
)
