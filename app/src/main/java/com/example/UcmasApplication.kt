package com.example

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class UcmasApplication : Application() {
    var firestore: FirebaseFirestore? = null
        private set

    override fun onCreate() {
        super.onCreate()
        try {
            FirebaseApp.initializeApp(this)
            val databaseId = getString(R.string.firestore_database_id)
            firestore = FirebaseFirestore.getInstance(databaseId)
            val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()
            firestore?.firestoreSettings = settings
            Log.i("UcmasApplication", "Firebase Firestore initialized with database: $databaseId")
        } catch (e: Exception) {
            Log.e("UcmasApplication", "Failed to initialize Firestore: ${e.message}", e)
        }
    }
}
