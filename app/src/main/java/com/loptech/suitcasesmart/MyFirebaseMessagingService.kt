package com.loptech.suitcasesmart

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        // Post to the main thread using the main looper's handler
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(baseContext, "Mensaje Recibido: $message", Toast.LENGTH_LONG).show()
        }
    }
}