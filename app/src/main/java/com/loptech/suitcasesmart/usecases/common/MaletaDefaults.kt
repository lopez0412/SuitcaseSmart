package com.loptech.suitcasesmart.usecases.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backpack
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.loptech.suitcasesmart.ui.theme.AviationNavy
import com.loptech.suitcasesmart.ui.theme.DarkTeal20
import com.loptech.suitcasesmart.ui.theme.MainAccent
import com.loptech.suitcasesmart.ui.theme.MainDarkColor

fun estadoLabel(estado: String): String = when (estado) {
    "por_empacar" -> "Por empacar"
    "empacado"    -> "Empacado"
    "usado"       -> "Usado"
    else          -> estado
}

fun estadoColor(estado: String): Color = when (estado) {
    "por_empacar" -> Color(0xFFE67E22)
    "empacado"    -> Color(0xFF27AE60)
    "usado"       -> Color(0xFF7F8C8D)
    else          -> Color.Gray
}

fun nextEstado(estado: String): String = when (estado) {
    "por_empacar" -> "empacado"
    "empacado"    -> "usado"
    else          -> "por_empacar"
}

data class TipoVisual(val icon: ImageVector, val color: Color)

/** Mismos 6 colores que iOS, en el mismo orden */
val MALETA_COLOR_OPTIONS: List<Pair<String, String>> = listOf(
    "Azul"    to "#4A90E2",
    "Verde"   to "#27AE60",
    "Rojo"    to "#E74C3C",
    "Naranja" to "#E67E22",
    "Morado"  to "#8E44AD",
    "Gris"    to "#7F8C8D"
)

fun hexToColor(hex: String): Color = try {
    Color(android.graphics.Color.parseColor(hex))
} catch (e: Exception) {
    Color.Gray
}

/** Nombre del icono que iOS guarda en Firestore (SF Symbol name) */
fun iconoForTipo(tipo: String): String = when (tipo) {
    "carry-on" -> "suitcase.rolling"
    "grande"   -> "suitcase"
    "mochila"  -> "backpack"
    "personal" -> "bag"
    else       -> "suitcase.rolling"
}

fun maletaVisualForTipo(tipo: String): TipoVisual = when (tipo) {
    "carry-on" -> TipoVisual(Icons.Filled.Luggage,     AviationNavy)
    "grande"   -> TipoVisual(Icons.Filled.Work,        MainDarkColor)
    "mochila"  -> TipoVisual(Icons.Filled.Backpack,    MainAccent)
    "personal" -> TipoVisual(Icons.Filled.ShoppingBag, DarkTeal20)
    else       -> TipoVisual(Icons.Filled.Luggage,     AviationNavy)
}
