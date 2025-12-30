package com.tonyseben.finaxor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.tonyseben.finaxor.domain.model.AuthState
import com.tonyseben.finaxor.ui.auth.AuthViewModel
import com.tonyseben.finaxor.ui.auth.rememberGoogleSignInLauncher
import com.tonyseben.finaxor.ui.navigation.AppNavHost
import com.tonyseben.finaxor.ui.navigation.Route
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
    MaterialTheme {
        val viewModel: AuthViewModel = koinViewModel()
        val uiState by viewModel.uiState.collectAsState()
        val googleSignInLauncher = rememberGoogleSignInLauncher()
        val navController = rememberNavController()

        // Determine start destination based on auth state
        val startDestination = when (uiState.authState) {
            is AuthState.Authenticated -> Route.Home.route
            else -> Route.Login.route
        }

        // Navigate based on auth state changes
        LaunchedEffect(uiState.authState) {
            when (uiState.authState) {
                is AuthState.Authenticated -> {
                    navController.navigate(Route.Home.route) {
                        popUpTo(Route.Login.route) { inclusive = true }
                    }
                }
                is AuthState.Unauthenticated -> {
                    navController.navigate(Route.Login.route) {
                        popUpTo(Route.Home.route) { inclusive = true }
                    }
                }
                is AuthState.Loading -> { /* Do nothing, show loading */ }
            }
        }

        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .safeContentPadding()
                .fillMaxSize()
        ) {
            if (uiState.authState is AuthState.Loading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                val currentUser = (uiState.authState as? AuthState.Authenticated)?.user

                AppNavHost(
                    navController = navController,
                    startDestination = startDestination,
                    googleSignInLauncher = googleSignInLauncher,
                    isLoading = uiState.isLoading,
                    errorMessage = uiState.errorMessage,
                    currentUser = currentUser,
                    onSignInSuccess = { idToken, accessToken ->
                        viewModel.signInWithGoogle(idToken, accessToken)
                    },
                    onClearError = { viewModel.clearError() },
                    onLogout = { viewModel.logout() }
                )
            }
        }
    }
}
