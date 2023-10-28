package com.loptech.suitcasesmart.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.loptech.suitcasesmart.model.domain.Contenido
import com.loptech.suitcasesmart.model.domain.Maleta
import com.loptech.suitcasesmart.model.domain.Viaje

class FirestoreDatabase {
    private val db = FirebaseFirestore.getInstance()

    fun agregarViaje(id:String, viaje: Viaje): Task<DocumentReference> {
        return db.collection(id).add(viaje)
    }

    fun getViajes(id:String): Task<QuerySnapshot> {
        return  db.collection(id).get()
    }

    fun agregarMaleta(idViaje: String, maleta: Maleta): Task<DocumentReference> {
        return db.collection("viajes").document(idViaje).collection("maletas").add(maleta)
    }

    fun agregarContenido(idViaje: String, idMaleta: String, contenido: Contenido): Task<DocumentReference> {
        return db.collection("viajes").document(idViaje).collection("maletas").document(idMaleta).collection("contenido").add(contenido)
    }
}