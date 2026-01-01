package com.tonyseben.finaxor.domain.usecase.portfolio

import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.domain.model.AssetSummary
import com.tonyseben.finaxor.domain.model.FixedDeposit
import com.tonyseben.finaxor.domain.model.PortfolioSummary
import com.tonyseben.finaxor.domain.repository.FixedDepositRepository
import com.tonyseben.finaxor.domain.repository.PortfolioRepository
import com.tonyseben.finaxor.domain.usecase.FlowUseCase
import com.tonyseben.finaxor.domain.usecase.fd.CalculateFDSummaryUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class GetPortfolioSummaryUseCase(
    private val portfolioRepository: PortfolioRepository,
    private val fdRepository: FixedDepositRepository,
    private val calculateFDSummaryUseCase: CalculateFDSummaryUseCase
    // Future: stockRepository, calculateStockSummaryUseCase, etc.
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

        // Collect asset flows and build summaries
        // Future: Use combine() when multiple asset types are added:
        // combine(fdFlow, stockFlow, mfFlow) { fd, stock, mf -> buildSummary(...) }
        fdRepository.getByPortfolio(params)
            .map { fdResult -> buildPortfolioSummary(portfolio, fdResult) }
            .collect { result -> emit(result) }
    }

    private suspend fun buildPortfolioSummary(
        portfolio: com.tonyseben.finaxor.domain.model.Portfolio,
        fdResult: Result<List<FixedDeposit>>
        // Future: stockResult: Result<List<Stock>>,
        // Future: mfResult: Result<List<MutualFund>>,
    ): Result<PortfolioSummary> {
        // Check for errors in any asset fetch
        if (fdResult is Result.Error) return fdResult
        if (fdResult is Result.Loading) return Result.Loading

        val fixedDeposits = (fdResult as Result.Success).data

        // Calculate summaries for each asset type
        val summaries = mutableListOf<AssetSummary>()

        when (val fdSummary = calculateFDSummaryUseCase(fixedDeposits)) {
            is Result.Success -> fdSummary.data?.let { summaries.add(it) }
            is Result.Error -> return fdSummary
            is Result.Loading -> return Result.Loading
        }

        // Future: Calculate other asset type summaries
        // when (val stockSummary = calculateStockSummaryUseCase(stocks)) { ... }

        return Result.Success(PortfolioSummary(portfolio, summaries))
    }
}
