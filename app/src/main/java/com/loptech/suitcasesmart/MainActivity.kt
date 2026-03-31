package com.loptech.suitcasesmart

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.loptech.suitcasesmart.firebase.GoogleAuthUiClient
import com.loptech.suitcasesmart.model.domain.UserData
import com.loptech.suitcasesmart.navigation.AppScreens
import com.loptech.suitcasesmart.provider.preferences.PreferencesManager
import com.loptech.suitcasesmart.ui.theme.AviationNavy
import com.loptech.suitcasesmart.ui.theme.SkyPale
import com.loptech.suitcasesmart.ui.theme.SuitcaseSmartTheme
import com.loptech.suitcasesmart.usecases.TravelDetail.SuitcaseDetail
import com.loptech.suitcasesmart.usecases.TravelDetail.TravelDetailViewModel
import com.loptech.suitcasesmart.usecases.checklist.ChecklistScreen
import com.loptech.suitcasesmart.usecases.home.HomeScreen
import com.loptech.suitcasesmart.usecases.home.HomeViewModel
import com.loptech.suitcasesmart.usecases.login.LoginScreen
import com.loptech.suitcasesmart.usecases.login.SignInviewModel
import com.loptech.suitcasesmart.usecases.onboarding.OnboardScreen
import com.loptech.suitcasesmart.usecases.profile.ProfileScreen
import com.loptech.suitcasesmart.usecases.registration.RegisterViewModel
import com.loptech.suitcasesmart.usecases.registration.RegistrationScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val TAG: String = "MainActivity"
    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(context = applicationContext)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 60
        }
        val firebaseConfig = Firebase.remoteConfig
        firebaseConfig.setConfigSettingsAsync(configSettings)
        firebaseConfig.setDefaultsAsync(mapOf("show_error_btn" to false))

        setContent {
            SuitcaseSmartTheme(darkTheme = isDarkTheme.value) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val preferencesManager = remember { PreferencesManager(this) }
                    val data = remember { mutableStateOf(preferencesManager.getData("isOnboarding", false)) }
                    val isDarkTheme = remember { mutableStateOf(preferencesManager.getData("isDarkTheme", false)) }
                    val startDestination = if (data.value) AppScreens.LoginScreen.route else AppScreens.OnboardingScreen.route

                    val toggleTheme: () -> Unit = {
                        val newValue = !isDarkTheme.value
                        isDarkTheme.value = newValue
                        preferencesManager.saveData("isDarkTheme", newValue)
                    }

                    val signOut: () -> Unit = {
                        lifecycleScope.launch {
                            googleAuthUiClient.signOut()
                            Toast.makeText(applicationContext, "Signed Out", Toast.LENGTH_LONG).show()
                            navController.navigate(AppScreens.LoginScreen.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }

                    NavHost(navController = navController, startDestination = startDestination) {

                        //MARK: LoginScreen
                        composable(AppScreens.LoginScreen.route) {
                            val viewModel = viewModel<SignInviewModel>()
                            val state by viewModel.state.collectAsState()

                            LaunchedEffect(key1 = Unit) {
                                if (googleAuthUiClient.getSignedInUser() != null) {
                                    navController.navigate(AppScreens.MainShell.route)
                                }
                            }
                            LaunchedEffect(key1 = state.isSignInSuccessful) {
                                if (state.isSignInSuccessful) {
                                    Toast.makeText(applicationContext, "Sign in successful", Toast.LENGTH_LONG).show()
                                    navController.navigate(AppScreens.MainShell.route)
                                    viewModel.resetState()
                                }
                            }
                            LoginScreen(
                                state = state,
                                onLogin = viewModel::login,
                                onNavigateToRegister = { navController.navigate(AppScreens.RegisterScreen.route) },
                                onDismissDialog = viewModel::hideErrorDialog,
                                onSignInClick = {
                                    lifecycleScope.launch {
                                        val result = googleAuthUiClient.signIn(this@MainActivity)
                                        viewModel.onSignInresult(result)
                                    }
                                }
                            )
                        }

                        //MARK: Onboarding Screen
                        composable(AppScreens.OnboardingScreen.route) {
                            OnboardScreen(navController = navController)
                        }

                        //MARK: Register Screen
                        composable(AppScreens.RegisterScreen.route) {
                            val viewModel: RegisterViewModel = viewModel()
                            val state by viewModel.state.collectAsState()

                            LaunchedEffect(key1 = state.successRegister) {
                                if (state.successRegister) {
                                    Toast.makeText(applicationContext, "Sign in successful", Toast.LENGTH_LONG).show()
                                    navController.navigate(AppScreens.MainShell.route)
                                }
                            }
                            RegistrationScreen(
                                state = state,
                                onRegister = viewModel::register,
                                onBack = { navController.popBackStack() },
                                onDismissDialog = viewModel::hideErrorDialog
                            )
                        }

                        //MARK: Main Shell (Home + Checklist + Profile tabs)
                        composable(AppScreens.MainShell.route) {
                            val homeViewModel = viewModel<HomeViewModel>()
                            googleAuthUiClient.getSignedInUser()?.let { userData ->
                                MainAppShell(
                                    userData = userData,
                                    homeViewModel = homeViewModel,
                                    navigateToDetail = { maletaId ->
                                        navController.navigate(AppScreens.SuitcaseDetailScreen.route + "/$maletaId")
                                    },
                                    onSignOut = signOut,
                                    isDarkTheme = isDarkTheme.value,
                                    onToggleTheme = toggleTheme
                                )
                            }
                        }

                        //MARK: SuitcaseDetail Screen (outside shell — no bottom nav)
                        composable(AppScreens.SuitcaseDetailScreen.route + "/{maletaId}") { backStackEntry ->
                            val viewModel = viewModel<TravelDetailViewModel>()
                            val state by viewModel.status.collectAsState()
                            googleAuthUiClient.getSignedInUser()?.let { userData ->
                                SuitcaseDetail(
                                    userData = userData,
                                    state = state,
                                    viewmodel = viewModel,
                                    maletaId = backStackEntry.arguments?.getString("maletaId").toString(),
                                    onBack = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun notification() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(
            OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }
                val token = task.result
                Log.d(TAG, "Este es el token del dispositivo $token")
            }
        )
        val url = intent.getStringExtra("url")
        url?.let { print("Este es el valor $url") }
    }
}

// ---- Tab definitions ----
private data class BottomTab(val route: String, val label: String, val icon: ImageVector)

private val bottomTabs = listOf(
    BottomTab(AppScreens.HomeScreen.route,     "Maletas",   Icons.Filled.Luggage),
    BottomTab(AppScreens.ChecklistScreen.route,"Checklist", Icons.Filled.Assignment),
    BottomTab(AppScreens.ProfileScreen.route,  "Perfil",    Icons.Filled.Person),
)

@Composable
private fun MainAppShell(
    userData: UserData,
    homeViewModel: HomeViewModel,
    navigateToDetail: (String) -> Unit,
    onSignOut: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val innerNavController = rememberNavController()
    val state by homeViewModel.status.collectAsState()
    val maletas by homeViewModel.maletas.collectAsState()
    val progreso by homeViewModel.progreso.collectAsState()

    LaunchedEffect(Unit) {
        homeViewModel.getMaletas(userData.userId.toString())
    }

    Scaffold(
        bottomBar = {
            AppBottomNavigationBar(navController = innerNavController)
        }
    ) { paddingValues ->
        NavHost(
            navController = innerNavController,
            startDestination = AppScreens.HomeScreen.route,
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
        ) {
            composable(AppScreens.HomeScreen.route) {
                HomeScreen(
                    userData = userData,
                    state = state,
                    viewmodel = homeViewModel,
                    navigateToDetail = navigateToDetail
                )
            }
            composable(AppScreens.ChecklistScreen.route) {
                ChecklistScreen(
                    userData = userData,
                    viewmodel = homeViewModel
                )
            }
            composable(AppScreens.ProfileScreen.route) {
                ProfileScreen(
                    userData = userData,
                    maletaCount = maletas.size,
                    itemCount = progreso.values.sumOf { it.second },
                    packedCount = progreso.values.sumOf { it.first },
                    onSignOut = onSignOut,
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = onToggleTheme
                )
            }
        }
    }
}

@Composable
private fun AppBottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        bottomTabs.forEach { tab ->
            val selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(tab.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    androidx.compose.material3.Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label
                    )
                },
                label = { Text(tab.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AviationNavy,
                    selectedTextColor = AviationNavy,
                    indicatorColor = SkyPale,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
