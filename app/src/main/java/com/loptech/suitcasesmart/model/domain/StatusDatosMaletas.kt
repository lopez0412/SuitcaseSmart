package com.loptech.suitcasesmart.model.domain

import androidx.annotation.StringRes
import com.google.firebase.firestore.DocumentReference

data class StatusDatosMaletas(
    val createSuitcaseSuccessful: Boolean = false,
    @StringRes val travelError: Int? = null,
    val displayProgressBar: Boolean = false,
    val reload: Boolean = false,
    val documentReference: DocumentReference? = null
)