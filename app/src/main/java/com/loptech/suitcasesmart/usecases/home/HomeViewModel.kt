package com.loptech.suitcasesmart.usecases.home

import androidx.lifecycle.ViewModel
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

    private val firebaseDatabase = FirestoreDatabase()

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
        firebaseDatabase.getMaletas(userId).addOnSuccessListener { snapshot ->
            val list = mutableListOf<Maleta>()
            for (document in snapshot) {
                val maleta = document.toObject(Maleta::class.java)
                maleta.id = document.id
                list.add(maleta)
            }
            _maletas.value = list
            _status.update { it.copy(displayProgressBar = false) }
        }
    }

    fun hideErrorDialog() {
        _status.update { it.copy(travelError = null) }
    }
}
