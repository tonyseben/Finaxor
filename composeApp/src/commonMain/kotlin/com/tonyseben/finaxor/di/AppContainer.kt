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
import com.tonyseben.finaxor.domain.usecase.auth.GetCurrentUserUseCase
import com.tonyseben.finaxor.domain.usecase.auth.LogoutUseCase
import com.tonyseben.finaxor.domain.usecase.auth.ObserveAuthStateUseCase
import com.tonyseben.finaxor.domain.usecase.auth.SignInWithGoogleUseCase
import com.tonyseben.finaxor.domain.usecase.fd.CalculateFDCurrentValueUseCase
import com.tonyseben.finaxor.domain.usecase.fd.CalculateFDDaysUntilMaturityUseCase
import com.tonyseben.finaxor.domain.usecase.fd.CalculateFDInterestEarnedUseCase
import com.tonyseben.finaxor.domain.usecase.fd.CalculateFDMaturityAmountUseCase
import com.tonyseben.finaxor.domain.usecase.fd.CreateFixedDepositUseCase
import com.tonyseben.finaxor.domain.usecase.fd.DeleteFixedDepositUseCase
import com.tonyseben.finaxor.domain.usecase.fd.GetFDStatusUseCase
import com.tonyseben.finaxor.domain.usecase.fd.GetPortfolioFDsUseCase
import com.tonyseben.finaxor.domain.usecase.fd.IsFDActiveUseCase
import com.tonyseben.finaxor.domain.usecase.fd.IsFDMaturedUseCase
import com.tonyseben.finaxor.domain.usecase.fd.UpdateFixedDepositUseCase
import com.tonyseben.finaxor.domain.usecase.member.AddMemberUseCase
import com.tonyseben.finaxor.domain.usecase.member.GetPortfolioMembersUseCase
import com.tonyseben.finaxor.domain.usecase.member.RemoveMemberUseCase
import com.tonyseben.finaxor.domain.usecase.member.UpdateMemberRoleUseCase
import com.tonyseben.finaxor.domain.usecase.portfolio.CreatePortfolioUseCase
import com.tonyseben.finaxor.domain.usecase.portfolio.DeletePortfolioUseCase
import com.tonyseben.finaxor.domain.usecase.portfolio.GetUserPortfoliosUseCase
import com.tonyseben.finaxor.domain.usecase.portfolio.UpdatePortfolioUseCase
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore

/**
 * Dependency Injection Container
 * Simple DI - can be replaced with Koin, Kodein, etc.
 */
object AppContainer {

    // ============================================
    // DATA LAYER
    // ============================================

    // Firebase
    private val firestore by lazy { Firebase.firestore }
    private val auth by lazy { Firebase.auth }

    // Data Sources
    private val authRemoteDataSource by lazy {
        AuthRemoteDataSource(auth)
    }

    private val userRemoteDataSource by lazy {
        UserRemoteDataSource(firestore)
    }

    private val portfolioRemoteDataSource by lazy {
        PortfolioRemoteDataSource(firestore)
    }

    private val fdRemoteDataSource by lazy {
        FixedDepositRemoteDataSource(firestore)
    }

