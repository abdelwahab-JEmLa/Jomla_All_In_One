package com.example.clientjetpack

import P0_MainScreen.Main.MainScreen
import P6_AiGroupeForSupplier.GenerativeAiViewModel
import Z_MasterOfApps.Kotlin.ViewModel.Init.B_Load.initializeFirebase
import Z_MasterOfApps.Z.Android.Main.Utils.PermissionHandler
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Bundle
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

class MyApplication : Application() {
    lateinit var database: AppDatabase
        private set

    private fun initializeDatabase() {
        try {
            database = AppDatabase.DatabaseModule.getDatabase(this)
        } catch (e: Exception) {
            throw e
        }
    }

    private fun initializeFirebase() {
        try {
            FirebaseApp.initializeApp(this)?.let { app ->
                initializeFirebase(app)
            } ?: run {
                throw IllegalStateException("Firebase initialization failed")
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override fun onCreate() {
        super.onCreate()
        try {
            initializeDatabase()
            initializeFirebase()
        } catch (e: Exception) {
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
        return try {
            when {
                modelClass.isAssignableFrom(HeadViewModel::class.java) ->
                    HeadViewModel(context.applicationContext, database) as T
                modelClass.isAssignableFrom(GenerativeAiViewModel::class.java) ->
                    GenerativeAiViewModel() as T
                else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
            }
        } catch (e: Exception) {
            throw e
        }
    }
}

class MainActivity : ComponentActivity() {
    private val database by lazy {
        try {
            (application as MyApplication).database
        } catch (e: Exception) {
            throw e
        }
    }

    private val permissionHandler by lazy {
        try {
            PermissionHandler(this)
        } catch (e: Exception) {
            throw e
        }
    }

    private val viewModelFactory by lazy {
        try {
            ViewModelFactory(applicationContext, database)
        } catch (e: Exception) {
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
            throw e
        }
    }

    private var permissionsChecked by mutableStateOf(false)
    private var retryCount = 0
    private val maxRetries = 3

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            setContent {
                ClientJetPackTheme {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (permissionsChecked) {
                            MainScreen(appViewModels)
                        }
                    }
                }
            }

            if (!permissionHandler.arePermissionsAlreadyGranted()) {
                checkPermissions()
            } else {
                permissionsChecked = true
            }
        } catch (e: Exception) {
            showErrorAndRestart()
        }
    }

    private fun showErrorAndRestart() {
        if (retryCount < maxRetries) {
            retryCount++
            Toast.makeText(
                this,
                "Une erreur s'est produite. Tentative de récupération...",
                Toast.LENGTH_LONG
            ).show()
            recreate()
        } else {
            Toast.makeText(
                this,
                "Une erreur critique s'est produite. Veuillez redémarrer l'application.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkPermissions() {
        permissionHandler.checkAndRequestPermissions(object : PermissionHandler.PermissionCallback {
            override fun onPermissionsGranted() {
                permissionsChecked = true
            }

            override fun onPermissionsDenied() {
                Toast.makeText(
                    this@MainActivity,
                    "Certaines fonctionnalités seront limitées sans les permissions nécessaires",
                    Toast.LENGTH_LONG
                ).show()
                permissionsChecked = true
            }

            override fun onPermissionRationale(permissions: Array<String>) {
                // La logique de rationale est gérée dans PermissionHandler
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (!permissionsChecked && retryCount < maxRetries) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                checkPermissions()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            // Nettoyage des ressources si nécessaire
        } catch (e: Exception) {
            // Handle cleanup errors silently
        }
    }
}
