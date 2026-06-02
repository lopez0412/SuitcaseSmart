package com.loptech.suitcasesmart.usecases.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.loptech.suitcasesmart.model.domain.Maleta
import com.loptech.suitcasesmart.model.domain.StatusDatosMaletas
import com.loptech.suitcasesmart.model.domain.UserData
import com.loptech.suitcasesmart.ui.theme.AviationNavy
import com.loptech.suitcasesmart.usecases.common.rows.MaletaRow
import com.loptech.suitcasesmart.usecases.common.views.AddMaletaSheetForm
import com.loptech.suitcasesmart.usecases.common.views.EventDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userData: UserData,
    state: StatusDatosMaletas,
    viewmodel: HomeViewModel,
    navigateToDetail: (String) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheet by remember { mutableStateOf(false) }
    var maletaToEdit by remember { mutableStateOf<Maleta?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var maletaToDelete by remember { mutableStateOf<Maleta?>(null) }

    val maletas by viewmodel.maletas.collectAsState()
    val progreso by viewmodel.progreso.collectAsState()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Mis Maletas",
                            style = MaterialTheme.typography.titleLarge
                        )
                        if (maletas.isNotEmpty()) {
                            Text(
                                text = "${maletas.size} maleta${if (maletas.size != 1) "s" else ""}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .shadow(elevation = 6.dp, shape = RoundedCornerShape(16.dp))
                    .background(AviationNavy, RoundedCornerShape(16.dp))
                    .clickable {
                        maletaToEdit = null
                        showSheet = true
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Add, "Agregar maleta", tint = Color.White, modifier = Modifier.size(24.dp))
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        if (state.displayProgressBar) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(50.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 6.dp
                )
            }
        } else if (maletas.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.Luggage,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "¡Agrega tu primera maleta!",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(maletas, key = { it.id.ifEmpty { it.nombre } }) { maleta ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { value ->
                            if (value == SwipeToDismissBoxValue.EndToStart) {
                                maletaToDelete = maleta
                                showDeleteDialog = true
                            }
                            false
                        }
                    )
                    SwipeToDismissBox(
                        state = dismissState,
                        enableDismissFromStartToEnd = false,
                        backgroundContent = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                                    .background(MaterialTheme.colorScheme.error, RoundedCornerShape(18.dp))
                                    .padding(end = 20.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Eliminar",
                                    tint = Color.White
                                )
                            }
                        }
                    ) {
                        val (emp, tot) = progreso[maleta.id] ?: Pair(0, 0)
                        MaletaRow(
                            maleta = maleta,
                            empacados = emp,
                            total = tot,
                            onClick = { if (maleta.id.isNotEmpty()) navigateToDetail(maleta.id) },
                            onLongClick = {
                                maletaToEdit = maleta
                                showSheet = true
                            }
                        )
                    }
                }
            }

        }

        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        showSheet = false
                        maletaToEdit = null
                    }
                },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                shape = MaterialTheme.shapes.large,
                tonalElevation = 8.dp
            ) {
                val editingMaleta = maletaToEdit
                AddMaletaSheetForm(
                    initialMaleta = editingMaleta,
                    onSave = { maleta ->
                        if (editingMaleta != null && editingMaleta.id.isNotEmpty()) {
                            viewmodel.updateMaleta(userData.userId.toString(), editingMaleta.id, maleta, {
                                scope.launch { sheetState.hide() }.invokeOnCompletion { showSheet = false; maletaToEdit = null }
                                scope.launch { snackbarHostState.showSnackbar("Maleta actualizada") }
                            }, {
                                scope.launch { sheetState.hide() }.invokeOnCompletion { showSheet = false; maletaToEdit = null }
                            })
                        } else {
                            viewmodel.addMaleta(userData.userId.toString(), maleta, {
                                scope.launch { sheetState.hide() }.invokeOnCompletion { showSheet = false }
                                scope.launch { snackbarHostState.showSnackbar("Maleta agregada exitosamente") }
                            }, {
                                scope.launch { sheetState.hide() }.invokeOnCompletion { showSheet = false }
                            })
                        }
                    },
                    onDismiss = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            showSheet = false
                            maletaToEdit = null
                        }
                    }
                )
            }
        }

        if (state.travelError != null) {
            EventDialog(
                errorMessage = state.travelError,
                onDismiss = { viewmodel.hideErrorDialog() }
            )
        }

        if (showDeleteDialog && maletaToDelete != null) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                    maletaToDelete = null
                },
                title = { Text("Eliminar maleta") },
                text = { Text("¿Eliminar \"${maletaToDelete?.nombre}\"? Esta acción no se puede deshacer.") },
                confirmButton = {
                    TextButton(onClick = {
                        maletaToDelete?.let { maleta ->
                            if (maleta.id.isNotEmpty()) {
                                viewmodel.deleteMaleta(userData.userId.toString(), maleta.id)
                            }
                        }
                        showDeleteDialog = false
                        maletaToDelete = null
                    }) {
                        Text("Eliminar", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDeleteDialog = false
                        maletaToDelete = null
                    }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}
