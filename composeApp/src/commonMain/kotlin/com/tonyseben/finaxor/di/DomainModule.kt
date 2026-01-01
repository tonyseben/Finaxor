package com.tonyseben.finaxor.di

import com.tonyseben.finaxor.domain.usecase.auth.GetCurrentUserUseCase
import com.tonyseben.finaxor.domain.usecase.auth.LogoutUseCase
import com.tonyseben.finaxor.domain.usecase.auth.ObserveAuthStateUseCase
import com.tonyseben.finaxor.domain.usecase.auth.SignInWithGoogleUseCase
import com.tonyseben.finaxor.domain.usecase.fd.CalculateFDCurrentValueUseCase
import com.tonyseben.finaxor.domain.usecase.fd.CalculateFDSummaryUseCase
import com.tonyseben.finaxor.domain.usecase.fd.CalculateFDDaysUntilMaturityUseCase
import com.tonyseben.finaxor.domain.usecase.fd.CalculateFDInterestEarnedUseCase
import com.tonyseben.finaxor.domain.usecase.fd.CalculateFDMaturityAmountUseCase
import com.tonyseben.finaxor.domain.usecase.fd.CreateFixedDepositUseCase
import com.tonyseben.finaxor.domain.usecase.fd.DeleteFixedDepositUseCase
import com.tonyseben.finaxor.domain.usecase.fd.GetFDStatusUseCase
import com.tonyseben.finaxor.domain.usecase.fd.GetFixedDepositsUseCase
import com.tonyseben.finaxor.domain.usecase.fd.IsFDActiveUseCase
import com.tonyseben.finaxor.domain.usecase.fd.IsFDMaturedUseCase
import com.tonyseben.finaxor.domain.usecase.fd.UpdateFixedDepositUseCase
import com.tonyseben.finaxor.domain.usecase.member.AddMemberUseCase
import com.tonyseben.finaxor.domain.usecase.member.GetPortfolioMembersUseCase
import com.tonyseben.finaxor.domain.usecase.member.RemoveMemberUseCase
import com.tonyseben.finaxor.domain.usecase.member.UpdateMemberRoleUseCase
import com.tonyseben.finaxor.domain.usecase.portfolio.CreatePortfolioUseCase
import com.tonyseben.finaxor.domain.usecase.portfolio.DeletePortfolioUseCase
import com.tonyseben.finaxor.domain.usecase.portfolio.GetPortfolioSummaryUseCase
import com.tonyseben.finaxor.domain.usecase.portfolio.GetPortfolioUseCase
import com.tonyseben.finaxor.domain.usecase.portfolio.GetUserPortfoliosUseCase
import com.tonyseben.finaxor.domain.usecase.portfolio.UpdatePortfolioUseCase
import org.koin.dsl.module

val domainModule = module {
    // Auth Use Cases
    factory { SignInWithGoogleUseCase(get(), get()) }
    factory { GetCurrentUserUseCase(get(), get()) }
    factory { LogoutUseCase(get()) }
    factory { ObserveAuthStateUseCase(get()) }

    // Portfolio Use Cases
    factory { CreatePortfolioUseCase(get()) }
    factory { GetPortfolioUseCase(get()) }
    factory { GetPortfolioSummaryUseCase(get(), get(), get()) }
    factory { GetUserPortfoliosUseCase(get()) }
    factory { UpdatePortfolioUseCase(get()) }
    factory { DeletePortfolioUseCase(get()) }

    // Member Use Cases
    factory { AddMemberUseCase(get(), get()) }
    factory { UpdateMemberRoleUseCase(get()) }
    factory { RemoveMemberUseCase(get()) }
    factory { GetPortfolioMembersUseCase(get()) }

    // Fixed Deposit Use Cases
    factory { CreateFixedDepositUseCase(get()) }
    factory { UpdateFixedDepositUseCase(get()) }
    factory { DeleteFixedDepositUseCase(get()) }
    factory { GetFixedDepositsUseCase(get()) }

    // FD Calculation Use Cases
    factory { CalculateFDMaturityAmountUseCase() }
    factory { CalculateFDInterestEarnedUseCase(get()) }
    factory { CalculateFDCurrentValueUseCase(get()) }
    factory { CalculateFDSummaryUseCase(get()) }
    factory { IsFDMaturedUseCase() }
    factory { IsFDActiveUseCase() }
    factory { CalculateFDDaysUntilMaturityUseCase() }
    factory { GetFDStatusUseCase() }
}
