package com.loptech.suitcasesmart.usecases.home

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ListenerRegistration
import com.loptech.suitcasesmart.R
import com.loptech.suitcasesmart.firebase.FirestoreDatabase
import com.loptech.suitcasesmart.model.domain.Maleta
import com.loptech.suitcasesmart.model.domain.StatusDatosMaletas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel : ViewModel() {

    private val _status = MutableStateFlow(StatusDatosMaletas())
    val status = _status.asStateFlow()

    private val _maletas = MutableStateFlow<List<Maleta>>(emptyList())
    val maletas = _maletas.asStateFlow()

    private val _progreso = MutableStateFlow<Map<String, Pair<Int, Int>>>(emptyMap())
    val progreso = _progreso.asStateFlow()

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
        }

        // Add listeners for new maletas (skip those already subscribed)
        maletas.filter { it.id.isNotEmpty() && it.id !in itemListeners }.forEach { maleta ->
            itemListeners[maleta.id] = firebaseDatabase.listenItems(userId, maleta.id) { items ->
                val total = items.size
                val empacados = items.count { it.estado == "empacado" || it.estado == "usado" }
                _progreso.update { it + (maleta.id to Pair(empacados, total)) }
            }
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
        itemListeners.remove(maletaId)?.remove()
        firebaseDatabase.deleteAllItemsAndMaleta(userId, maletaId)
    }

    fun hideErrorDialog() {
        _status.update { it.copy(travelError = null) }
    }
}
