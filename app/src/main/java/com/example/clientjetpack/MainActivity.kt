package com.example.clientjetpack

import P0_MainScreen.Main.MainScreen
import P6_AiGroupeForSupplier.GenerativeAiViewModel
import Z_MasterOfApps.Kotlin.ViewModel.Init.B_Load.initializeFirebase
import Z_MasterOfApps.Z.Android.Main.Utils.PermissionHandler
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.clientjetpack.Modules.AppDatabase
import com.example.clientjetpack.ViewModel.HeadViewModel
import com.example.clientjetpack.ui.theme.ClientJetPackTheme
import com.google.firebase.FirebaseApp

private const val TAG = "MainActivity"

class MyApplication : Application() {
    lateinit var database: AppDatabase
        private set

    private fun initializeDatabase() {
        try {
            database = AppDatabase.DatabaseModule.getDatabase(this)
            Log.d(TAG, "Database initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize database", e)
            throw e
        }
    }

    private fun initializeFirebase() {
        try {
            FirebaseApp.initializeApp(this)?.let { app ->
                initializeFirebase(app)
                Log.d(TAG, "Firebase initialized successfully")
            } ?: run {
                Log.e(TAG, "Firebase initialization returned null")
                throw IllegalStateException("Firebase initialization failed")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Firebase", e)
            throw e
        }
    }

    override fun onCreate() {
        super.onCreate()
        try {
            initializeDatabase()
            initializeFirebase()
        } catch (e: Exception) {
            Log.e(TAG, "Application initialization failed", e)
            // Consider showing a user-friendly error message
        }
    }
}

data class AppViewModels(
    val headViewModel: HeadViewModel,
    val generativeAiViewModel: GenerativeAiViewModel,
)

class ViewModelFactory(
    private val context: Context,
    private val database: AppDatabase,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        Log.d(TAG, "Creating ViewModel: ${modelClass.simpleName}")
        return try {
            when {
                modelClass.isAssignableFrom(HeadViewModel::class.java) ->
                    HeadViewModel(context.applicationContext, database) as T
                modelClass.isAssignableFrom(GenerativeAiViewModel::class.java) ->
                    GenerativeAiViewModel() as T
                else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create ViewModel: ${modelClass.simpleName}", e)
            throw e
        }
    }
}

class MainActivity : ComponentActivity() {
    private val database by lazy {
        try {
            (application as MyApplication).database
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize database", e)
            throw e
        }
    }

    private val permissionHandler by lazy {
        try {
            PermissionHandler(this)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize PermissionHandler", e)
            throw e
        }
    }

    private val viewModelFactory by lazy {
        try {
            ViewModelFactory(applicationContext, database)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create ViewModelFactory", e)
            throw e
        }
    }

    private val headViewModel: HeadViewModel by viewModels { viewModelFactory }
    private val generativeAiViewModel: GenerativeAiViewModel by viewModels { viewModelFactory }

    private val appViewModels by lazy {
        try {
            AppViewModels(
                headViewModel = headViewModel,
                generativeAiViewModel = generativeAiViewModel,
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize AppViewModels", e)
            throw e
        }
    }

    private var permissionsChecked by mutableStateOf(false)
    private var retryCount = 0
    private val maxRetries = 3

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate started")

        try {
            setContent {
                ClientJetPackTheme {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (permissionsChecked) {
                            Log.d(TAG, "Permissions checked, showing MainScreen")
                            MainScreen(appViewModels)
                        }
                    }
                }
            }

            if (!permissionHandler.arePermissionsAlreadyGranted()) {
                Log.d(TAG, "Permissions not granted, requesting permissions")
                checkPermissions()
            } else {
                Log.d(TAG, "Permissions already granted")
                permissionsChecked = true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Fatal error in onCreate", e)
            showErrorAndRestart()
        }
    }

    private fun showErrorAndRestart() {
        if (retryCount < maxRetries) {
            retryCount++
            Log.w(TAG, "Attempting retry $retryCount of $maxRetries")
            Toast.makeText(
                this,
                "Une erreur s'est produite. Tentative de récupération...",
                Toast.LENGTH_LONG
            ).show()
            recreate()
        } else {
            Log.e(TAG, "Max retries reached, showing fatal error")
            Toast.makeText(
                this,
                "Une erreur critique s'est produite. Veuillez redémarrer l'application.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkPermissions() {
        Log.d(TAG, "Checking permissions")
        permissionHandler.checkAndRequestPermissions(object : PermissionHandler.PermissionCallback {
            override fun onPermissionsGranted() {
                Log.d(TAG, "All permissions granted")
                permissionsChecked = true
            }

            override fun onPermissionsDenied() {
                Log.w(TAG, "Some permissions were denied")
                // Ne pas fermer l'application, mais informer l'utilisateur
                Toast.makeText(
                    this@MainActivity,
                    "Certaines fonctionnalités seront limitées sans les permissions nécessaires",
                    Toast.LENGTH_LONG
                ).show()
                permissionsChecked = true  // Permettre à l'app de continuer même avec des permissions limitées
            }

            override fun onPermissionRationale(permissions: Array<String>) {
                Log.i(TAG, "Showing permission rationale for: ${permissions.joinToString()}")
                // La logique de rationale est gérée dans PermissionHandler
            }
        })
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume called")
        if (!permissionsChecked && retryCount < maxRetries) {
            Log.d(TAG, "Rechecking permissions in onResume")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                checkPermissions()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy called")
        try {
            // Nettoyage des ressources si nécessaire
        } catch (e: Exception) {
            Log.e(TAG, "Error during cleanup in onDestroy", e)
        }
    }
}
