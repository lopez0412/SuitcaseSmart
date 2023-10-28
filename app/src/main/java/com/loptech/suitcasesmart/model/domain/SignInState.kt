package com.loptech.suitcasesmart.model.domain

import androidx.annotation.StringRes
import com.loptech.suitcasesmart.usecases.home.ProviderType


data class SignInState(
    val isSignInSuccessful: Boolean = false,
    @StringRes val signInError: Int? = null,
    val displayProgressBar: Boolean = false,
    val providerType: ProviderType = ProviderType.BASIC
)
