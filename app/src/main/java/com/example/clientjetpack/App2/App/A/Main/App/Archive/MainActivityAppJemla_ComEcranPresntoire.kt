package com.example.clientjetpack.App2.App.A.Main.App.Archive

import Z_CodePartageEntreApps.Apps.Manager.Module.C.Permission.StoragePermissionDialog
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
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
import com.example.clientjetpack.App2.App.A.Main.Init.PermissionHandler_App2
import com.example.clientjetpack.App2.App.A.Main.MainScreen.MainScreen_Jemla_Com_PresentoirApp
import com.example.clientjetpack.ui.theme.ClientJetPackTheme
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.core.annotation.KoinExperimentalAPI

class MainActivityAppJemla_ComEcranPresntoire : ComponentActivity() {
    lateinit var content: () -> Unit
    private val permissionHandler by lazy { PermissionHandler_App2(this) }
    private var permissionsChecked by mutableStateOf(false)
    private var showStorageDialog by mutableStateOf(false)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActivityContent()
    }

    @SuppressLint("ObsoleteSdkInt")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onResume() {
        super.onResume()

        val hasStorageAccess = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }

        if (hasStorageAccess && showStorageDialog) {
            showStorageDialog = false
            permissionsChecked = true
        }

        if (!permissionsChecked && hasStorageAccess) {
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
                        Box(modifier = Modifier.Companion.fillMaxSize()) {
                            if (permissionsChecked) {
                                MainScreen_Jemla_Com_PresentoirApp()
                            }

                            if (showStorageDialog) {
                                StoragePermissionDialog(
                                    onOpenSettings = { permissionHandler.openStorageSettings() },
                                    onDismiss = {
                                        showStorageDialog = false
                                        permissionsChecked = true
                                        showPermissionDeniedMessage()
                                    }
                                )
                            }
                        }
                    }
                }
            }

            handlePermissions()
        }.onFailure {
            Toast.makeText(this, "Une erreur s'est produite. Veuillez redémarrer l'application.", Toast.LENGTH_LONG).show()
        }
    }

    private fun handlePermissions() {
        if (permissionHandler.arePermissionsGranted()) {
            permissionsChecked = true
            return
        }

        permissionHandler.checkAndRequestPermissions(object : PermissionHandler_App2.PermissionCallback {
            override fun onPermissionsGranted() {
                permissionsChecked = true
                showStorageDialog = false
                Toast.makeText(this@MainActivityAppJemla_ComEcranPresntoire, "✓ جميع الأذونات ممنوحة - الصور ستظهر الآن", Toast.LENGTH_SHORT).show()
            }

            override fun onPermissionsDenied() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
                    showStorageDialog = true
                    return
                }
                showPermissionDeniedMessage()
                permissionsChecked = true
            }

            override fun onPermissionRationale(permissions: Array<String>) {}
        })
    }

    private fun showPermissionDeniedMessage() {
        val message = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            "⚠ الصور لن تظهر بدون إذن الوصول للملفات. يمكنك منح الإذن من الإعدادات لاحقاً"
        } else {
            "⚠ بعض الوظائف محدودة بدون الأذونات الكاملة"
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
