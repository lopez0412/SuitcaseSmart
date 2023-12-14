package com.loptech.suitcasesmart.usecases.common.views

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.loptech.suitcasesmart.model.domain.Viaje
import com.loptech.suitcasesmart.usecases.common.controls.DatePickerCustom



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddTravelSheetForm(
    onSave: (Viaje) -> Unit,
    onDissmiss: () -> Unit
){
    //MARK: Properties
    var destino by remember { mutableStateOf(String()) }
    var fechaPartida by remember { mutableStateOf(String()) }
    var fechaRetorno by remember { mutableStateOf(String()) }
    var notas by remember { mutableStateOf(String()) }

    var showDate by remember { mutableStateOf(false) }
    var showDateRetorno by remember { mutableStateOf(false) }

    val viajeToAdd by remember { mutableStateOf(Viaje()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Agregar Nuevo Viaje",
            modifier = Modifier
                .wrapContentSize(Alignment.Center),
            textAlign = TextAlign.Center,
            color = Color.Black
        )
        OutlinedTextField(
            value = destino,
            onValueChange = { destino = it },
            label = { Text("Destino") }
        )

        //Input for Fecha Partida
        OutlinedTextField(
            value = fechaPartida,
            onValueChange = { fechaPartida = it },
            enabled = false,
            label = {
                Text(text = "Fecha Partida")
                    },
            trailingIcon = {
                IconButton(onClick = { showDate = true }) {
                    Icon(imageVector = Icons.Rounded.DateRange, contentDescription = "Calendario")
                }
            }
        )
        // Show Date picker
        if (showDate){
            DatePickerCustom(onDiss = { showDate = false }, onSelectDate = { fechaPartida = it})
        }
        //:end Input for Fecha Partida

        //Input fecha Regreso
        OutlinedTextField(
            value = fechaRetorno,
            onValueChange = { fechaRetorno = it },
            enabled = false,
            label = {
                Text(text = "Fecha Regreso")
            },
            trailingIcon = {
                IconButton(onClick = { showDateRetorno = true }) {
                    Icon(imageVector = Icons.Rounded.DateRange, contentDescription = "Calendario")
                }
            }
        )
        // Show Date picker
        if (showDateRetorno){
            DatePickerCustom(onDiss = { showDateRetorno = false }, onSelectDate = { fechaRetorno = it})
        }
        //:end Input Fecha Retorno

        OutlinedTextField(
            value = notas,
            onValueChange = { notas = it },
            label = { Text("Notas") }
        )

        //Buttons Row
        Row(
            modifier = Modifier.padding(vertical = 8.dp),
        ) {
            Button(onClick = {
                viajeToAdd.destino = destino
                viajeToAdd.fechaPartida = fechaPartida
                viajeToAdd.fechaRegreso = fechaRetorno
                viajeToAdd.notas = notas

                onSave(viajeToAdd)
            }, modifier = Modifier.padding()) {
                Text("Guardar")
            }//:Button Guardar

            //Button Cerrar
            OutlinedButton(onClick = { onDissmiss() }, modifier = Modifier.padding()) {
                Text(text = "Cancelar")
            }

        }//:Row

    }
}