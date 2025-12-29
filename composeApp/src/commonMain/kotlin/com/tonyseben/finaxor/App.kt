package com.tonyseben.finaxor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.tonyseben.finaxor.domain.model.AuthState
import com.tonyseben.finaxor.ui.auth.AuthViewModel
import com.tonyseben.finaxor.ui.auth.LoginScreen
import com.tonyseben.finaxor.ui.auth.rememberGoogleSignInLauncher
import com.tonyseben.finaxor.ui.home.HomeScreen
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
    MaterialTheme {
        val viewModel: AuthViewModel = koinViewModel()
        val uiState by viewModel.uiState.collectAsState()
        val googleSignInLauncher = rememberGoogleSignInLauncher()

        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .safeContentPadding()
                .fillMaxSize()
        ) {
            when (val authState = uiState.authState) {
                is AuthState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is AuthState.Unauthenticated -> {
                    LoginScreen(
                        googleSignInLauncher = googleSignInLauncher,
                        isLoading = uiState.isLoading,
                        errorMessage = uiState.errorMessage,
                        onSignInSuccess = { idToken, accessToken ->
                            viewModel.signInWithGoogle(idToken, accessToken)
                        },
                        onClearError = { viewModel.clearError() }
                    )
                }
                is AuthState.Authenticated -> {
                    HomeScreen(
                        user = authState.user,
                        isLoading = uiState.isLoading,
                        onLogout = { viewModel.logout() }
                    )
                }
            }
        }
    }
}