    // Repositories
    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(authRemoteDataSource)
    }

    val userRepository: UserRepository by lazy {
        UserRepositoryImpl(userRemoteDataSource)
    }

    val portfolioRepository: PortfolioRepository by lazy {
        PortfolioRepositoryImpl(
            portfolioRemoteDataSource,
            userRemoteDataSource
        )
    }

    val fdRepository: FixedDepositRepository by lazy {
        FixedDepositRepositoryImpl(fdRemoteDataSource)
    }

    // ============================================
    // DOMAIN LAYER - USE CASES
    // ============================================

    // Auth Use Cases
    val signInWithGoogleUseCase by lazy {
        SignInWithGoogleUseCase(authRepository, userRepository)
    }

    val getCurrentUserUseCase by lazy {
        GetCurrentUserUseCase(authRepository, userRepository)
    }

    val logoutUseCase by lazy {
        LogoutUseCase(authRepository)
    }

    val observeAuthStateUseCase by lazy {
        ObserveAuthStateUseCase(authRepository)
    }

    // Portfolio Use Cases
    val createPortfolioUseCase by lazy {
        CreatePortfolioUseCase(portfolioRepository)
    }

    val getUserPortfoliosUseCase by lazy {
        GetUserPortfoliosUseCase(portfolioRepository)
    }

    val updatePortfolioUseCase by lazy {
        UpdatePortfolioUseCase(portfolioRepository)
    }

    val deletePortfolioUseCase by lazy {
        DeletePortfolioUseCase(portfolioRepository)
    }

    // Member Use Cases
    val addMemberUseCase by lazy {
        AddMemberUseCase(portfolioRepository, userRepository)
    }

    val updateMemberRoleUseCase by lazy {
        UpdateMemberRoleUseCase(portfolioRepository)
    }

    val removeMemberUseCase by lazy {
        RemoveMemberUseCase(portfolioRepository)
    }

    val getPortfolioMembersUseCase by lazy {
        GetPortfolioMembersUseCase(portfolioRepository)
    }

    // Fixed Deposit Use Cases
    val createFDUseCase by lazy {
        CreateFixedDepositUseCase(fdRepository)
    }

    val updateFDUseCase by lazy {
        UpdateFixedDepositUseCase(fdRepository)
    }

    val deleteFDUseCase by lazy {
        DeleteFixedDepositUseCase(fdRepository)
    }

    val getPortfolioFDsUseCase by lazy {
        GetPortfolioFDsUseCase(fdRepository)
    }

    // FD Calculation Use Cases
    val calculateFDMaturityAmountUseCase by lazy {
        CalculateFDMaturityAmountUseCase()
    }

    val calculateFDInterestEarnedUseCase by lazy {
        CalculateFDInterestEarnedUseCase(calculateFDMaturityAmountUseCase)
    }

    val calculateFDCurrentValueUseCase by lazy {
        CalculateFDCurrentValueUseCase(calculateFDMaturityAmountUseCase)
    }

    val isFDMaturedUseCase by lazy {
        IsFDMaturedUseCase()
    }

    val isFDActiveUseCase by lazy {
        IsFDActiveUseCase()
    }

    val calculateFDDaysUntilMaturityUseCase by lazy {
        CalculateFDDaysUntilMaturityUseCase()
    }

    val getFDStatusUseCase by lazy {
        GetFDStatusUseCase()
    }
}

/**
 * Extension functions for easier access
 */

// Repositories
fun getAuthRepository(): AuthRepository = AppContainer.authRepository
fun getUserRepository(): UserRepository = AppContainer.userRepository
fun getPortfolioRepository(): PortfolioRepository = AppContainer.portfolioRepository
fun getFDRepository(): FixedDepositRepository = AppContainer.fdRepository

// Auth Use Cases
fun getSignInWithGoogleUseCase() = AppContainer.signInWithGoogleUseCase
fun getGetCurrentUserUseCase() = AppContainer.getCurrentUserUseCase
fun getLogoutUseCase() = AppContainer.logoutUseCase
fun getObserveAuthStateUseCase() = AppContainer.observeAuthStateUseCase

// Portfolio Use Cases
fun getCreatePortfolioUseCase() = AppContainer.createPortfolioUseCase
fun getGetUserPortfoliosUseCase() = AppContainer.getUserPortfoliosUseCase
fun getUpdatePortfolioUseCase() = AppContainer.updatePortfolioUseCase
fun getDeletePortfolioUseCase() = AppContainer.deletePortfolioUseCase

// Member Use Cases
fun getAddMemberUseCase() = AppContainer.addMemberUseCase
fun getUpdateMemberRoleUseCase() = AppContainer.updateMemberRoleUseCase
fun getRemoveMemberUseCase() = AppContainer.removeMemberUseCase
fun getGetPortfolioMembersUseCase() = AppContainer.getPortfolioMembersUseCase

// FD Use Cases
fun getCreateFDUseCase() = AppContainer.createFDUseCase
fun getUpdateFDUseCase() = AppContainer.updateFDUseCase
fun getDeleteFDUseCase() = AppContainer.deleteFDUseCase
fun getGetPortfolioFDsUseCase() = AppContainer.getPortfolioFDsUseCase

// FD Calculation Use Cases
fun getCalculateFDMaturityAmountUseCase() = AppContainer.calculateFDMaturityAmountUseCase
fun getCalculateFDInterestEarnedUseCase() = AppContainer.calculateFDInterestEarnedUseCase
fun getCalculateFDCurrentValueUseCase() = AppContainer.calculateFDCurrentValueUseCase
fun getIsFDMaturedUseCase() = AppContainer.isFDMaturedUseCase
fun getIsFDActiveUseCase() = AppContainer.isFDActiveUseCase
fun getCalculateFDDaysUntilMaturityUseCase() = AppContainer.calculateFDDaysUntilMaturityUseCase
fun getGetFDStatusUseCase() = AppContainer.getFDStatusUseCase