package com.loptech.suitcasesmart.usecases.common.rows

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loptech.suitcasesmart.R
import com.loptech.suitcasesmart.model.domain.Viaje
import com.loptech.suitcasesmart.ui.theme.MainColorBlack
import com.loptech.suitcasesmart.ui.theme.MainGrey
import com.loptech.suitcasesmart.ui.theme.MainGrey20

@Composable
fun TravelRow(
    viaje: Viaje,
    onClick: () -> Unit
){
    Card(colors = CardDefaults.cardColors(
        containerColor = MainGrey20
    ),
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .padding(horizontal = 10.dp, vertical = 5.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(modifier = Modifier
                .width(64.dp)
                .height(64.dp)
                .padding(8.dp),
                painter = painterResource(id = R.drawable.avion),
                contentDescription = "Lugar")
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Text(
                    text = viaje.destino,
                    fontSize = 24.sp
                )
                Text(text = "Fecha: ${viaje.fechaPartida}")
            }

        }
    }
}

@Preview
@Composable
fun TravelRowPreview(){
    TravelRow(Viaje("Buenos Aires, Argentina"), onClick = {/* TODO */})
}