package com.loptech.suitcasesmart.usecases.home

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ListenerRegistration
import com.loptech.suitcasesmart.R
import com.loptech.suitcasesmart.firebase.FirestoreDatabase
import com.loptech.suitcasesmart.model.domain.Maleta
import com.loptech.suitcasesmart.model.domain.MaletaOut
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

    override fun onCleared() {
        super.onCleared()
        maletasListener?.remove()
    }

    fun addMaleta(userId: String, maleta: MaletaOut, onSuccess: () -> Unit, onError: () -> Unit) {
        firebaseDatabase.addMaleta(userId, maleta).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _status.update {
                    it.copy(
                        createSuitcaseSuccessful = true,
                        displayProgressBar = false,
                        reload = true,
                        documentReference = task.result
                    )
                }
                onSuccess()
            } else {
                _status.update {
                    it.copy(
                        createSuitcaseSuccessful = false,
                        travelError = R.string.error_al_crear_viaje,
                        displayProgressBar = false,
                        reload = true
                    )
                }
                onError()
            }
        }
    }

    fun getMaletas(userId: String) {
        _status.update { it.copy(displayProgressBar = true) }
        maletasListener?.remove()
        maletasListener = firebaseDatabase.listenMaletas(userId) { list ->
            _maletas.value = list
            _status.update { it.copy(displayProgressBar = false) }
            loadItemCounts(userId, list)
        }
    }

    private fun loadItemCounts(userId: String, maletas: List<Maleta>) {
        val validMaletas = maletas.filter { it.id != null }
        if (validMaletas.isEmpty()) return

        val newCounts = mutableMapOf<String, Pair<Int, Int>>()
        var completed = 0

        for (maleta in validMaletas) {
            val id = maleta.id!!
            firebaseDatabase.getItems(userId, id).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val snapshot = task.result
                    val total = snapshot.size()
                    val empacados = snapshot.documents.count { doc ->
                        val estado = doc.getString("estado")
                        estado == "empacado" || estado == "usado"
                    }
                    newCounts[id] = Pair(empacados, total)
                }
                completed++
                if (completed == validMaletas.size) {
                    _progreso.value = newCounts.toMap()
                }
            }
        }
    }

    fun updateMaleta(userId: String, maletaId: String, maleta: MaletaOut, onSuccess: () -> Unit, onError: () -> Unit) {
        _maletas.update { list ->
            list.map { m ->
                if (m.id == maletaId) m.copy(nombre = maleta.nombre, tipo = maleta.tipo, color = maleta.color, icono = maleta.icono)
                else m
            }
        }
        firebaseDatabase.updateMaleta(userId, maletaId, maleta).addOnCompleteListener { task ->
            if (task.isSuccessful) onSuccess() else onError()
        }
    }

    fun deleteMaleta(userId: String, maletaId: String) {
        _maletas.update { it.filter { maleta -> maleta.id != maletaId } }
        _progreso.update { it - maletaId }
        firebaseDatabase.deleteMaleta(userId, maletaId)
    }

    fun hideErrorDialog() {
        _status.update { it.copy(travelError = null) }
    }
}
