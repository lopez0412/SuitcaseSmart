package com.loptech.suitcasesmart.usecases.checklist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loptech.suitcasesmart.model.domain.UserData
import com.loptech.suitcasesmart.ui.theme.AviationNavy
import com.loptech.suitcasesmart.ui.theme.SkyBlue
import com.loptech.suitcasesmart.ui.theme.SkyLight
import com.loptech.suitcasesmart.usecases.TravelDetail.ItemRow
import com.loptech.suitcasesmart.usecases.home.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistScreen(
    userData: UserData,
    viewmodel: HomeViewModel
) {
    val pendingItems by viewmodel.pendingItems.collectAsState()
    val progreso by viewmodel.progreso.collectAsState()

    val totalItems = progreso.values.sumOf { it.second }
    val packedItems = progreso.values.sumOf { it.first }
    val percent = if (totalItems > 0) packedItems * 100 / totalItems else 0

    val grouped = pendingItems.groupBy { (maleta, _) -> maleta }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Checklist",
                            style = MaterialTheme.typography.titleLarge
                        )
                        if (pendingItems.isNotEmpty()) {
                            Text(
                                text = "${pendingItems.size} item${if (pendingItems.size != 1) "s" else ""} pendiente${if (pendingItems.size != 1) "s" else ""}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        if (pendingItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("✓", fontSize = 48.sp, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "¡Todo empacado!",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                // Summary card
                item(key = "summary") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .background(AviationNavy, RoundedCornerShape(16.dp))
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Progreso total",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "$packedItems de $totalItems items empacados",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SkyLight.copy(alpha = 0.8f)
                                )
                            )
                        }
                        // Circular progress
                        Box(
                            modifier = Modifier.size(44.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            androidx.compose.foundation.Canvas(modifier = Modifier.size(44.dp)) {
                                val sweep = (percent / 100f) * 360f
                                drawArc(
                                    color = Color.White.copy(alpha = 0.15f),
                                    startAngle = -90f,
                                    sweepAngle = 360f,
                                    useCenter = false,
                                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 5.dp.toPx())
                                )
                                drawArc(
                                    color = SkyBlue,
                                    startAngle = -90f,
                                    sweepAngle = sweep,
                                    useCenter = false,
                                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 5.dp.toPx())
                                )
                            }
                            Text(
                                text = "$percent%",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }
                }

                grouped.forEach { (maleta, pairs) ->
                    item(key = "header_${maleta.id}") {
                        Text(
                            text = maleta.nombre.uppercase(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.sp
                        )
                    }
                    items(pairs.map { it.second }, key = { it.id.ifEmpty { it.nombre } }) { item ->
                        ItemRow(
                            item = item,
                            onCheckboxClick = {
                                userData.userId?.let { userId ->
                                    viewmodel.updateEstado(userId, maleta.id, item)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
