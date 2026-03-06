package com.loptech.suitcasesmart.usecases.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.loptech.suitcasesmart.R
import com.loptech.suitcasesmart.navigation.AppScreens
import com.loptech.suitcasesmart.provider.preferences.PreferencesManager
import com.loptech.suitcasesmart.model.domain.OnBoardingData

@Composable
fun OnboardScreen(navController: NavHostController) {

    val onboardPages = onboardPagesList

    val currentPage = remember { mutableStateOf(0) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        OnBoardImageView(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(0.5f)
                .fillMaxHeight(0.7f),
            imageRes = onboardPages[currentPage.value].image
        )

        OnBoardDetails(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            currentPage = onboardPages[currentPage.value]
        )

        OnBoardNavButton(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp),
            currentPage = currentPage.value,
            navController = navController,
            noOfPages = onboardPages.size
        ) {
            currentPage.value++
        }

        TabSelector(
            onboardPages = onboardPages,
            currentPage = currentPage.value
        ) { index ->
            currentPage.value = index
        }
    }
}

val onboardPagesList = listOf(
    OnBoardingData(
        image = R.drawable.play_store_icon,
        title = "Bienvenid@s a SuitcaseSmart",
        description = "Bienvenid@ a SuitcaseSmart la app que te ayuda a ordenar tu maleta y tu viaje."
    ), OnBoardingData(
        image = R.drawable.play_store_icon,
        title = "Primero Inicia Sesion o Registrate",
        description = "Para poder tener la mejor experiencia es necesario iniciar sesion "
    ), OnBoardingData(
        image = R.drawable.play_store_icon,
        title = "Listo",
        description = "Primero Crea tu viaje, salida, destino, agrega tus maletas y por ultimo agrega tu equipaje."
    )
)

@Composable
fun OnBoardImageView(modifier: Modifier = Modifier, imageRes: Int) {
    Box(modifier = modifier) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier.width(300.dp).height(300.dp).align(Alignment.Center).fillMaxWidth(),
            contentScale = ContentScale.FillWidth
        )
    }
}

@Composable
fun OnBoardDetails(
    modifier: Modifier = Modifier, currentPage: OnBoardingData
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = currentPage.title,
            style = MaterialTheme.typography.displaySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = currentPage.description,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun OnBoardNavButton(
    modifier: Modifier = Modifier, currentPage: Int, noOfPages: Int, navController: NavController, onNextClicked: () -> Unit
) {
    val context = LocalContext.current

    val preferencesManager = remember {
        PreferencesManager(context)
    }
    val data = remember { mutableStateOf(preferencesManager.getData("isOnboarding", false)) }


    Button(
        onClick = {
            if (currentPage < noOfPages - 1) {
                onNextClicked()
            } else {
                // Update data and save to SharedPreferences
                preferencesManager.saveData("isOnboarding", true)
                data.value = true

                // Handle onboarding completion
                navController.popBackStack()
                navController.navigate(AppScreens.LoginScreen.route)
            }
        }, modifier = modifier
    ) {
        Text(text = if (currentPage < noOfPages - 1) "Siguiente" else "Iniciar")
    }
}

@Suppress("DEPRECATION")
@Composable
fun TabSelector(onboardPages: List<OnBoardingData>, currentPage: Int, onTabSelected: (Int) -> Unit) {
    TabRow(
        selectedTabIndex = currentPage,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        onboardPages.forEachIndexed { index, _ ->
            Tab(selected = index == currentPage, onClick = {
                onTabSelected(index)
            }, modifier = Modifier.padding(16.dp), content = {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = if (index == currentPage) MaterialTheme.colorScheme.onPrimary
                            else Color.LightGray, shape = RoundedCornerShape(4.dp)
                        )
                )
            })
        }
    }
}