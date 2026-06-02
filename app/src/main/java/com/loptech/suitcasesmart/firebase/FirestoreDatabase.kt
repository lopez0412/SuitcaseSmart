package com.loptech.suitcasesmart.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.loptech.suitcasesmart.model.domain.Item
import com.loptech.suitcasesmart.model.domain.Maleta

class FirestoreDatabase {
    private val db = FirebaseFirestore.getInstance()

    private fun userMaletas(userId: String): CollectionReference =
        db.collection("users").document(userId).collection("maletas")

    private fun maletaItems(userId: String, maletaId: String): CollectionReference =
        userMaletas(userId).document(maletaId).collection("items")

    fun listenMaletas(
        userId: String,
        onUpdate: (List<Maleta>) -> Unit,
        onError: (Exception) -> Unit = {}
    ): ListenerRegistration {
        return userMaletas(userId).addSnapshotListener { snapshot, error ->
            if (error != null) { onError(error); return@addSnapshotListener }
            if (snapshot == null) return@addSnapshotListener
            onUpdate(snapshot.documents.mapNotNull { it.toObject(Maleta::class.java) })
        }
    }

    fun listenItems(
        userId: String,
        maletaId: String,
        onUpdate: (List<Item>) -> Unit,
        onError: (Exception) -> Unit = {}
    ): ListenerRegistration {
        return maletaItems(userId, maletaId).addSnapshotListener { snapshot, error ->
            if (error != null) { onError(error); return@addSnapshotListener }
            if (snapshot == null) return@addSnapshotListener
            onUpdate(snapshot.documents.mapNotNull { it.toObject(Item::class.java) })
        }
    }

    fun getMaletaById(userId: String, maletaId: String): DocumentReference =
        userMaletas(userId).document(maletaId)

    fun addMaleta(userId: String, maleta: Maleta): Task<DocumentReference> =
        userMaletas(userId).add(maleta)

    fun updateMaleta(userId: String, maletaId: String, maleta: Maleta): Task<Void> =
        userMaletas(userId).document(maletaId).update(
            mapOf(
                "nombre" to maleta.nombre,
                "tipo" to maleta.tipo,
                "color" to maleta.color,
                "icono" to maleta.icono
            )
        )

    fun deleteAllItemsAndMaleta(
        userId: String,
        maletaId: String,
        onSuccess: () -> Unit = {},
        onError: () -> Unit = {}
    ) {
        val maletaRef = userMaletas(userId).document(maletaId)
        maletaItems(userId, maletaId).get()
            .addOnSuccessListener { snapshot ->
                val batch = db.batch()
                snapshot.documents.forEach { batch.delete(it.reference) }
                batch.delete(maletaRef)
                batch.commit()
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onError() }
            }
            .addOnFailureListener { onError() }
    }

    fun addItem(userId: String, maletaId: String, item: Item): Task<DocumentReference> =
        maletaItems(userId, maletaId).add(item)

    fun updateItemEstado(userId: String, maletaId: String, itemId: String, estado: String): Task<Void> =
        maletaItems(userId, maletaId).document(itemId).update("estado", estado)

    fun updateItem(userId: String, maletaId: String, itemId: String, item: Item): Task<Void> =
        maletaItems(userId, maletaId).document(itemId).update(
            mapOf(
                "nombre" to item.nombre,
                "categoria" to item.categoria,
                "cantidad" to item.cantidad
            )
        )

    fun deleteItem(userId: String, maletaId: String, itemId: String): Task<Void> =
        maletaItems(userId, maletaId).document(itemId).delete()
}
