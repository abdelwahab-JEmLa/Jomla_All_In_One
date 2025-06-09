package com.example.clientjetpack

import AuthManager
import AuthResult
import P0_MainScreen.Main.MainScreen
import P6_AiGroupeForSupplier.GenerativeAiViewModel
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.Apps.Manager.Module.C.Permission.PermissionHandler
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
import androidx.lifecycle.lifecycleScope
import com.example.clientjetpack.ui.theme.ClientJetPackTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.core.annotation.KoinExperimentalAPI

private const val TAG = "MainActivity"

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
    private val database by lazy { AppDatabase.DatabaseModule.getDatabase(this) }
    private val permissionHandler by lazy { PermissionHandler(this) }
    private val viewModelFactory by lazy { ViewModelFactory(applicationContext, database) }
    private val dao by lazy { database.comptAppDao() }  // Correction ici

    private val generativeAiViewModel: GenerativeAiViewModel by viewModels { viewModelFactory }
    private val appViewModels by lazy {
        AppViewModels(generativeAiViewModel)
    }

    private var permissionsChecked by mutableStateOf(false)

    private var applicationAfficheProduitsPourCompt by mutableStateOf(false)

    private lateinit var authManager: AuthManager

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: API level ${Build.VERSION.SDK_INT}")
        setupActivityContent()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @OptIn(KoinExperimentalAPI::class)
    private fun setupActivityContent() {
        runCatching {
            setContent {
                ClientJetPackTheme {
                    KoinAndroidContext {
                        Box(modifier = Modifier.fillMaxSize()) {
                            if (permissionsChecked) {
                                // Auth setup une seule fois
                                setupAuth()
                                MainScreen()
                            }
                        }
                    }
                }
            }
            handlePermissions()
        }.onFailure { }
    }

    private fun setupAuth() {
        if (!::authManager.isInitialized) {
            // Correction : utilisez dao au lieu de database
            authManager = AuthManager(dao)  // Retirez la virgule vide

            lifecycleScope.launch {
                when (val result = authManager.authenticate()) {
                    is AuthResult.Success -> {
                        val compte = result.compte
                        applicationAfficheProduitsPourCompt = true
                        Log.d(TAG, "Auth success: ${compte.nom}")
                    }
                    is AuthResult.Error -> {
                        Log.e(TAG, "Auth error: ${result.message}")
                    }
                    // Plus besoin d'else avec sealed class
                    else -> {}
                }
            }
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

