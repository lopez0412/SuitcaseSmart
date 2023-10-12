package com.loptech.suitcasesmart.firebase

import androidx.annotation.StringRes


data class SignInState(
    val isSignInSuccessful: Boolean = false,
    @StringRes val signInError: Int? = null,
    val displayProgressBar: Boolean = false,
)
