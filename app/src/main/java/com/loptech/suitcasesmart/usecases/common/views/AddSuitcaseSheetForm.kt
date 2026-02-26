package com.loptech.suitcasesmart.usecases.common.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.loptech.suitcasesmart.model.domain.Item

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemSheetForm(
    initialItem: Item? = null,
    onSave: (Item) -> Unit,
    onDissmiss: () -> Unit
) {
    val isEditing = initialItem != null
    var nombre by remember { mutableStateOf(initialItem?.nombre ?: "") }
    var cantidad by remember { mutableStateOf(initialItem?.cantidad?.toString() ?: "1") }
    var categoria by remember { mutableStateOf(initialItem?.categoria ?: "") }
    var categoriaExpanded by remember { mutableStateOf(false) }
    val categorias = listOf("ropa", "electronica", "documentos", "higiene", "medicamentos", "otros")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isEditing) "Editar Item" else "Agregar Item",
            modifier = Modifier.wrapContentSize(Alignment.Center),
            textAlign = TextAlign.Center,
            color = Color.Black
        )

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        ExposedDropdownMenuBox(
            expanded = categoriaExpanded,
            onExpandedChange = { categoriaExpanded = !categoriaExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = categoria,
                onValueChange = {},
                readOnly = true,
                label = { Text("Categoría") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoriaExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = categoriaExpanded,
                onDismissRequest = { categoriaExpanded = false }
            ) {
                categorias.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            categoria = option
                            categoriaExpanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = cantidad,
            onValueChange = { cantidad = it },
            label = { Text("Cantidad") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .padding(bottom = 15.dp)
        ) {
            Button(onClick = {
                onSave(
                    Item(
                        nombre = nombre,
                        categoria = categoria,
                        cantidad = cantidad.toIntOrNull() ?: 1
                    )
                )
            }) {
                Text("Guardar")
            }
            OutlinedButton(
                onClick = { onDissmiss() },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Cancelar")
            }
        }
    }
}
