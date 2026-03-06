package com.loptech.suitcasesmart

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.loptech.suitcasesmart.firebase.GoogleAuthUiClient
import com.loptech.suitcasesmart.usecases.login.SignInviewModel
import com.loptech.suitcasesmart.navigation.AppScreens
import com.loptech.suitcasesmart.provider.preferences.PreferencesManager
import com.loptech.suitcasesmart.ui.theme.SuitcaseSmartTheme
import com.loptech.suitcasesmart.usecases.TravelDetail.SuitcaseDetail
import com.loptech.suitcasesmart.usecases.TravelDetail.TravelDetailViewModel
import com.loptech.suitcasesmart.usecases.home.HomeScreen
import com.loptech.suitcasesmart.usecases.home.HomeViewModel
import com.loptech.suitcasesmart.usecases.launch.SplashScreen
import com.loptech.suitcasesmart.usecases.login.LoginScreen
import com.loptech.suitcasesmart.usecases.profile.ProfileScreen
import com.loptech.suitcasesmart.usecases.onboarding.OnboardScreen
import com.loptech.suitcasesmart.usecases.registration.RegisterViewModel
import com.loptech.suitcasesmart.usecases.registration.RegistrationScreen
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private val TAG: String = "MainActivity"
    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
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
            SuitcaseSmartTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val preferencesManager = remember {
                        PreferencesManager(this)
                    }
                    val data = remember { mutableStateOf(preferencesManager.getData("isOnboarding", false)) }
                    val startDestination = if (data.value) AppScreens.LoginScreen.route else AppScreens.OnboardingScreen.route

                    NavHost(navController = navController, startDestination = startDestination) {

                        //MARK: LoginScreen
                        composable(AppScreens.LoginScreen.route) {
                            val viewModel = viewModel<SignInviewModel>()
                            val state by viewModel.state.collectAsState()

                            LaunchedEffect(key1 = Unit) {
                                if (googleAuthUiClient.getSignedInUser() != null) {
                                    navController.navigate(AppScreens.HomeScreen.route)
                                }
                            }

                            val launcher = rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.StartIntentSenderForResult(),
                                onResult = { result ->
                                    if (result.resultCode == RESULT_OK) {
                                        lifecycleScope.launch {
                                            val signInResult = googleAuthUiClient.getSignInResultWithIntent(
                                                intent = result.data ?: return@launch
                                            )
                                            viewModel.onSignInresult(signInResult)
                                        }
                                    }
                                }
                            )

                            LaunchedEffect(key1 = state.isSignInSuccessful) {
                                if (state.isSignInSuccessful) {
                                    Toast.makeText(applicationContext, "Sign in successful", Toast.LENGTH_LONG).show()
                                    navController.navigate(AppScreens.HomeScreen.route)
                                    viewModel.resetState()
                                }
                            }

                            LoginScreen(
                                state = state,
                                onLogin = viewModel::login,
                                onNavigateToRegister = {
                                    navController.navigate(AppScreens.RegisterScreen.route)
                                },
                                onDismissDialog = viewModel::hideErrorDialog,
                                onSignInClick = {
                                    lifecycleScope.launch {
                                        val signInIntentSender = googleAuthUiClient.signIn()
                                        if (signInIntentSender == null) {
                                            Toast.makeText(applicationContext, "No se pudo iniciar Google Sign-In. Intenta de nuevo.", Toast.LENGTH_LONG).show()
                                            return@launch
                                        }
                                        launcher.launch(
                                            IntentSenderRequest.Builder(signInIntentSender).build()
                                        )
                                    }
                                })
                        }

                        //MARK: Profile Screen
                        composable(AppScreens.ProfileScreen.route) {
                            googleAuthUiClient.getSignedInUser()?.let { it1 ->
                                ProfileScreen(
                                    userData = it1,
                                    onsignOut = {
                                        lifecycleScope.launch {
                                            googleAuthUiClient.signOut()
                                            Toast.makeText(applicationContext, "Signed Out", Toast.LENGTH_LONG).show()
                                            navController.popBackStack()
                                        }
                                    })
                            }
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
                                    navController.navigate(AppScreens.HomeScreen.route)
                                }
                            }

                            RegistrationScreen(
                                state = state,
                                onRegister = viewModel::register,
                                onBack = { navController.popBackStack() },
                                onDismissDialog = viewModel::hideErrorDialog
                            )
                        }

                        //MARK: Home Screen
                        composable(AppScreens.HomeScreen.route) {
                            val viewModel = viewModel<HomeViewModel>()
                            val state by viewModel.status.collectAsState()

                            googleAuthUiClient.getSignedInUser()?.let { it1 ->
                                HomeScreen(
                                    userData = it1,
                                    state = state,
                                    viewmodel = viewModel,
                                    navigateToDetail = {
                                        navController.navigate(AppScreens.SuitcaseDetailScreen.route + "/" + it)
                                    },
                                    navigateToProfile = {
                                        navController.navigate(AppScreens.ProfileScreen.route)
                                    },
                                    onSignOut = {
                                        lifecycleScope.launch {
                                            googleAuthUiClient.signOut()
                                            Toast.makeText(applicationContext, "Signed Out", Toast.LENGTH_LONG).show()
                                            navController.navigate(AppScreens.LoginScreen.route) {
                                                popUpTo(0) { inclusive = true }
                                            }
                                        }
                                    }
                                )
                            }
                        }

                        //MARK: SuitcaseDetail Screen
                        composable(
                            AppScreens.SuitcaseDetailScreen.route + "/" + "{maletaId}"
                        ) { backStackEntry ->
                            val viewModel = viewModel<TravelDetailViewModel>()
                            val state by viewModel.status.collectAsState()
                            googleAuthUiClient.getSignedInUser()?.let { it1 ->
                                SuitcaseDetail(
                                    userData = it1,
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
                val msg = "Este es el token del dispositivo $token"
                Log.d(TAG, msg)
            }
        )

        var url = intent.getStringExtra("url")
        url?.let {
            print("Este es el valor $url")
        }
    }
}
