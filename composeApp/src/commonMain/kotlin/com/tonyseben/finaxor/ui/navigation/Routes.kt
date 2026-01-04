package com.tonyseben.finaxor.ui.navigation

sealed class Route(val route: String) {
    // Auth flow
    data object Login : Route("login")

    // Main flow (authenticated)
    data object Home : Route("home")

    data object Portfolio : Route("portfolio/{portfolioId}") {
        fun createRoute(portfolioId: String) = "portfolio/$portfolioId"
    }

    data object FD : Route("portfolio/{portfolioId}/fd/{fdId}") {
        fun createRoute(portfolioId: String, fdId: String?) =
            "portfolio/$portfolioId/fd/${fdId ?: "new"}"
    }
}
