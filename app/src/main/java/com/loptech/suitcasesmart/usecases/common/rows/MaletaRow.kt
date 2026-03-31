package com.loptech.suitcasesmart.usecases.common.rows

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loptech.suitcasesmart.model.domain.Maleta
import com.loptech.suitcasesmart.ui.theme.AmberPendingBg
import com.loptech.suitcasesmart.ui.theme.GreenPackedBg
import com.loptech.suitcasesmart.usecases.common.hexToColor
import com.loptech.suitcasesmart.usecases.common.maletaVisualForTipo

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MaletaRow(
    maleta: Maleta,
    empacados: Int = 0,
    total: Int = 0,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {}
) {
    val visual = maletaVisualForTipo(maleta.tipo)
    val iconColor = if (maleta.color.isNotEmpty()) hexToColor(maleta.color) else visual.color
    val pending = total - empacados

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
    ) {
        // Lateral accent bar
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(88.dp)
                .background(iconColor)
        )

        // Icon wrapper
        Box(
            modifier = Modifier
                .padding(start = 12.dp, top = 16.dp)
                .size(48.dp)
                .background(iconColor.copy(alpha = 0.12f), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = visual.icon,
                contentDescription = maleta.tipo,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Content
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(top = 14.dp, end = 12.dp, bottom = 12.dp)
        ) {
            Text(
                text = maleta.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = maleta.tipo,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (total > 0) {
                Spacer(modifier = Modifier.height(8.dp))

                // Stat pills
                Row {
                    if (pending > 0) {
                        Box(
                            modifier = Modifier
                                .background(AmberPendingBg, RoundedCornerShape(50.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "$pending pendientes",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFE67E22)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    if (empacados > 0) {
                        Box(
                            modifier = Modifier
                                .background(GreenPackedBg, RoundedCornerShape(50.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "$empacados empacados",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF27AE60)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Colored progress bar
                LinearProgressIndicator(
                    progress = { empacados.toFloat() / total },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(50)),
                    color = iconColor,
                    trackColor = iconColor.copy(alpha = 0.15f)
                )
            }
        }
    }
}
