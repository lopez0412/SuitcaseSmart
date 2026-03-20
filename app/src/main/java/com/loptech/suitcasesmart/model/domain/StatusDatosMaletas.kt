package com.loptech.suitcasesmart.model.domain

import androidx.annotation.StringRes

data class StatusDatosMaletas(
    @StringRes val travelError: Int? = null,
    val displayProgressBar: Boolean = false,
)
