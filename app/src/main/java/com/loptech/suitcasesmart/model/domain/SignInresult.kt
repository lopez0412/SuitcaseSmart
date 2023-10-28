package com.loptech.suitcasesmart.model.domain

import androidx.annotation.StringRes


data class SignInresult(
    val data: UserData?,
    @StringRes val errorMessage: Int?
)

data class  UserData(
    val userId: String?,
    val email: String?,
    val username: String?,
    val profilePictureUrl: String?
)
