package com.tonyseben.finaxor.ui.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tonyseben.finaxor.domain.model.AuthUser
import com.tonyseben.finaxor.ui.auth.GoogleSignInLauncher
import com.tonyseben.finaxor.ui.auth.LoginScreen
import com.tonyseben.finaxor.ui.fd.FDScreen
import com.tonyseben.finaxor.ui.home.CreatePortfolioSheet
import com.tonyseben.finaxor.ui.home.HomeScreen
import com.tonyseben.finaxor.ui.home.HomeViewModel
import com.tonyseben.finaxor.ui.portfolio.PortfolioScreen
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String,
    googleSignInLauncher: GoogleSignInLauncher,
    isLoading: Boolean,
    errorMessage: String?,
    currentUser: AuthUser?,
    onSignInSuccess: (idToken: String, accessToken: String?) -> Unit,
    onClearError: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Route.Login.route) {
            LoginScreen(
                googleSignInLauncher = googleSignInLauncher,
                isLoading = isLoading,
                errorMessage = errorMessage,
                onSignInSuccess = onSignInSuccess,
                onClearError = onClearError
            )
        }

        composable(Route.Home.route) {
            currentUser?.let { user ->
                val homeViewModel: HomeViewModel = koinViewModel()
                val homeUiState by homeViewModel.uiState.collectAsState()
                val sheetState = rememberModalBottomSheetState()

                HomeScreen(
                    user = user,
                    portfolios = homeUiState.portfolios,
                    isLoading = isLoading || homeUiState.isLoading,
                    onLogout = onLogout,
                    onCreateClick = { homeViewModel.showCreateSheet() },
                    onPortfolioClick = { portfolioId ->
                        navController.navigate(Route.Portfolio.createRoute(portfolioId))
                    }
                )

                if (homeUiState.showCreateSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { homeViewModel.hideCreateSheet() },
                        sheetState = sheetState
                    ) {
                        CreatePortfolioSheet(
                            isCreating = homeUiState.isCreating,
                            onCreateClick = { name ->
                                homeViewModel.createPortfolio(name) { portfolioId ->
                                    navController.navigate(Route.Portfolio.createRoute(portfolioId))
                                }
                            }
                        )
                    }
                }
            }
        }

        composable(Route.Portfolio.route) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId") ?: return@composable
            PortfolioScreen(
                portfolioId = portfolioId,
                onBackClick = { navController.popBackStack() },
                onAddAsset = { assetType ->
                    if (assetType == "FIXED_DEPOSIT") {
                        navController.navigate(Route.FD.createRoute(portfolioId, null))
                    }
                }
            )
        }

        composable(Route.FD.route) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId") ?: return@composable
            val fdId = backStackEntry.arguments?.getString("fdId")?.takeIf { it != "new" }
            FDScreen(
                portfolioId = portfolioId,
                fdId = fdId,
                onBackClick = { navController.popBackStack() },
                onDeleted = { navController.popBackStack() }
            )
        }
    }
}
