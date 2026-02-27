package com.loptech.suitcasesmart.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.loptech.suitcasesmart.model.domain.Item
import com.loptech.suitcasesmart.model.domain.Maleta
import com.loptech.suitcasesmart.model.domain.MaletaOut

class FirestoreDatabase {
    private val db = FirebaseFirestore.getInstance()

    fun getMaletas(userId: String): Task<QuerySnapshot> {
        return db.collection("users").document(userId).collection("maletas").get()
    }

    fun listenMaletas(userId: String, onUpdate: (List<Maleta>) -> Unit): ListenerRegistration {
        return db.collection("users").document(userId).collection("maletas")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot == null) return@addSnapshotListener
                val list = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Maleta::class.java)?.also { it.id = doc.id }
                }
                onUpdate(list)
            }
    }

    fun listenItems(userId: String, maletaId: String, onUpdate: (List<Item>) -> Unit): ListenerRegistration {
        return db.collection("users").document(userId)
            .collection("maletas").document(maletaId)
            .collection("items")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot == null) return@addSnapshotListener
                val list = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Item::class.java)?.also { it.id = doc.id }
                }
                onUpdate(list)
            }
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

    fun deleteMaleta(userId: String, maletaId: String): Task<Void> {
        return db.collection("users").document(userId)
            .collection("maletas").document(maletaId)
            .delete()
    }

    fun updateMaleta(userId: String, maletaId: String, maleta: MaletaOut): Task<Void> {
        return db.collection("users").document(userId)
            .collection("maletas").document(maletaId)
            .update(
                mapOf(
                    "nombre" to maleta.nombre,
                    "tipo" to maleta.tipo,
                    "color" to maleta.color,
                    "icono" to maleta.icono
                )
            )
    }

    fun updateItem(userId: String, maletaId: String, itemId: String, item: Item): Task<Void> {
        return db.collection("users").document(userId)
            .collection("maletas").document(maletaId)
            .collection("items").document(itemId)
            .update(
                mapOf(
                    "nombre" to item.nombre,
                    "categoria" to item.categoria,
                    "cantidad" to item.cantidad
                )
            )
    }

    fun deleteItem(userId: String, maletaId: String, itemId: String): Task<Void> {
        return db.collection("users").document(userId)
            .collection("maletas").document(maletaId)
            .collection("items").document(itemId)
            .delete()
    }
}
