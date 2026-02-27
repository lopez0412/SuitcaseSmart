package com.loptech.suitcasesmart.usecases.TravelDetail

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ListenerRegistration
import com.loptech.suitcasesmart.firebase.FirestoreDatabase
import com.loptech.suitcasesmart.model.domain.Item
import com.loptech.suitcasesmart.model.domain.Maleta
import com.loptech.suitcasesmart.model.domain.StatusDatosMaletas
import com.loptech.suitcasesmart.usecases.common.nextEstado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TravelDetailViewModel : ViewModel() {

    private val _status = MutableStateFlow(StatusDatosMaletas())
    val status = _status.asStateFlow()

    private val _maleta = MutableStateFlow(Maleta())
    val maleta = _maleta.asStateFlow()

    private val _items = MutableStateFlow<List<Item>>(emptyList())
    val items = _items.asStateFlow()

    private val firebaseDatabase = FirestoreDatabase()
    private var maletaListener: ListenerRegistration? = null
    private var itemsListener: ListenerRegistration? = null

    override fun onCleared() {
        super.onCleared()
        maletaListener?.remove()
        itemsListener?.remove()
    }

    fun hideErrorDialog() {
        _status.update { it.copy(travelError = null) }
    }

    fun getMaleta(userId: String, maletaId: String) {
        maletaListener?.remove()
        maletaListener = firebaseDatabase.getMaletaById(userId, maletaId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    val maleta = snapshot.toObject(Maleta::class.java)!!
                    maleta.id = snapshot.id
                    _maleta.value = maleta
                }
            }
    }

    fun getItems(userId: String, maletaId: String) {
        itemsListener?.remove()
        itemsListener = firebaseDatabase.listenItems(userId, maletaId) { list ->
            _items.value = list
        }
    }

    fun addItem(userId: String, maletaId: String, item: Item, onSuccess: () -> Unit, onError: () -> Unit) {
        firebaseDatabase.addItem(userId, maletaId, item).addOnCompleteListener { task ->
            if (task.isSuccessful) onSuccess() else onError()
        }
    }

    fun updateEstado(userId: String, maletaId: String, item: Item) {
        val nuevoEstado = nextEstado(item.estado)
        _items.update { list ->
            list.map { if (it.id == item.id) it.copy(estado = nuevoEstado) else it }
        }
        item.id?.let { itemId ->
            firebaseDatabase.updateItemEstado(userId, maletaId, itemId, nuevoEstado)
        }
    }

    fun updateItem(userId: String, maletaId: String, item: Item) {
        _items.update { list -> list.map { if (it.id == item.id) item else it } }
        item.id?.let { itemId ->
            firebaseDatabase.updateItem(userId, maletaId, itemId, item)
        }
    }

    fun deleteItem(userId: String, maletaId: String, item: Item) {
        _items.update { list -> list.filter { it.id != item.id } }
        item.id?.let { itemId ->
            firebaseDatabase.deleteItem(userId, maletaId, itemId)
        }
    }
}
