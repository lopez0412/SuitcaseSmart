package com.loptech.suitcasesmart.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.navArgument
import com.loptech.suitcasesmart.usecases.home.ProviderType

sealed class AppScreens(val route:String, val arguments: List<NamedNavArgument> = emptyList()) {
    object SplashScreen: AppScreens("splash_screen")
    object OnboardingScreen: AppScreens("onboarding_screen")
    object LoginScreen: AppScreens("login_screen")
    object RegisterScreen: AppScreens("register_screen")
    object ProfileScreen: AppScreens("profile")
    object MainScreen: AppScreens("main_screen", listOf(
        navArgument("email") { defaultValue = "" },
        navArgument("password") { defaultValue = "" }
    ))
    object HomeScreen: AppScreens("home_screen/{providerType}", listOf(
        navArgument("providerType") { defaultValue = ProviderType.BASIC }
    ))
}