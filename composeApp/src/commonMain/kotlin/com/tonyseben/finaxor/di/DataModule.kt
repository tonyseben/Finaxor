package com.tonyseben.finaxor.di

import com.tonyseben.finaxor.data.repository.AuthRepositoryImpl
import com.tonyseben.finaxor.data.repository.FixedDepositRepositoryImpl
import com.tonyseben.finaxor.data.repository.PortfolioRepositoryImpl
import com.tonyseben.finaxor.data.repository.UserRepositoryImpl
import com.tonyseben.finaxor.data.source.remote.AuthRemoteDataSource
import com.tonyseben.finaxor.data.source.remote.FixedDepositRemoteDataSource
import com.tonyseben.finaxor.data.source.remote.PortfolioRemoteDataSource
import com.tonyseben.finaxor.data.source.remote.UserRemoteDataSource
import com.tonyseben.finaxor.domain.repository.AuthRepository
import com.tonyseben.finaxor.domain.repository.FixedDepositRepository
import com.tonyseben.finaxor.domain.repository.PortfolioRepository
import com.tonyseben.finaxor.domain.repository.UserRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import org.koin.dsl.module

val dataModule = module {
    // Firebase instances
    single { Firebase.firestore }
    single { Firebase.auth }

    // Data Sources
    single { AuthRemoteDataSource(get()) }
    single { UserRemoteDataSource(get()) }
    single { PortfolioRemoteDataSource(get()) }
    single { FixedDepositRemoteDataSource(get()) }

    // Repositories
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<PortfolioRepository> { PortfolioRepositoryImpl(get(), get()) }
    single<FixedDepositRepository> { FixedDepositRepositoryImpl(get()) }
}
