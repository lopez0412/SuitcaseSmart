package com.loptech.suitcasesmart.usecases.TravelDetail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loptech.suitcasesmart.model.domain.Item
import com.loptech.suitcasesmart.model.domain.StatusDatosMaletas
import com.loptech.suitcasesmart.model.domain.UserData
import com.loptech.suitcasesmart.ui.theme.AmberPendingBg
import com.loptech.suitcasesmart.ui.theme.AviationNavy
import com.loptech.suitcasesmart.ui.theme.AviationNavyLight
import com.loptech.suitcasesmart.ui.theme.GreenPacked
import com.loptech.suitcasesmart.ui.theme.GreenPackedBg
import com.loptech.suitcasesmart.ui.theme.SkyLight
import com.loptech.suitcasesmart.usecases.common.hexToColor
import com.loptech.suitcasesmart.usecases.common.maletaVisualForTipo
import com.loptech.suitcasesmart.usecases.common.views.AddItemSheetForm
import com.loptech.suitcasesmart.usecases.common.views.EventDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SuitcaseDetail(
    userData: UserData,
    state: StatusDatosMaletas,
    viewmodel: TravelDetailViewModel,
    maletaId: String,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheet by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }
    var itemToEdit by remember { mutableStateOf<Item?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<Item?>(null) }

    val maleta by viewmodel.maleta.collectAsState()
    val itemsList by viewmodel.items.collectAsState()

    val total = itemsList.size
    val empacados = itemsList.count { it.estado == "empacado" || it.estado == "usado" }

    LaunchedEffect(key1 = Unit) {
        viewmodel.getMaleta(userData.userId.toString(), maletaId)
        viewmodel.getItems(userData.userId.toString(), maletaId)
    }

    val tabLabels = listOf("Todos", "Por empacar", "Empacado", "Usado")
    val tabEstados = listOf(null, "por_empacar", "empacado", "usado")
    val displayedItems = tabEstados[selectedTab]?.let { estado ->
        itemsList.filter { it.estado == estado }
    } ?: itemsList

    val visual = maletaVisualForTipo(maleta.tipo)
    val iconColor = if (maleta.color.isNotEmpty()) hexToColor(maleta.color) else visual.color

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AviationNavy)
                    .windowInsetsPadding(WindowInsets.statusBars)
            ) {
                // Back button
                Row(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 12.dp, bottom = 4.dp)
                        .clickable { onBack() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = SkyLight,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Mis Maletas", color = SkyLight, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }

                // Maleta info row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .background(AviationNavyLight.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = visual.icon,
                            contentDescription = null,
                            tint = SkyLight,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = maleta.nombre.ifEmpty { "Maleta" },
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${maleta.tipo} · $total item${if (total != 1) "s" else ""}",
                            color = SkyLight.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                    }
                }

                // Progress bar with counter
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LinearProgressIndicator(
                        progress = { if (total > 0) empacados.toFloat() / total else 0f },
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp),
                        color = AviationNavyLight,
                        trackColor = Color.White.copy(alpha = 0.25f)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "$empacados/$total",
                        color = SkyLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .shadow(elevation = 6.dp, shape = RoundedCornerShape(16.dp))
                    .background(AviationNavy, RoundedCornerShape(16.dp))
                    .clickable {
                        itemToEdit = null
                        showSheet = true
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Add, "Agregar item", tint = Color.White, modifier = Modifier.size(24.dp))
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
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
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                // Filter tabs
                item(key = "tabs") {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(tabLabels.size) { idx ->
                            val active = selectedTab == idx
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (active) AviationNavy else MaterialTheme.colorScheme.surfaceVariant,
                                        RoundedCornerShape(100.dp)
                                    )
                                    .clickable { selectedTab = idx }
                                    .padding(horizontal = 14.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    text = tabLabels[idx],
                                    fontSize = 13.sp,
                                    fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (active) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Items agrupados por categoría
                val grouped = displayedItems.groupBy { it.categoria }
                grouped.forEach { (categoria, itemsInGroup) ->
                    item(key = "cat_$categoria") {
                        Text(
                            text = categoria.replaceFirstChar { it.uppercase() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .padding(top = if (categoria == grouped.keys.first()) 0.dp else 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = androidx.compose.ui.unit.TextUnit(0.12f, androidx.compose.ui.unit.TextUnitType.Em)
                            ),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    items(itemsInGroup, key = { it.id.ifEmpty { it.nombre } }) { item ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { value ->
                                if (value == SwipeToDismissBoxValue.EndToStart) {
                                    itemToDelete = item
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
                                        .padding(horizontal = 16.dp, vertical = 4.dp)
                                        .background(MaterialTheme.colorScheme.error, RoundedCornerShape(14.dp))
                                        .padding(end = 20.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(Icons.Filled.Delete, "Eliminar", tint = Color.White)
                                }
                            }
                        ) {
                            ItemRow(
                                item = item,
                                onCheckboxClick = {
                                    userData.userId?.let { viewmodel.updateEstado(it, maletaId, item) }
                                },
                                onLongClick = {
                                    itemToEdit = item
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
                            itemToEdit = null
                        }
                    },
                    sheetState = sheetState,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    shape = MaterialTheme.shapes.large,
                    tonalElevation = 8.dp
                ) {
                    val editingItem = itemToEdit
                    AddItemSheetForm(
                        initialItem = editingItem,
                        onSave = { formItem ->
                            if (editingItem != null) {
                                val updated = editingItem.copy(
                                    nombre = formItem.nombre,
                                    categoria = formItem.categoria,
                                    cantidad = formItem.cantidad
                                )
                                viewmodel.updateItem(userData.userId.toString(), maletaId, updated)
                                scope.launch { sheetState.hide() }.invokeOnCompletion { showSheet = false; itemToEdit = null }
                                scope.launch { snackbarHostState.showSnackbar("Item actualizado") }
                            } else {
                                viewmodel.addItem(userData.userId.toString(), maletaId, formItem, {
                                    scope.launch { sheetState.hide() }.invokeOnCompletion { showSheet = false }
                                    scope.launch { snackbarHostState.showSnackbar("Item agregado") }
                                }, {
                                    scope.launch { sheetState.hide() }.invokeOnCompletion { showSheet = false }
                                })
                            }
                        },
                        onDissmiss = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                showSheet = false
                                itemToEdit = null
                            }
                        }
                    )
                }
            }
        }

        if (state.travelError != null) {
            EventDialog(
                errorMessage = state.travelError,
                onDismiss = { viewmodel.hideErrorDialog() }
            )
        }

        if (showDeleteDialog && itemToDelete != null) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                    itemToDelete = null
                },
                title = { Text("Eliminar item") },
                text = { Text("¿Eliminar \"${itemToDelete?.nombre}\"? Esta acción no se puede deshacer.") },
                confirmButton = {
                    TextButton(onClick = {
                        itemToDelete?.let { item ->
                            viewmodel.deleteItem(userData.userId.toString(), maletaId, item)
                        }
                        showDeleteDialog = false
                        itemToDelete = null
                    }) {
                        Text("Eliminar", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDeleteDialog = false
                        itemToDelete = null
                    }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemRow(
    item: Item,
    onCheckboxClick: () -> Unit,
    onLongClick: () -> Unit = {}
) {
    val isUsado = item.estado == "usado"
    val isEmpacado = item.estado == "empacado"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .border(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                RoundedCornerShape(14.dp)
            )
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .combinedClickable(onClick = {}, onLongClick = onLongClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Checkbox
        val checkboxBg = when (item.estado) {
            "empacado" -> AviationNavyLight
            "usado"    -> GreenPacked
            else       -> null
        }
        Box(
            modifier = Modifier
                .size(22.dp)
                .then(
                    if (checkboxBg != null)
                        Modifier.background(checkboxBg, RoundedCornerShape(8.dp))
                    else
                        Modifier.border(1.5.dp, Color.LightGray, RoundedCornerShape(8.dp))
                )
                .clickable { onCheckboxClick() },
            contentAlignment = Alignment.Center
        ) {
            if (checkboxBg != null) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.nombre,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = if (isUsado) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        else MaterialTheme.colorScheme.onSurface,
                textDecoration = if (isUsado) TextDecoration.LineThrough else null
            )
            Text(
                text = "${item.categoria} · ${item.cantidad} ud.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Estado badge
        val (badgeBg, badgeText, badgeColor) = when (item.estado) {
            "empacado"    -> Triple(AviationNavyLight.copy(alpha = 0.12f), "Empacado",   AviationNavyLight)
            "usado"       -> Triple(GreenPackedBg,                          "Usado",       GreenPacked)
            else          -> Triple(AmberPendingBg,                         "Pendiente",   Color(0xFFE67E22))
        }
        Box(
            modifier = Modifier
                .background(badgeBg, RoundedCornerShape(50.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = badgeText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = badgeColor
            )
        }
    }
}
