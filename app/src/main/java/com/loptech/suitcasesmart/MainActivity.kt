package com.loptech.suitcasesmart

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.loptech.suitcasesmart.firebase.GoogleAuthUiClient
import com.loptech.suitcasesmart.usecases.login.SignInviewModel
import com.loptech.suitcasesmart.navigation.AppScreens
import com.loptech.suitcasesmart.ui.theme.SuitcaseSmartTheme
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
    //MARK: Properties
    //private lateinit var analytics: FirebaseAnalytics

    private val TAG: String = "MainActivity"
    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //lanza evento inicial.
        //analytics = Firebase.analytics


        //Analytics event...
        //val bundle = Bundle()
        //bundle.putString("message", "Integracion de analytics completa!!")
        //analytics.logEvent("InitScreen", bundle)

        //notification()

        //Remote Config
        val configSettings = remoteConfigSettings{
            minimumFetchIntervalInSeconds = 60 // 3600
        }
        val firebaseConfig = Firebase.remoteConfig
        firebaseConfig.setConfigSettingsAsync(configSettings)
        firebaseConfig.setDefaultsAsync(mapOf("show_error_btn" to false))


        setContent {
            SuitcaseSmartTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = AppScreens.SplashScreen.route){
                        //MARK: LoginScreen composable
                        composable(AppScreens.LoginScreen.route){
                            val viewModel = viewModel<SignInviewModel>()
                            val state by viewModel.state.collectAsState()

                            //confirm if user is logged in and send to Home Screen
                            LaunchedEffect(key1 = Unit ){
                                if (googleAuthUiClient.getSignedInUser() != null){
                                    navController.navigate(AppScreens.HomeScreen.route)
                                }
                            }//:LaunchedEffect

                            // launcher for sign in
                            val launcher = rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.StartIntentSenderForResult(),
                                onResult = {result ->
                                    if (result.resultCode == RESULT_OK){
                                        lifecycleScope.launch {
                                            val signInResult = googleAuthUiClient.getSignInResultWithIntent(
                                               intent = result.data ?: return@launch
                                            )
                                            viewModel.onSignInresult(signInResult)
                                        }
                                    }
                                }
                            )//: Launcher

                            //LaunchedEffect triggered by successful signin
                            LaunchedEffect(key1 = state.isSignInSuccessful){
                                if (state.isSignInSuccessful){
                                   Toast.makeText(
                                       applicationContext,
                                       "Sign in successful",
                                       Toast.LENGTH_LONG
                                   ).show()
                                    navController.navigate(AppScreens.HomeScreen.route)
                                    viewModel.resetState()
                                }
                            }//:LaunchedEffect

                            //Login Screen .
                            LoginScreen(
                                state = state,
                                onLogin = viewModel::login,
                                onNavigateToRegister = {
                                    navController.navigate(AppScreens.RegisterScreen.route)
                                },
                                onDismissDialog = viewModel::hideErrorDialog,
                                onSignInClick = {
                                    lifecycleScope.launch {
                                        //Log.d("Click", "Clicking the button")
                                        val signInIntentSender = googleAuthUiClient.signIn()
                                        launcher.launch(
                                            IntentSenderRequest.Builder(
                                                signInIntentSender ?: return@launch
                                            ).build()
                                        ) //:launcher
                                    }
                                }) //:LoginScreen
                             


                        }//: composable sign in

                        //MARK: composable profile
                        composable(
                            AppScreens.ProfileScreen.route
                        ){
                            googleAuthUiClient.getSignedInUser()?.let { it1 ->
                                ProfileScreen(
                                    userData = it1,
                                    onsignOut = {
                                        lifecycleScope.launch {
                                            googleAuthUiClient.signOut()
                                            Toast.makeText(applicationContext,
                                                "Signed Out",
                                                Toast.LENGTH_LONG).show()

                                            navController.popBackStack()
                                        }
                                    })
                            }
                        }//: Profile Screen

                        //MARK: Splash Screen
                        composable(AppScreens.SplashScreen.route) {
                            SplashScreen(navController)
                        }//:Splash
                        
                        //MARK: Onboarding Screen
                        composable(AppScreens.OnboardingScreen.route){
                            OnboardScreen(navController = navController)
                        }//:Onboarding Screen

                        //MARK: Register Screen
                        composable(AppScreens.RegisterScreen.route){
                            val viewModel: RegisterViewModel = viewModel()
                            val state by viewModel.state.collectAsState()

                            LaunchedEffect(key1 = state.successRegister){
                                if (state.successRegister){
                                    Toast.makeText(
                                        applicationContext,
                                        "Sign in successful",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    navController.navigate(AppScreens.HomeScreen.route)
                                    //viewModel.resetState()
                                }
                            }

                            RegistrationScreen(
                                state = state,
                                onRegister = viewModel::register,
                                onBack = { navController.popBackStack() },
                                onDismissDialog = viewModel::hideErrorDialog
                            )
                        }//: Register Screen


                        //MARK: Home Screen composable.
                        composable(
                            AppScreens.HomeScreen.route
                        ){
                            val viewModel = viewModel<HomeViewModel>()
                            val state by viewModel.status.collectAsState()

                            googleAuthUiClient.getSignedInUser()?.let { it1 ->
                                HomeScreen(
                                    userData = it1,
                                    state = state,
                                    viewmodel = viewModel,
                                    onsignOut = {
                                        lifecycleScope.launch {
                                            googleAuthUiClient.signOut()
                                            Toast.makeText(applicationContext,
                                                "Signed Out",
                                                Toast.LENGTH_LONG).show()

                                            navController.popBackStack()
                                        }
                                    })
                            }
                        }//: Home Screen

                    }
                }
            }
        }
    }

    fun notification(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener(
            OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result

                // Log and toast
                val msg = "Este es el token del dispositivo $token"
                Log.d(TAG, msg)
                //Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()

            }
        )

        //Temas (Topics)
        //FirebaseMessaging.getInstance().subscribeToTopic("Tutorial")

        //Recuperar info
        var url = intent.getStringExtra("url")
        url?.let {
            print("Este es el valor $url")
        }
    }


}

