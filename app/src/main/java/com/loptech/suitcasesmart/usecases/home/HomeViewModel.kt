package com.loptech.suitcasesmart.usecases.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import com.loptech.suitcasesmart.R
import com.loptech.suitcasesmart.firebase.FirestoreDatabase
import com.loptech.suitcasesmart.model.domain.Item
import com.loptech.suitcasesmart.model.domain.Maleta
import com.loptech.suitcasesmart.model.domain.StatusDatosMaletas
import com.loptech.suitcasesmart.usecases.common.nextEstado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class HomeViewModel : ViewModel() {

    private val _status = MutableStateFlow(StatusDatosMaletas())
    val status = _status.asStateFlow()

    private val _maletas = MutableStateFlow<List<Maleta>>(emptyList())
    val maletas = _maletas.asStateFlow()

    private val _progreso = MutableStateFlow<Map<String, Pair<Int, Int>>>(emptyMap())
    val progreso = _progreso.asStateFlow()

    private val _allItems = MutableStateFlow<Map<String, List<Item>>>(emptyMap())

    // All items with estado "por_empacar", paired with their maleta — used by ChecklistScreen
    val pendingItems = combine(_maletas, _allItems) { maletas, allItems ->
        maletas.flatMap { maleta ->
            (allItems[maleta.id] ?: emptyList())
                .filter { it.estado == "por_empacar" }
                .map { maleta to it }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val firebaseDatabase = FirestoreDatabase()
    private var maletasListener: ListenerRegistration? = null
    private val itemListeners = mutableMapOf<String, ListenerRegistration>()

    override fun onCleared() {
        super.onCleared()
        maletasListener?.remove()
        itemListeners.values.forEach { it.remove() }
    }

    fun getMaletas(userId: String) {
        if (_maletas.value.isEmpty()) {
            _status.update { it.copy(displayProgressBar = true) }
        }
        maletasListener?.remove()
        maletasListener = firebaseDatabase.listenMaletas(userId) { list ->
            _maletas.value = list
            _status.update { it.copy(displayProgressBar = false) }
            listenItemCounts(userId, list)
        }
    }

    private fun listenItemCounts(userId: String, maletas: List<Maleta>) {
        val currentIds = maletas.map { it.id }.filter { it.isNotEmpty() }.toSet()

        // Remove listeners for deleted maletas
        itemListeners.keys.filter { it !in currentIds }.forEach { id ->
            itemListeners.remove(id)?.remove()
            _progreso.update { it - id }
            _allItems.update { it - id }
        }

        // Add listeners for new maletas (skip those already subscribed)
        maletas.filter { it.id.isNotEmpty() && it.id !in itemListeners }.forEach { maleta ->
            itemListeners[maleta.id] = firebaseDatabase.listenItems(userId, maleta.id) { items ->
                val total = items.size
                val empacados = items.count { it.estado == "empacado" || it.estado == "usado" }
                _progreso.update { it + (maleta.id to Pair(empacados, total)) }
                _allItems.update { it + (maleta.id to items) }
            }
        }
    }

    fun updateEstado(userId: String, maletaId: String, item: Item) {
        val nuevoEstado = nextEstado(item.estado)
        _allItems.update { map ->
            val updatedItems = (map[maletaId] ?: emptyList()).map {
                if (it.id == item.id) it.copy(estado = nuevoEstado) else it
            }
            map + (maletaId to updatedItems)
        }
        if (item.id.isNotEmpty()) {
            firebaseDatabase.updateItemEstado(userId, maletaId, item.id, nuevoEstado)
        }
    }

    fun addMaleta(userId: String, maleta: Maleta, onSuccess: () -> Unit, onError: () -> Unit) {
        firebaseDatabase.addMaleta(userId, maleta).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _status.update { it.copy(displayProgressBar = false) }
                onSuccess()
            } else {
                _status.update {
                    it.copy(travelError = R.string.error_al_crear_viaje, displayProgressBar = false)
                }
                onError()
            }
        }
    }

    fun updateMaleta(userId: String, maletaId: String, maleta: Maleta, onSuccess: () -> Unit, onError: () -> Unit) {
        _maletas.update { list ->
            list.map { m -> if (m.id == maletaId) maleta.copy(id = maletaId) else m }
        }
        firebaseDatabase.updateMaleta(userId, maletaId, maleta).addOnCompleteListener { task ->
            if (task.isSuccessful) onSuccess() else onError()
        }
    }

    fun deleteMaleta(userId: String, maletaId: String) {
        _maletas.update { it.filter { maleta -> maleta.id != maletaId } }
        _progreso.update { it - maletaId }
        _allItems.update { it - maletaId }
        itemListeners.remove(maletaId)?.remove()
        firebaseDatabase.deleteAllItemsAndMaleta(userId, maletaId)
    }

    fun hideErrorDialog() {
        _status.update { it.copy(travelError = null) }
    }
}
