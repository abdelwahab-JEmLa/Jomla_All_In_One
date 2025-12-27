package com.example.clientjetpack

import P0_MainScreen.Main.MainScreen
import Z_CodePartageEntreApps.Apps.Manager.Module.C.Permission.PermissionHandler
import Z_CodePartageEntreApps.Apps.Manager.Module.C.Permission.StoragePermissionDialog
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
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
import androidx.core.content.ContextCompat
import com.example.clientjetpack.ui.theme.ClientJetPackTheme
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.core.annotation.KoinExperimentalAPI

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    lateinit var content: () -> Unit
    private val permissionHandler by lazy { PermissionHandler(this) }

    private var permissionsChecked by mutableStateOf(false)
    private var showStorageDialog by mutableStateOf(false)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "═══════════════════════════════════════════════════════")
        Log.d(TAG, "MainActivity onCreate")
        Log.d(TAG, "  API level: ${Build.VERSION.SDK_INT}")
        Log.d(TAG, "  Android version: ${Build.VERSION.RELEASE}")
        Log.d(TAG, "═══════════════════════════════════════════════════════")
        setupActivityContent()
    }

    @SuppressLint("ObsoleteSdkInt")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onResume() {
        super.onResume()

        // Check storage access based on API level
        val hasStorageAccess = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // API 30+ - Use MANAGE_EXTERNAL_STORAGE
            Environment.isExternalStorageManager()
        } else {
            // API 29 and below - Check WRITE_EXTERNAL_STORAGE
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
        }

        Log.d(TAG, "─────────────────────────────────────────────────────")
        Log.d(TAG, "onResume - Storage Status:")
        Log.d(TAG, "  API: ${Build.VERSION.SDK_INT}")
        Log.d(TAG, "  Storage access: ${if (hasStorageAccess) "✓ GRANTED" else "✗ DENIED"}")
        Log.d(TAG, "  Permissions checked: $permissionsChecked")
        Log.d(TAG, "─────────────────────────────────────────────────────")

        // If we just came back from settings and now have storage access
        if (hasStorageAccess && showStorageDialog) {
            Log.d(TAG, "✅ Storage access granted from settings!")
            showStorageDialog = false
            permissionsChecked = true
        }

        // If permissions weren't checked before but now we have storage access, recheck
        if (!permissionsChecked && hasStorageAccess) {
            Log.d(TAG, "Rechecking permissions after storage grant...")
            handlePermissions()
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
                                // Show loading or permission request screen
                            }

                            // Show storage permission dialog if needed
                            if (showStorageDialog) {
                                StoragePermissionDialog(
                                    onOpenSettings = {
                                        Log.d(TAG, "User clicked to open storage settings")
                                        permissionHandler.openStorageSettings()
                                    },
                                    onDismiss = {
                                        Log.d(TAG, "User dismissed storage dialog")
                                        showStorageDialog = false
                                        // Allow app to continue without storage access
                                        permissionsChecked = true
                                        showPermissionDeniedMessage()
                                    }
                                )
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
        Log.d(TAG, "═══════════════════════════════════════════════════════")
        Log.d(TAG, "handlePermissions() called")
        Log.d(TAG, "═══════════════════════════════════════════════════════")

        if (permissionHandler.arePermissionsGranted()) {
            Log.d(TAG, "✅ All permissions are already granted")
            permissionsChecked = true
            return
        }

        permissionHandler.checkAndRequestPermissions(object : PermissionHandler.PermissionCallback {
            override fun onPermissionsGranted() {
                Log.d(TAG, "═══════════════════════════════════════════════════════")
                Log.d(TAG, "✅ CALLBACK: All permissions granted")
                Log.d(TAG, "═══════════════════════════════════════════════════════")
                permissionsChecked = true
                showStorageDialog = false

                // Show success message
                Toast.makeText(
                    this@MainActivity,
                    "✓ جميع الأذونات ممنوحة - الصور ستظهر الآن",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onPermissionsDenied() {
                Log.d(TAG, "═══════════════════════════════════════════════════════")
                Log.d(TAG, "⚠ CALLBACK: Some permissions denied")
                Log.d(TAG, "═══════════════════════════════════════════════════════")

                // Check if it's specifically storage that's denied
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (!Environment.isExternalStorageManager()) {
                        Log.d(TAG, "→ Storage not granted, showing dialog")
                        showStorageDialog = true
                        return
                    }
                }

                showPermissionDeniedMessage()
                // Allow app to continue
                permissionsChecked = true
            }

            override fun onPermissionRationale(permissions: Array<String>) {
                Log.d(TAG, "ℹ️ CALLBACK: Permission rationale needed")
                Log.d(TAG, "  Permissions: ${permissions.joinToString()}")
            }
        })
    }

    private fun showPermissionDeniedMessage() {
        val message = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
            !Environment.isExternalStorageManager()) {
            "⚠ الصور لن تظهر بدون إذن الوصول للملفات. يمكنك منح الإذن من الإعدادات لاحقاً"
        } else {
            "⚠ بعض الوظائف محدودة بدون الأذونات الكاملة"
        }

        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        Log.w(TAG, message)
    }
}
