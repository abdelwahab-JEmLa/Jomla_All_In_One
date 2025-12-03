package com.example.clientjetpack

import P0_MainScreen.Main.MainScreen
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.Apps.Manager.Module.C.Permission.PermissionHandler
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.clientjetpack.ui.theme.ClientJetPackTheme
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.core.annotation.KoinExperimentalAPI

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    private val database by lazy { AppDatabase.DatabaseModule.getDatabase(this) }
    private val permissionHandler by lazy { PermissionHandler(this) }

    private var permissionsChecked by mutableStateOf(false)
    private var applicationAfficheProduitsPourCompt by mutableStateOf(false)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: API level ${Build.VERSION.SDK_INT}")
        setupActivityContent()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onResume() {
        super.onResume()

        // Recheck permissions when app resumes (user might have granted storage permission)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val hasStorageAccess = Environment.isExternalStorageManager()
            Log.d(TAG, "onResume: Storage access = $hasStorageAccess")

            // If permissions weren't checked before but now we have storage access, recheck
            if (!permissionsChecked && hasStorageAccess) {
                handlePermissions()
            }
        }
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
                                MainScreen()
                            } else {
                                // You could show a permission request screen here
                            }
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
            Log.d(TAG, "✅ All permissions are already granted")
            permissionsChecked = true
            return
        }

        permissionHandler.checkAndRequestPermissions(object : PermissionHandler.PermissionCallback {
            override fun onPermissionsGranted() {
                Log.d(TAG, "✅ All permissions granted through request")
                permissionsChecked = true
            }

            override fun onPermissionsDenied() {
                Log.d(TAG, "⚠️ Some permissions denied")
                showPermissionDeniedMessage()
                // Still setting permissionsChecked to true to allow the app to run
                // PDFs will be saved to app-specific directory instead
                permissionsChecked = true
            }

            override fun onPermissionRationale(permissions: Array<String>) {
                Log.d(TAG, "ℹ️ Permission rationale needed for: ${permissions.joinToString()}")
            }
        })
    }

    private fun showPermissionDeniedMessage() {
        val message = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
            !Environment.isExternalStorageManager()) {
            "Les PDFs seront sauvegardés dans le dossier de l'application. " +
                    "Pour sauvegarder dans un dossier public, accordez l'accès au stockage dans les paramètres."
        } else {
            "Certaines fonctionnalités seront limitées sans les permissions nécessaires"
        }

        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
