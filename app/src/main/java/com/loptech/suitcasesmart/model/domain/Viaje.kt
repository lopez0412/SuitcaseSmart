package com.loptech.suitcasesmart.model.domain

import com.google.firebase.firestore.Exclude

data class Maleta(
    @get:Exclude var id: String? = null,
    val nombre: String = "",
    val tipo: String = "",
    val color: String = "",
    val icono: String = ""
)

data class MaletaOut(
    val nombre: String = "",
    val tipo: String = "",
    val color: String = "",
    val icono: String = ""
)

data class Item(
    @get:Exclude var id: String? = null,
    val nombre: String = "",
    val categoria: String = "",
    val cantidad: Int = 0,
    val estado: String = "por_empacar",
    val notas: String = ""
)
