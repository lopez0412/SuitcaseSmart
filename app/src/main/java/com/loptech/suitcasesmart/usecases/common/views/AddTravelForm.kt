package com.loptech.suitcasesmart.usecases.common.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.loptech.suitcasesmart.model.domain.Viaje
import com.loptech.suitcasesmart.ui.theme.MainGrey
import com.loptech.suitcasesmart.ui.theme.MainGrey20
import com.loptech.suitcasesmart.usecases.common.controls.DatePickerCustom
import kotlin.reflect.KFunction2

@Composable
fun AddTravelForm(
    onSave: (Viaje) -> Unit,
    onDissmiss: () -> Unit
){
    //MARK: Properties
    var destino by remember { mutableStateOf(String()) }
    var fechaPartida by remember { mutableStateOf(String()) }
    var fechaRetorno by remember { mutableStateOf(String()) }
    var notas by remember { mutableStateOf(String()) }

    var showDate by remember { mutableStateOf(Boolean) }

    val viajeToAdd by remember { mutableStateOf(Viaje()) }

    //MARK: Body
   Dialog(onDismissRequest = { onDissmiss.invoke() }) {
       Card(
           colors = CardColors(
               containerColor = Color.White,
               contentColor = Color.White,
               disabledContainerColor = Color.Gray,
               disabledContentColor = Color.Gray
           ),
           modifier = Modifier
               .fillMaxWidth()
               .padding(16.dp),
           shape = RoundedCornerShape(16.dp),
       ) {
           Column(
               modifier = Modifier
                   .fillMaxWidth()
                   .padding(16.dp)
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
   }
}