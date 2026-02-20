package Application2.App.App.Archive

import android.app.Application
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

@Suppress("DEPRECATION")
class AppEcranPresntoireJemlaCom : Application() {
    companion object {
        private const val TAG = "MyApplication"
        private const val CACHE_SIZE_MB = 100L
        private const val CACHE_SIZE_BYTES = CACHE_SIZE_MB * 1024L * 1024L
    }

    override fun onCreate() {
        super.onCreate()

        Log.d(TAG, "Application starting...")

        // CRITIQUE: Initialiser Firebase AVANT tout le reste
        // pour empêcher les autres composants de créer leurs propres instances
        initializeFirebase()

        // Initialiser Koin après Firebase
        initializeKoin()

        Log.d(TAG, "Application initialized successfully")
    }

    /**
     * Initialise Firebase avec gestion d'erreur complète
     * DOIT être appelé en premier pour éviter les problèmes de DNS
     */
    private fun initializeFirebase() {
        try {
            // 1. Initialiser Firebase App si nécessaire
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
                Log.d(TAG, "✓ Firebase App initialized")
            }

            // 2. Configurer Firestore EN PREMIER avec mode offline
            // Cela empêche les tentatives de connexion immédiate
            configureFirestoreOfflineFirst()

            // 3. Configurer Realtime Database
            configureRealtimeDatabase()

            // 4. Vérifier la connectivité et activer le réseau si disponible
            if (isNetworkAvailable()) {
                enableFirestoreNetwork()
            } else {
                Log.w(TAG, "⚠ No network - Firebase will work in offline mode")
            }

        } catch (e: Exception) {
            Log.e(TAG, "❌ Firebase initialization error: ${e.message}", e)
        }
    }

    /**
     * Configure Firestore en mode hors ligne d'abord
     * Cela empêche les tentatives de connexion DNS immédiates
     */
    private fun configureFirestoreOfflineFirst() {
        try {
            val firestore = FirebaseFirestore.getInstance()

            // Configuration pour mode offline-first
            val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .setCacheSizeBytes(CACHE_SIZE_BYTES)
                .build()

            firestore.firestoreSettings = settings

            // DÉSACTIVER le réseau immédiatement pour éviter les tentatives de connexion
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
            Handler(Looper.getMainLooper()).postDelayed({
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
                androidContext(this@AppEcranPresntoireJemlaCom)
            }
            Log.d(TAG, "✓ Koin initialized")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Koin initialization error: ${e.message}", e)
        }
    }

    /**
     * Vérifie si une connexion réseau est disponible
     */
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
