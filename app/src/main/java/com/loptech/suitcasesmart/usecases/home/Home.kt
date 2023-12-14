package com.loptech.suitcasesmart.usecases.home

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.loptech.suitcasesmart.R
import com.loptech.suitcasesmart.model.domain.StatusDatosViajes
import com.loptech.suitcasesmart.model.domain.UserData
import com.loptech.suitcasesmart.ui.theme.MainColorBlack
import com.loptech.suitcasesmart.ui.theme.MainGrey20
import com.loptech.suitcasesmart.usecases.common.rows.TravelRow
import com.loptech.suitcasesmart.usecases.common.views.AddTravelForm
import com.loptech.suitcasesmart.usecases.common.views.AddTravelSheetForm
import com.loptech.suitcasesmart.usecases.common.views.EventDialog
import kotlinx.coroutines.launch


enum class ProviderType{
    BASIC,
    GOOGLE,
    FACEBOOK
}
@RequiresApi(Build.VERSION_CODES.O)
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
    var showAddDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    //Sheet Values.
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }


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
                    scope.launch {
                        sheetState.show()
                    }
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
                    Box(modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
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
                            viaje.id?.let { Log.i("Viaje", it) }
                        })
                    }
                        //Sheet View
                        if (sheetState.isVisible) {
                            ModalBottomSheet(
                                onDismissRequest = {
                                    scope.launch {
                                        sheetState.hide()
                                    }
                                },
                                sheetState = sheetState
                            ) {
                                // Sheet content
                                AddTravelSheetForm(onSave = {
                                    /* Todo: Revisar el problema con el id del viaje, al guardar este se manda al documento, deberia ser solo el valor de los datos*/
                                    val viajeSaved = it
                                    viewmodel.addviaje(userData.userId.toString(), viajeSaved, {
                                        scope.launch {
                                            sheetState.hide()
                                            snackbarHostState.showSnackbar("Viaje Agregado exitosamente")
                                        }

                                        userData.userId?.let { it1 -> viewmodel.getViajes(it1) }
                                    }, {
                                        scope.launch {
                                            sheetState.hide()
                                        }
                                    })
                                }, onDissmiss = {
                                    scope.launch {
                                        sheetState.hide()
                                    }
                                })
                            }
                        }// Sheet is visible

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
