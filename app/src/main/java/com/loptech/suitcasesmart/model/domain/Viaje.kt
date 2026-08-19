package com.loptech.suitcasesmart.model.domain

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Maleta(
    @DocumentId val id: String = "",
    val nombre: String = "",
    val tipo: String = "",
    val color: String = "",
    val icono: String = "",
    @ServerTimestamp val creadaEn: Date? = null
)

data class Item(
    @DocumentId val id: String = "",
    val nombre: String = "",
    val categoria: String = "",
    val cantidad: Int = 0,
    val estado: String = "por_empacar",
    val notas: String = "",
    @ServerTimestamp val creadoEn: Date? = null
)
