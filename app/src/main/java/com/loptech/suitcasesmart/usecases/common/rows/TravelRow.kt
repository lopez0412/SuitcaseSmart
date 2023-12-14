package com.loptech.suitcasesmart.usecases.common.rows

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
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
    Column(modifier = Modifier.padding(5.dp).clickable {
        onClick.invoke()
    }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.avion),
                contentDescription = "Artist image"
            )
            Spacer(modifier = Modifier.size(15.dp))
            Column {
                Text(fontFamily = FontFamily.SansSerif, text = viaje.destino)
                Text(fontSize = 12.sp, text = "Fecha ${viaje.fechaPartida}")
            }
            Spacer(Modifier.weight(1f))
            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = "Move Forward")
        }
        HorizontalDivider()
    }
}

@Preview
@Composable
fun TravelRowPreview(){
    TravelRow(Viaje("Buenos Aires, Argentina"), onClick = {/* TODO */})
}