package com.tonyseben.finaxor.ui.navigation

sealed class Route(val route: String) {
    // Auth flow
    data object Login : Route("login")

    // Main flow (authenticated)
    data object Home : Route("home")

    data object PortfolioDetail : Route("portfolio/{portfolioId}") {
        fun createRoute(portfolioId: String) = "portfolio/$portfolioId"
    }
}
