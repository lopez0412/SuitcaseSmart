package com.loptech.suitcasesmart.usecases.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.loptech.suitcasesmart.model.domain.StatusDatosViajes
import com.loptech.suitcasesmart.model.domain.UserData
import com.loptech.suitcasesmart.model.domain.Viaje
import com.loptech.suitcasesmart.ui.theme.MainColorBlack
import com.loptech.suitcasesmart.ui.theme.MainGrey20
import com.loptech.suitcasesmart.usecases.common.rows.TravelRow
import com.loptech.suitcasesmart.usecases.common.views.AddTravelForm
import com.loptech.suitcasesmart.usecases.common.views.EventDialog
import kotlinx.coroutines.launch


enum class ProviderType{
    BASIC,
    GOOGLE,
    FACEBOOK
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userData: UserData,
    state: StatusDatosViajes,
    viewmodel: HomeViewModel,
    onsignOut: () -> Unit
){
    //MARK: Properties
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    var showAddDialog = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = Unit){
        viewmodel.getViajes(userData.userId.toString())
    }

    //MARK: Body
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text("Viajes") },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MainColorBlack,
                    titleContentColor = MainGrey20
                ),
                actions ={
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Menu",
                            tint = MainGrey20
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(containerColor = MainColorBlack,
                contentColor = MainGrey20 ,
                shape = CircleShape,
                onClick = {
                    showAddDialog.value = true
                }
            ){
                Icon(Icons.Filled.Add, "Add viajes")
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },

        ) { innerPadding ->
                //If displayProgressBar
                if (state.displayProgressBar) {
                    Box(modifier = Modifier.padding(innerPadding).fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(50.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 6.dp
                        )
                    }//:BOX
                } else {
                    Column(modifier = Modifier.padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                    for (viaje in viewmodel.viajes) {
                        TravelRow(viaje = viaje, onClick = {

                        })
                    }
                        //ShowDialog
                    if (showAddDialog.value) {
                        AddTravelForm(onSave = {
                            val viajeSaved = it
                            viewmodel.addviaje(userData.userId.toString(), viajeSaved)
                            if (state.createTravelSuccessful) {
                                showAddDialog.value = false
                                scope.launch {
                                    snackbarHostState.showSnackbar("Viaje Agregado exitosamente")
                                }
                            }
                        },
                            onDissmiss = {
                                showAddDialog.value = false
                            })
                    }//: Show Dialog
                }//: Column
                }//:end if

                if (state.travelError != null) {
                    //Event Dialog
                    EventDialog(
                        errorMessage = state.travelError,
                        onDismiss = { viewmodel.hideErrorDialog() })
                }
    }
}
