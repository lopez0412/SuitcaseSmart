package com.loptech.suitcasesmart.navigation

sealed class AppScreens(val route: String) {
    object OnboardingScreen : AppScreens("onboarding_screen")
    object LoginScreen : AppScreens("login_screen")
    object RegisterScreen : AppScreens("register_screen")
    object MainShell : AppScreens("main_shell")
    object HomeScreen : AppScreens("home_screen")
    object ChecklistScreen : AppScreens("checklist_screen")
    object ProfileScreen : AppScreens("profile")
    object SuitcaseDetailScreen : AppScreens("detail_screen")
}
