package com.loptech.suitcasesmart.usecases.launch

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.loptech.suitcasesmart.R
import com.loptech.suitcasesmart.navigation.AppScreens
import com.loptech.suitcasesmart.provider.preferences.PreferencesManager
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {
    val context = LocalContext.current

    val preferencesManager = remember {
        PreferencesManager(context)
    }
    val data = remember { mutableStateOf(preferencesManager.getData("isOnboarding", false)) }

    LaunchedEffect(key1 = true) {
        delay(3000)
        navController.popBackStack()
        if (data.value){
            navController.navigate(AppScreens.LoginScreen.route)
        }else {
            navController.navigate(AppScreens.OnboardingScreen.route)
        }
    }
    Splash()
}

@Composable
fun Splash() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.suitcase),
            contentDescription = "SuitcaseSmart",
            modifier = Modifier.size(150.dp, 150.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    Splash()
}