package com.loptech.suitcasesmart.usecases.common.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.loptech.suitcasesmart.model.domain.Maleta
import com.loptech.suitcasesmart.usecases.common.MALETA_COLOR_OPTIONS
import com.loptech.suitcasesmart.usecases.common.hexToColor
import com.loptech.suitcasesmart.usecases.common.iconoForTipo
import com.loptech.suitcasesmart.usecases.common.maletaVisualForTipo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMaletaSheetForm(
    initialMaleta: Maleta? = null,
    onSave: (Maleta) -> Unit,
    onDismiss: () -> Unit
) {
    val isEditing = initialMaleta != null
    var nombre by remember { mutableStateOf(initialMaleta?.nombre ?: "") }
    var tipo by remember { mutableStateOf(initialMaleta?.tipo ?: "") }
    var expanded by remember { mutableStateOf(false) }
    var selectedColorHex by remember { mutableStateOf(initialMaleta?.color ?: "") }
    var submitted by remember { mutableStateOf(false) }
    val tipos = listOf("carry-on", "grande", "mochila", "personal")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .imePadding()
            .nestedScroll(rememberNestedScrollInteropConnection()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isEditing) "Editar Maleta" else "Agregar nueva Maleta",
            modifier = Modifier.wrapContentSize(Alignment.Center),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            isError = submitted && nombre.isBlank(),
            supportingText = if (submitted && nombre.isBlank()) {
                { Text("El nombre es requerido") }
            } else null
        )

        Spacer(modifier = Modifier.height(4.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = tipo,
                onValueChange = {},
                readOnly = true,
                label = { Text("Tipo") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                isError = submitted && tipo.isEmpty(),
                supportingText = if (submitted && tipo.isEmpty()) {
                    { Text("Selecciona un tipo") }
                } else null
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                tipos.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            tipo = option
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selector de color
        Text(
            text = "Color",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MALETA_COLOR_OPTIONS.forEach { (name, hex) ->
                val isSelected = selectedColorHex == hex
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(color = hexToColor(hex), shape = CircleShape)
                        .border(
                            width = if (isSelected) 3.dp else 0.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable { selectedColorHex = hex },
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = name,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        if (submitted && selectedColorHex.isEmpty()) {
            Text(
                text = "Selecciona un color",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            )
        }

        // Preview: icono en el color seleccionado
        AnimatedVisibility(visible = tipo.isNotEmpty()) {
            val visual = maletaVisualForTipo(tipo)
            val previewColor = if (selectedColorHex.isNotEmpty()) hexToColor(selectedColorHex) else visual.color
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(color = previewColor, shape = RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = visual.icon,
                        contentDescription = tipo,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .padding(bottom = 15.dp)
        ) {
            Button(
                onClick = {
                    submitted = true
                    if (nombre.isNotBlank() && tipo.isNotEmpty() && selectedColorHex.isNotEmpty()) {
                        onSave(
                            Maleta(
                                nombre = nombre,
                                tipo = tipo,
                                color = selectedColorHex,
                                icono = iconoForTipo(tipo)
                            )
                        )
                    }
                }
            ) {
                Text("Guardar")
            }
            OutlinedButton(
                onClick = { onDismiss() },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Cancelar")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
