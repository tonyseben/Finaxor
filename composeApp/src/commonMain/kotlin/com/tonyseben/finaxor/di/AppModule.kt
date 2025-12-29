package com.tonyseben.finaxor.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

fun appModules(): List<Module> = listOf(
    dataModule,
    domainModule,
    uiModule
)

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(appModules())
    }
}
