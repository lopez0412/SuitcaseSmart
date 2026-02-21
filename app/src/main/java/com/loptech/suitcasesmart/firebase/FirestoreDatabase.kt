package com.loptech.suitcasesmart.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.loptech.suitcasesmart.model.domain.Item
import com.loptech.suitcasesmart.model.domain.Maleta
import com.loptech.suitcasesmart.model.domain.MaletaOut

class FirestoreDatabase {
    private val db = FirebaseFirestore.getInstance()

    fun getMaletas(userId: String): Task<QuerySnapshot> {
        return db.collection("users").document(userId).collection("maletas").get()
    }

    fun addMaleta(userId: String, maleta: MaletaOut): Task<DocumentReference> {
        return db.collection("users").document(userId).collection("maletas").add(maleta)
    }

    fun getMaletaById(userId: String, maletaId: String): DocumentReference {
        return db.collection("users").document(userId).collection("maletas").document(maletaId)
    }

    fun addItem(userId: String, maletaId: String, item: Item): Task<DocumentReference> {
        return db.collection("users").document(userId)
            .collection("maletas").document(maletaId)
            .collection("items").add(item)
    }

    fun getItems(userId: String, maletaId: String): Task<QuerySnapshot> {
        return db.collection("users").document(userId)
            .collection("maletas").document(maletaId)
            .collection("items").get()
    }

    fun updateItemEstado(userId: String, maletaId: String, itemId: String, estado: String): Task<Void> {
        return db.collection("users").document(userId)
            .collection("maletas").document(maletaId)
            .collection("items").document(itemId)
            .update("estado", estado)
    }
}
