package com.example.clientjetpack

import Application2.App.MainScreen.MainScreen_Jemla_Com_PresentoirApp
import P0_MainScreen.Main.MainScreen
import EntreApps.Shared.Models.Components.AppType
import EntreApps.Shared.Models.M18CentralParametresOfAllApps
import EntreApps.Shared.Modules.PermissionHandler
import EntreApps.Shared.Modules.StoragePermissionDialog
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
import com.example.clientjetpack.ui.theme.ClientJetPackTheme
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.core.annotation.KoinExperimentalAPI

class MainActivity : ComponentActivity() {
    lateinit var content: () -> Unit
    private val permissionHandler by lazy { PermissionHandler(this) }
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
                        Box(modifier = Modifier.fillMaxSize()) {
                            if (permissionsChecked) {
                                when (M18CentralParametresOfAllApps.get_Default().its_AppType) {
                                    AppType.JomLaElectroLivreurGrossist_PresenterScreen -> MainScreen_Jemla_Com_PresentoirApp()
                                    else -> MainScreen()
                                }

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

        permissionHandler.checkAndRequestPermissions(object : PermissionHandler.PermissionCallback {
            override fun onPermissionsGranted() {
                permissionsChecked = true
                showStorageDialog = false
                Toast.makeText(this@MainActivity, "✓ جميع الأذونات ممنوحة - الصور ستظهر الآن", Toast.LENGTH_SHORT).show()
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
