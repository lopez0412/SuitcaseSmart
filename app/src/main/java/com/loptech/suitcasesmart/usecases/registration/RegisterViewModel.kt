package com.loptech.suitcasesmart.usecases.registration

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.loptech.suitcasesmart.R
import com.loptech.suitcasesmart.model.domain.RegisterState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel: ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state = _state.asStateFlow()

    fun register(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        val errorMessage = if(name.isBlank() ||  email.isBlank() || password.isBlank() || confirmPassword.isBlank()){
            R.string.error_input_empty
        } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            R.string.error_not_a_valid_email
        } else if(password != confirmPassword) {
            R.string.error_incorrectly_repeated_password
        } else null

        errorMessage?.let {
            _state.update {
                it.copy(errorMessage = errorMessage)
            }
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(displayProgressBar = true)
            }

            val auth = Firebase.auth
            auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener{ task ->
                    if (task.isSuccessful){
                        _state.update {
                            it.copy(displayProgressBar = false,
                                successRegister = true)
                        }
                    }else{
                        _state.update {
                            it.copy(errorMessage = R.string.register_error_message,
                                displayProgressBar = false)
                        }
                    }

                }

            _state.update {
                it.copy(displayProgressBar = false)
            }
        }
    }

    fun hideErrorDialog() {
        _state.update {
            it.copy(errorMessage = null)
        }
    }

}