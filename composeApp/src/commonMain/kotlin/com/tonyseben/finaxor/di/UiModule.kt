package com.tonyseben.finaxor.di

import com.tonyseben.finaxor.ui.auth.AuthViewModel
import com.tonyseben.finaxor.ui.fd.FDViewModel
import com.tonyseben.finaxor.ui.home.HomeViewModel
import com.tonyseben.finaxor.ui.portfolio.PortfolioViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val uiModule = module {
    viewModelOf(::AuthViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::PortfolioViewModel)
    viewModelOf(::FDViewModel)
}
