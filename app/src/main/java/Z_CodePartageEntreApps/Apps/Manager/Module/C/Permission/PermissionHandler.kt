package Z_CodePartageEntreApps.Apps.Manager.Module.C.Permission

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.example.clientjetpack.MainActivity

class PermissionHandler(private val activity: MainActivity) {
    companion object {
        private const val PREFS_NAME = "PermissionPrefs"
        private const val PERMISSIONS_GRANTED_KEY = "PermissionsGranted"
        private const val TAG = "PermissionHandler"
    }

    private val prefs: SharedPreferences = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private var showDialog by mutableStateOf(false)
    var showStorageExplanationDialog by mutableStateOf(false)
    private var permissionCallback: PermissionCallback? = null

    interface PermissionCallback {
        fun onPermissionsGranted()
        fun onPermissionsDenied()
        fun onPermissionRationale(permissions: Array<String>)
    }

    private val requestPermissionLauncher = activity.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        Log.d(TAG, "Permissions result: $permissions, all granted: $allGranted")

        if (allGranted) {
            // Check if we also need MANAGE_EXTERNAL_STORAGE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    savePermissionsGranted()
                    permissionCallback?.onPermissionsGranted()
                    showDialog = false
                } else {
                    // Request MANAGE_EXTERNAL_STORAGE
                    requestManageExternalStorage()
                }
            } else {
                savePermissionsGranted()
                permissionCallback?.onPermissionsGranted()
                showDialog = false
            }
        } else {
            val denied = permissions.filter { !it.value }.keys.toTypedArray()
            Log.d(TAG, "Denied permissions: ${denied.joinToString()}")
            permissionCallback?.onPermissionsDenied()
        }
    }

    /**
     * Get required permissions based on the device's API level
     */
    fun getRequiredPermissions(): Array<String> {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.NEARBY_WIFI_DEVICES,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            else -> arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }

    /**
     * Check if all required permissions are granted INCLUDING storage access
     */
    fun arePermissionsGranted(): Boolean {
        val requiredPermissions = getRequiredPermissions()
        val standardPermissionsGranted = requiredPermissions.all {
            ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
        }

        // For Android 11+, also check MANAGE_EXTERNAL_STORAGE
        val storageGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            true // Not needed on older versions
        }

        // Log the status of each permission for debugging
        if (!standardPermissionsGranted || !storageGranted) {
            requiredPermissions.forEach {
                val granted = ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
                Log.d(TAG, "🔐 Permission $it: ${if (granted) "ACCORDÉE" else "MANQUANTE"}")
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Log.d(TAG, "🔐 MANAGE_EXTERNAL_STORAGE: ${if (storageGranted) "ACCORDÉE" else "MANQUANTE"}")
            }
        }

        return standardPermissionsGranted && storageGranted
    }

    /**
     * Save the permissions granted status in SharedPreferences
     */
    private fun savePermissionsGranted() {
        if (arePermissionsGranted()) {
            prefs.edit().putBoolean(PERMISSIONS_GRANTED_KEY, true).apply()
            Log.d(TAG, "✅ Toutes les permissions sont accordées et sauvegardées")
        } else {
            Log.d(TAG, "❌ Certaines permissions manquent encore")
        }
    }

    /**
     * Request MANAGE_EXTERNAL_STORAGE permission (Android 11+)
     * Shows explanation dialog first
     */
    private fun requestManageExternalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            showStorageExplanationDialog = true
        }
    }

    /**
     * Actually open the storage settings (called after user confirms dialog)
     */
    fun openStorageSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                    data = Uri.parse("package:${activity.packageName}")
                }
                activity.startActivity(intent)

                Log.d(TAG, "Opening storage settings")

                // Note: We can't detect when user returns from settings
                // App will need to recheck permissions when resumed
            } catch (e: Exception) {
                Log.e(TAG, "Error opening storage settings", e)
                // Fallback to general settings
                openAppSettings()
            }
        }
    }

    /**
     * Check and request required permissions
     */
    fun checkAndRequestPermissions(callback: PermissionCallback) {
        this.permissionCallback = callback

        if (arePermissionsGranted()) {
            Log.d(TAG, "Permissions already granted, proceeding")
            callback.onPermissionsGranted()
            return
        }

        // Check if we need MANAGE_EXTERNAL_STORAGE first
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            Log.d(TAG, "Requesting MANAGE_EXTERNAL_STORAGE first")
            requestManageExternalStorage()
            // Don't return - let the user complete this, then check standard permissions
        }

        val permissionsToRequest = getRequiredPermissions().filter {
            ContextCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        Log.d(TAG, "Requesting permissions: ${permissionsToRequest.joinToString()}")

        when {
            permissionsToRequest.isEmpty() -> {
                // Standard permissions are granted, but check storage again
                if (arePermissionsGranted()) {
                    savePermissionsGranted()
                    callback.onPermissionsGranted()
                } else {
                    // Must be waiting for MANAGE_EXTERNAL_STORAGE
                    Log.d(TAG, "Waiting for storage management permission")
                }
            }
            else -> {
                requestPermissionLauncher.launch(permissionsToRequest)
            }
        }
    }

    /**
     * Open the app settings to allow the user to grant permissions manually
     */
    fun openAppSettings() {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", activity.packageName, null)
            activity.startActivity(this)
        }
    }
}
