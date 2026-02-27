package com.loptech.suitcasesmart.usecases.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.loptech.suitcasesmart.model.domain.UserData
import java.lang.RuntimeException

var showErrorButton: Boolean = false

@Composable
fun ProfileScreen(
    userData: UserData,
    onsignOut: () -> Unit
){
    //MARK: - Properties

    Firebase.remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
        if (task.isSuccessful){
            showErrorButton = Firebase.remoteConfig.getBoolean("show_error_btn")
        }
    }
    //MARK: Column container..
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (userData.profilePictureUrl != null){
            AsyncImage(
                model = userData.profilePictureUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )//:AsyncImage
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        if (userData.username != null){
            Text(text = userData.username)
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (userData.email != null){
            Text(text = userData.email)
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(onClick = onsignOut) {
            Text("Sign Out")
        }

        if (showErrorButton) {
            Button(onClick = {
                throw RuntimeException("Forzando el error")
            }) {
                Text("Crash")
            }
        }

    }
}