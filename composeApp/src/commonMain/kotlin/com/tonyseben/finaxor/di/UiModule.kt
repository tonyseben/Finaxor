package com.tonyseben.finaxor.di

import com.tonyseben.finaxor.ui.auth.AuthViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val uiModule = module {
    viewModelOf(::AuthViewModel)
}
