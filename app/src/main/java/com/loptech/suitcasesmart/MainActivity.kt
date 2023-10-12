package com.loptech.suitcasesmart

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.iid.FirebaseInstanceIdReceiver
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.loptech.suitcasesmart.firebase.GoogleAuthUiClient
import com.loptech.suitcasesmart.firebase.SignInviewModel
import com.loptech.suitcasesmart.navigation.AppScreens
import com.loptech.suitcasesmart.ui.theme.MainAccent
import com.loptech.suitcasesmart.ui.theme.SuitcaseSmartTheme
import com.loptech.suitcasesmart.usecases.launch.SplashScreen
import com.loptech.suitcasesmart.usecases.login.LoginScreen
import com.loptech.suitcasesmart.usecases.login.ProfileScreen
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

        notification()

        setContent {
            SuitcaseSmartTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = AppScreens.SplashScreen.route){
                        composable(AppScreens.LoginScreen.route){
                            val viewModel = viewModel<SignInviewModel>()
                            val state by viewModel.state.collectAsState()

                            LaunchedEffect(key1 = Unit ){
                                if (googleAuthUiClient.getSignedInUser() != null){
                                    navController.navigate(AppScreens.ProfileScreen.route)
                                }
                            }

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

                            LaunchedEffect(key1 = state.isSignInSuccessful){
                                if (state.isSignInSuccessful){
                                   Toast.makeText(
                                       applicationContext,
                                       "Sign in successful",
                                       Toast.LENGTH_LONG
                                   ).show()

                                    navController.navigate(AppScreens.ProfileScreen.route)
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
                                        Log.d("Click", "Clicking the button")
                                        val signInIntentSender = googleAuthUiClient.signIn()
                                        launcher.launch(
                                            IntentSenderRequest.Builder(
                                                signInIntentSender ?: return@launch
                                            ).build()
                                        ) //:launcher
                                    }
                                }) //:LoginScreen
                             


                        }//: composable sign in

                        //composable profile
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
                        }

                        //Splash Screen
                        composable(AppScreens.SplashScreen.route) {
                            SplashScreen(navController)
                        }//:Splash
                        
                        // Onboarding Screen
                        composable(AppScreens.OnboardingScreen.route){
                            OnboardScreen(navController = navController)
                        }//:Onboarding Screen

                        //Register Screen
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

                                    navController.navigate("profile")
                                    //viewModel.resetState()
                                }
                            }

                            RegistrationScreen(
                                state = state,
                                onRegister = viewModel::register,
                                onBack = { navController.popBackStack() },
                                onDismissDialog = viewModel::hideErrorDialog
                            )
                        }
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

