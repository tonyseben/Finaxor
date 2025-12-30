package com.tonyseben.finaxor.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tonyseben.finaxor.domain.model.AuthUser
import com.tonyseben.finaxor.ui.auth.GoogleSignInLauncher
import com.tonyseben.finaxor.ui.auth.LoginScreen
import com.tonyseben.finaxor.ui.home.HomeScreen

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
                HomeScreen(
                    user = user,
                    isLoading = isLoading,
                    onLogout = onLogout
                )
            }
        }
    }
}
