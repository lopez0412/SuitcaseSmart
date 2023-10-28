package com.loptech.suitcasesmart.model.domain

data class Viaje(
    var destino: String = "",
    var fechaPartida: String = "",
    var fechaRegreso: String = "",
    var notas: String = ""
)
data class Maleta(
    val nombre: String = "",
    val notas: String = ""
)

data class Contenido(
    val nombre: String = "",
    val cantidad: Int = 0,
    val categoria: String = ""
)
