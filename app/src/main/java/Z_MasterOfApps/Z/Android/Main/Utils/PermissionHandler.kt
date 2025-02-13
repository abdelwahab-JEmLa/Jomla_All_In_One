package Z_MasterOfApps.Z.Android.Main.Utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.clientjetpack.MainActivity

class PermissionHandler(private val activity: MainActivity) {

    companion object {
        private const val ANDROID_13 = Build.VERSION_CODES.TIRAMISU
        private const val ANDROID_12 = Build.VERSION_CODES.S
        private const val ANDROID_11 = Build.VERSION_CODES.R
        private const val ANDROID_10 = Build.VERSION_CODES.Q

        private const val PREFS_NAME = "PermissionPrefs"
        private const val PERMISSIONS_GRANTED_KEY = "PermissionsGranted"
    }

    private val prefs: SharedPreferences = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private var showDialog by mutableStateOf(false)
    private var permissionCallback: PermissionCallback? = null
    private var currentDeniedPermissions: Array<String> = emptyArray()

    // Permissions de localisation
    private val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    // Permissions WiFi selon la version Android
    @RequiresApi(ANDROID_13)
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

    // Permissions Bluetooth selon la version
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

    // Permissions de stockage selon la version
    private val storagePermissions = when {
        Build.VERSION.SDK_INT >= ANDROID_13 -> arrayOf(
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

    // Toutes les permissions requises
    @RequiresApi(ANDROID_13)
    private val allPermissions =
        (locationPermissions + wifiPermissions + nearbyPermissions + storagePermissions)
            .distinct()
            .toTypedArray()

    interface PermissionCallback {
        fun onPermissionsGranted()
        fun onPermissionsDenied()
        fun onPermissionRationale(permissions: Array<String>)
    }

    private val requestPermissionLauncher = activity.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        handlePermissionResult(permissions)
    }

    @RequiresApi(ANDROID_13)
    fun checkAndRequestPermissions(callback: PermissionCallback) {
        this.permissionCallback = callback

        // Vérifier si les permissions ont déjà été accordées
        if (prefs.getBoolean(PERMISSIONS_GRANTED_KEY, false)) {
            if (areAllPermissionsGranted()) {
                callback.onPermissionsGranted()
                return
            }
        }

        val permissionsToRequest = allPermissions.filter { permission ->
            ContextCompat.checkSelfPermission(
                activity,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        currentDeniedPermissions = permissionsToRequest

        when {
            permissionsToRequest.isEmpty() -> {
                prefs.edit().putBoolean(PERMISSIONS_GRANTED_KEY, true).apply()
                callback.onPermissionsGranted()
            }
            permissionsToRequest.any { shouldShowRequestPermissionRationale(it) } -> {
                showPermissionDialog()
                callback.onPermissionRationale(permissionsToRequest)
            }
            else -> {
                requestPermissionLauncher.launch(permissionsToRequest)
            }
        }
    }

    private fun handlePermissionResult(permissions: Map<String, Boolean>) {
        when {
            permissions.all { it.value } -> {
                prefs.edit().putBoolean(PERMISSIONS_GRANTED_KEY, true).apply()
                permissionCallback?.onPermissionsGranted()
                showDialog = false
            }
            permissions.any { !it.value && shouldShowRequestPermissionRationale(it.key) } -> {
                permissionCallback?.onPermissionRationale(
                    permissions.filter { !it.value }.keys.toTypedArray()
                )
                showPermissionDialog()
            }
            else -> {
                permissionCallback?.onPermissionsDenied()
                showPermissionDialog()
            }
        }
    }

    @RequiresApi(ANDROID_13)
    private fun areAllPermissionsGranted(): Boolean {
        return allPermissions.all { permission ->
            ContextCompat.checkSelfPermission(
                activity,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun shouldShowRequestPermissionRationale(permission: String): Boolean {
        return activity.shouldShowRequestPermissionRationale(permission)
    }

    private fun showPermissionDialog() {
        activity.runOnUiThread {
            showDialog = true
        }
    }

    @Composable
    fun PermissionDialogContent() {
        if (showDialog) {
            AlertDialog(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onDismissRequest = { showDialog = false },
                title = { Text("Permissions Requises") },
                text = { Text(getPermissionMessage()) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDialog = false
                            openAppSettings()
                        }
                    ) {
                        Text("Paramètres")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDialog = false
                            // Réessayer les permissions si possible
                            if (currentDeniedPermissions.isNotEmpty()) {
                                requestPermissionLauncher.launch(currentDeniedPermissions)
                            }
                        }
                    ) {
                        Text("Réessayer")
                    }
                }
            )
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

    // Méthode pour réinitialiser l'état des permissions (utile pour les tests)
    fun resetPermissionsState() {
        prefs.edit().remove(PERMISSIONS_GRANTED_KEY).apply()
        showDialog = false
        currentDeniedPermissions = emptyArray()
    }
}
