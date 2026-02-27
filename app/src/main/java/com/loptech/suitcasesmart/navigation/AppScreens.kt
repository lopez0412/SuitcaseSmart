package com.loptech.suitcasesmart.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.navArgument

sealed class AppScreens(val route: String, val arguments: List<NamedNavArgument> = emptyList()) {
    object OnboardingScreen : AppScreens("onboarding_screen")
    object LoginScreen : AppScreens("login_screen")
    object RegisterScreen : AppScreens("register_screen")
    object ProfileScreen : AppScreens("profile")
    object HomeScreen : AppScreens("home_screen")
    object SuitcaseDetailScreen : AppScreens("detail_screen", listOf(
        navArgument("maletaId") { defaultValue = "" }
    ))
}
