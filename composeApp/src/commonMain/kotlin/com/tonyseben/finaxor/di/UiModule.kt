package com.tonyseben.finaxor.di

import com.tonyseben.finaxor.ui.assetlist.AssetListViewModel
import com.tonyseben.finaxor.ui.auth.AuthViewModel
import com.tonyseben.finaxor.ui.fd.FDViewModel
import com.tonyseben.finaxor.ui.home.HomeViewModel
import com.tonyseben.finaxor.ui.portfolio.PortfolioViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val uiModule = module {
    viewModelOf(::AuthViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::PortfolioViewModel)
    viewModel { params ->
        AssetListViewModel(
            portfolioId = params[0],
            assetType = params[1],
            getAssetListDataUseCase = get()
        )
    }
    viewModel { params ->
        FDViewModel(
            portfolioId = params[0],
            fdId = params.values.getOrNull(1) as? String,
            getFDUseCase = get(),
            createFDUseCase = get(),
            updateFDUseCase = get(),
            deleteFDUseCase = get(),
            getCurrentAuthUserUseCase = get(),
            calculateFDStatsUseCase = get()
        )
    }
}
