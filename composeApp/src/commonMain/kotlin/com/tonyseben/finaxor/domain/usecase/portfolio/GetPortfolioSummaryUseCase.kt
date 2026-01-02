package com.tonyseben.finaxor.domain.usecase.portfolio

import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.domain.asset.AssetStrategy
import com.tonyseben.finaxor.domain.model.AssetSummary
import com.tonyseben.finaxor.domain.model.Portfolio
import com.tonyseben.finaxor.domain.model.PortfolioSummary
import com.tonyseben.finaxor.domain.repository.PortfolioRepository
import com.tonyseben.finaxor.domain.usecase.FlowUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class GetPortfolioSummaryUseCase(
    private val portfolioRepository: PortfolioRepository,
    private val assetStrategies: List<AssetStrategy>
) : FlowUseCase<String, PortfolioSummary> {

    override fun invoke(params: String): Flow<Result<PortfolioSummary>> = flow {
        emit(Result.Loading)

        // Fetch portfolio details (one-time)
        val portfolioResult = portfolioRepository.getPortfolio(params)
        val portfolio = when (portfolioResult) {
            is Result.Success -> portfolioResult.data
            is Result.Error -> {
                emit(portfolioResult)
                return@flow
            }
            is Result.Loading -> {
                emit(Result.Loading)
                return@flow
            }
        }

        if (assetStrategies.isEmpty()) {
            emit(Result.Success(PortfolioSummary(portfolio, emptyList())))
            return@flow
        }

        // Combine all strategy asset flows
        val assetFlows = assetStrategies.map { it.getAssets(params) }

        combine(assetFlows) { results ->
            buildPortfolioSummary(portfolio, results.toList())
        }.collect { result ->
            emit(result)
        }
    }

    private suspend fun buildPortfolioSummary(
        portfolio: Portfolio,
        assetResults: List<Result<List<Any>>>
    ): Result<PortfolioSummary> {
        val summaries = mutableListOf<AssetSummary>()

        assetStrategies.forEachIndexed { index, strategy ->
            val result = assetResults[index]

            when (result) {
                is Result.Error -> return result
                is Result.Loading -> return Result.Loading
                is Result.Success -> {
                    when (val summaryResult = strategy.calculateSummary(result.data)) {
                        is Result.Success -> summaryResult.data?.let { summaries.add(it) }
                        is Result.Error -> return summaryResult
                        is Result.Loading -> return Result.Loading
                    }
                }
            }
        }

        return Result.Success(PortfolioSummary(portfolio, summaries))
    }
}
