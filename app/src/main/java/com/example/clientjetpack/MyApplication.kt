package com.example.clientjetpack

import Application2.App.App.appModule_App2_ac_app1
import EntreApps.Shared.Models.Components.AppType
import EntreApps.Shared.Models.M18CentralParametresOfAllApps
import Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin.appModule
import Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin.modulesDonLesDeuAppNeceFemrePas
import android.Manifest
import android.app.Application
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.annotation.RequiresPermission
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin


class MyApplication : Application() {
    companion object {
        private const val TAG = "MyApplication"
        private const val CACHE_SIZE_MB = 100L
        private const val CACHE_SIZE_BYTES = CACHE_SIZE_MB * 1024L * 1024L
    }

    override fun onCreate() {
        super.onCreate()

        Log.d(TAG, "Application starting...")

        initializeFirebase()

        initializeKoin()

        Log.d(TAG, "Application initialized successfully")
    }

    private fun initializeFirebase() {
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
                Log.d(TAG, "✓ Firebase App initialized")
            }

            configureFirestoreOfflineFirst()

            configureRealtimeDatabase()

            if (isNetworkAvailable()) {
                enableFirestoreNetwork()
            } else {
                Log.w(TAG, "⚠ No network - Firebase will work in offline mode")
            }

        } catch (e: Exception) {
            Log.e(TAG, "❌ Firebase initialization error: ${e.message}", e)
        }
    }

    private fun configureFirestoreOfflineFirst() {
        try {
            val firestore = FirebaseFirestore.getInstance()

            // Configuration pour mode offline-first
            val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .setCacheSizeBytes(CACHE_SIZE_BYTES)
                .build()

            firestore.firestoreSettings = settings

            firestore.disableNetwork()
                .addOnSuccessListener {
                    Log.d(TAG, "✓ Firestore configured in offline mode (${CACHE_SIZE_MB}MB cache)")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "⚠ Could not disable Firestore network: ${e.message}")
                }

        } catch (e: Exception) {
            Log.e(TAG, "❌ Firestore configuration error: ${e.message}", e)
        }
    }

    /**
     * Active le réseau Firestore uniquement si Internet est disponible
     */
    private fun enableFirestoreNetwork() {
        try {
            val firestore = FirebaseFirestore.getInstance()

            // Attendre un peu avant d'activer le réseau
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                firestore.enableNetwork()
                    .addOnSuccessListener {
                        Log.d(TAG, "✓ Firestore network enabled")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "⚠ Could not enable Firestore network: ${e.message}")
                    }
            }, 1000) // Délai de 1 seconde

        } catch (e: Exception) {
            Log.w(TAG, "⚠ Network activation error: ${e.message}")
        }
    }

    /**
     * Configure Firebase Realtime Database avec persistence
     */
    private fun configureRealtimeDatabase() {
        try {
            FirebaseDatabase.getInstance().apply {
                setPersistenceEnabled(true)
                setPersistenceCacheSizeBytes(CACHE_SIZE_BYTES)
            }
            Log.d(TAG, "✓ Realtime Database configured (${CACHE_SIZE_MB}MB cache)")
        } catch (e: Exception) {
            // Ignore si déjà configuré
            if (e.message?.contains("persistence") == true) {
                Log.d(TAG, "✓ Realtime Database persistence already enabled")
            } else {
                Log.e(TAG, "❌ Realtime Database error: ${e.message}", e)
            }
        }
    }

    /**
     * Initialise Koin pour l'injection de dépendances
     */
    private fun initializeKoin() {
        try {
            startKoin {
                androidLogger()
                androidContext(this@MyApplication)
                modules(modulesDonLesDeuAppNeceFemrePas)
                if (M18CentralParametresOfAllApps.get_Default().its_AppType == AppType.JomLaElectroLivreurGrossist_PresenterScreen) {
                    modules(appModule_App2_ac_app1)
                } else {
                    modules(appModule)
                }
            }
            Log.d(TAG, "✓ Koin initialized")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Koin initialization error: ${e.message}", e)
        }
    }

    /**
     * Vérifie si une connexion réseau est disponible
     */
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    private fun isNetworkAvailable(): Boolean {
        return try {
            val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as? ConnectivityManager
                ?: return false

            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } catch (e: Exception) {
            Log.e(TAG, "Network check error: ${e.message}")
            false
        }
    }
}
