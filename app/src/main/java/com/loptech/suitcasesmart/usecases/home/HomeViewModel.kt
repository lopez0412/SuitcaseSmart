package com.loptech.suitcasesmart.usecases.home

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.loptech.suitcasesmart.R
import com.loptech.suitcasesmart.firebase.FirestoreDatabase
import com.loptech.suitcasesmart.model.domain.SignInState
import com.loptech.suitcasesmart.model.domain.StatusDatosViajes
import com.loptech.suitcasesmart.model.domain.Viaje
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel: ViewModel(){

    private val _status = MutableStateFlow(StatusDatosViajes())
    val status = _status.asStateFlow()

    private val _viajes = mutableListOf<Viaje>()
    val viajes = _viajes

    private val firebaseDatabase = FirestoreDatabase()
    //Function to add Travel Record.
    fun addviaje(id:String, viaje: Viaje){
        firebaseDatabase.agregarViaje(id,viaje).addOnCompleteListener {task ->
            if (task.isSuccessful){
                _status.update {
                    it.copy(
                        createTravelSuccessful = true,
                        displayProgressBar = false,
                        reload = true,
                        documentReference = task.result
                    )
                }
            }else{
                _status.update {
                    it.copy(
                        createTravelSuccessful = false,
                        travelError = R.string.error_al_crear_viaje,
                        displayProgressBar = false,
                        reload = true
                    )
                }
            }
        }
    }//:Add travel Record

    //Function to get Travel Records from user Id
    fun getViajes(id:String){

        _status.update {
            it.copy(
                displayProgressBar = true
            )
        }


        firebaseDatabase.getViajes(id).addOnSuccessListener { viajesList ->
            for (document in viajesList){
                val documentId = document.id
                val viaje = document.toObject(Viaje::class.java)
                _viajes.add(viaje)
            }

            _status.update {
                it.copy(
                    displayProgressBar = false
                )
            }
        }


    }

    fun hideErrorDialog() {
        _status.update {
            it.copy(travelError = null)
        }
    }

}