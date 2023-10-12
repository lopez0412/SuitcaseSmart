package com.loptech.suitcasesmart.firebase

import android.content.Context
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.loptech.suitcasesmart.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class SignInviewModel: ViewModel(){

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()




    fun onSignInresult(result: SignInresult){
        _state.update { it.copy(
            isSignInSuccessful = result.data != null,
            signInError = result.errorMessage
        ) }
    }

    fun resetState(){
        _state.update {
            SignInState()
        }
    }

    fun login(email: String, password: String) {

        val errorMessage = if(email.isBlank() || password.isBlank()) {
            R.string.error_input_empty
        } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            R.string.error_not_a_valid_email
        } else null

        errorMessage?.let {
            _state.update {
                it.copy(signInError = errorMessage)
            }
            return
        }
        // launch coroutine with signin method for Email password auth.
        viewModelScope.launch {
            _state.update {
                it.copy(displayProgressBar = true)
            }

            val auth = Firebase.auth
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _state.update {
                            it.copy(
                                displayProgressBar = false,
                                isSignInSuccessful = true)
                        }
                    } else {
                        _state.update {
                            it.copy(
                                signInError = R.string.error_user_signin,
                                displayProgressBar = false
                            )
                        }
                    }
                }
        }

    }

    fun hideErrorDialog() {
        _state.update {
            it.copy(signInError = null)
        }
    }
}