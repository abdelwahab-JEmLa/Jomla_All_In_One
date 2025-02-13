package Z_MasterOfApps.Z.Android.Main.Utils

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.example.clientjetpack.ui.theme.ClientJetPackTheme

class PermissionHandler(private val activity: ComponentActivity) {
    companion object {
        private const val ANDROID_12 = Build.VERSION_CODES.S
        private const val ANDROID_11 = Build.VERSION_CODES.R
        private const val ANDROID_10 = Build.VERSION_CODES.Q
    }

    // Location permissions required for all versions
    private val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    // WiFi permissions based on Android version
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val wifiPermissions = when {
        Build.VERSION.SDK_INT >= ANDROID_12 -> arrayOf(
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.NEARBY_WIFI_DEVICES
        )
        else -> arrayOf(
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE
        )
    }

    // Bluetooth permissions based on version
    private val nearbyPermissions = when {
        Build.VERSION.SDK_INT >= ANDROID_12 -> arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_CONNECT
        )
        Build.VERSION.SDK_INT >= ANDROID_10 -> arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        else -> arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN
        )
    }

    // Storage permissions based on version
    private val storagePermissions = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO
        )
        Build.VERSION.SDK_INT >= ANDROID_10 -> arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        else -> arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    // All required permissions
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val allPermissions =
        (locationPermissions + wifiPermissions + nearbyPermissions + storagePermissions)
            .distinct()
            .toTypedArray()

    interface PermissionCallback {
        fun onPermissionsGranted()
        fun onPermissionsDenied()
        fun onPermissionRationale(permissions: Array<String>)
    }

    private var permissionCallback: PermissionCallback? = null
    private var showDialog by mutableStateOf(false)

    private val requestPermissionLauncher = activity.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        handlePermissionResult(permissions)
    }

    private fun handlePermissionResult(permissions: Map<String, Boolean>) {
        val deniedPermissions = permissions.filterValues { !it }.keys.toTypedArray()

        when {
            deniedPermissions.isEmpty() -> {
                permissionCallback?.onPermissionsGranted()
            }
            deniedPermissions.any { shouldShowRequestPermissionRationale(it) } -> {
                permissionCallback?.onPermissionRationale(deniedPermissions)
                showPermissionDialog()
            }
            else -> {
                permissionCallback?.onPermissionsDenied()
                showPermissionDialog()
            }
        }
    }

    private fun shouldShowRequestPermissionRationale(permission: String): Boolean {
        return activity.shouldShowRequestPermissionRationale(permission)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun checkAndRequestPermissions(callback: PermissionCallback) {
        this.permissionCallback = callback

        val permissionsToRequest = allPermissions.filter { permission ->
            ContextCompat.checkSelfPermission(
                activity,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        when {
            permissionsToRequest.isEmpty() -> {
                callback.onPermissionsGranted()
            }
            permissionsToRequest.any { shouldShowRequestPermissionRationale(it) } -> {
                callback.onPermissionRationale(permissionsToRequest)
                requestPermissionLauncher.launch(permissionsToRequest)
            }
            else -> {
                requestPermissionLauncher.launch(permissionsToRequest)
            }
        }
    }

    private fun showPermissionDialog() {
        showDialog = true
        activity.setContent {
            PermissionDialog()
        }
    }

    @Composable
    private fun PermissionDialog() {
        var showDialogState by remember { mutableStateOf(showDialog) }

        if (showDialogState) {
            ClientJetPackTheme {
                AlertDialog(
                    onDismissRequest = { showDialogState = false },
                    title = { Text("Permissions Requises") },
                    text = { Text(getPermissionMessage()) },
                    confirmButton = {
                        TextButton(onClick = {
                            showDialogState = false
                            openAppSettings()
                        }) {
                            Text("Paramètres")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialogState = false }) {
                            Text("Annuler")
                        }
                    }
                )
            }
        }
    }

    private fun getPermissionMessage(): String {
        return when {
            Build.VERSION.SDK_INT >= ANDROID_12 -> {
                "Cette application nécessite l'accès au WiFi, aux appareils à proximité et au stockage. " +
                        "Veuillez accorder ces permissions dans les Paramètres."
            }
            Build.VERSION.SDK_INT >= ANDROID_11 -> {
                "Cette application nécessite l'accès à la localisation pour le WiFi et le Bluetooth, " +
                        "ainsi que l'accès au stockage. Veuillez accorder ces permissions dans les Paramètres."
            }
            else -> {
                "Cette application nécessite l'accès à la localisation, au WiFi, au Bluetooth et au stockage. " +
                        "Veuillez accorder ces permissions dans les Paramètres."
            }
        }
    }

    private fun openAppSettings() {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", activity.packageName, null)
            activity.startActivity(this)
        }
    }
}
