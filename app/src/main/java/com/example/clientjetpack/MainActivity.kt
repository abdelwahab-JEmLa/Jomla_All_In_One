package com.example.clientjetpack

import P0_MainScreen.Main.MainScreen
import P6_AiGroupeForSupplier.GenerativeAiViewModel
import Z_MasterOfApps.Kotlin.ViewModel.Init.B_Load.initializeFirebase
import Z_MasterOfApps.Z.Android.A_MainActivityApp.Start.Modules.PermissionHandler
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import Z_CodePartageEntreApps.Modules.AppDatabase
import Z_CodePartageEntreApps.Modules.appModule
import com.example.clientjetpack.ui.theme.ClientJetPackTheme
import com.google.firebase.FirebaseApp
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.context.startKoin

private const val TAG = "MainActivity"

class MyApplication : Application() {
    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        initializeComponents()
    }

    private fun initializeComponents() {
        runCatching {
            AppDatabase.DatabaseModule.getDatabase(this).also { database = it }
            FirebaseApp.initializeApp(this)?.let(::initializeFirebase)
                ?: throw IllegalStateException("Firebase initialization failed")

            // Initialize Koin
            // Start Koin
            startKoin {
                // Log Koin into Android logger with default settings
                androidLogger()
                // Declare Android context
                androidContext(this@MyApplication)
                // Declare modules
                modules(appModule)
            }
        }.onFailure {
            Log.e(TAG, "Error initializing components", it)
        }
    }
}

data class AppViewModels(
    val generativeAiViewModel: GenerativeAiViewModel,
)

class ViewModelFactory(
    private val context: Context,
    private val database: AppDatabase,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {


        modelClass.isAssignableFrom(GenerativeAiViewModel::class.java) ->
            GenerativeAiViewModel() as T

        else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}

class MainActivity : ComponentActivity() {
    private val database by lazy { (application as MyApplication).database }
    private val permissionHandler by lazy { PermissionHandler(this) }
    private val viewModelFactory by lazy { ViewModelFactory(applicationContext, database) }

    private val generativeAiViewModel: GenerativeAiViewModel by viewModels { viewModelFactory }
    private val appViewModels by lazy {
        AppViewModels(generativeAiViewModel)
    }

    private var permissionsChecked by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: API level ${Build.VERSION.SDK_INT}")
        setupActivityContent()
    }

    @OptIn(KoinExperimentalAPI::class)
    private fun setupActivityContent() {
        runCatching {
            setContent {
                ClientJetPackTheme {
                    KoinAndroidContext {
                        Box(modifier = Modifier.fillMaxSize()) {
                            if (permissionsChecked) {
                                MainScreen()
                            } else {}
                        }
                    }
                }
            }

            // Handle permissions for all API levels
            handlePermissions()
        }.onFailure {
            Log.e(TAG, "Error in setupActivityContent", it)
            Toast.makeText(
                this,
                "Une erreur s'est produite. Veuillez redémarrer l'application.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun handlePermissions() {
        Log.d(TAG, "Checking permissions...")

        if (permissionHandler.arePermissionsGranted()) {
            Log.d(TAG, "All permissions are already granted")
            permissionsChecked = true
            return
        }

        permissionHandler.checkAndRequestPermissions(object : PermissionHandler.PermissionCallback {
            override fun onPermissionsGranted() {
                Log.d(TAG, "All permissions granted through request")
                permissionsChecked = true
            }

            override fun onPermissionsDenied() {
                Log.d(TAG, "Some permissions denied")
                showPermissionDeniedMessage()
                // Still setting permissionsChecked to true to allow the app to run with limited functionality
                permissionsChecked = true
            }

            override fun onPermissionRationale(permissions: Array<String>) {
                Log.d(TAG, "Permission rationale needed for: ${permissions.joinToString()}")
                // This is handled in PermissionHandler
            }
        })
    }

    private fun showPermissionDeniedMessage() {
        Toast.makeText(
            this,
            "Certaines fonctionnalités seront limitées sans les permissions nécessaires",
            Toast.LENGTH_LONG
        ).show()
    }
}
