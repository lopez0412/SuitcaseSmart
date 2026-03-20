package com.loptech.suitcasesmart.usecases.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.loptech.suitcasesmart.R
import com.loptech.suitcasesmart.model.domain.SignInState
import com.loptech.suitcasesmart.model.domain.SignInresult
import com.loptech.suitcasesmart.usecases.home.ProviderType
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
            signInError = result.errorMessage,
            providerType = ProviderType.GOOGLE
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
                                isSignInSuccessful = true,
                                providerType = ProviderType.BASIC)
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
