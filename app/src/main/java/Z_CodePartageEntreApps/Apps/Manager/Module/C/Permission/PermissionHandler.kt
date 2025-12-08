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
    private var pendingStandardPermissions = false

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
            savePermissionsGranted()
            checkFinalPermissionState()
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
                Manifest.permission.READ_MEDIA_IMAGES
            )
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE
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
     * Check if standard runtime permissions are granted
     */
    private fun areStandardPermissionsGranted(): Boolean {
        val requiredPermissions = getRequiredPermissions()
        return requiredPermissions.all {
            ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Check if storage permission is granted (Android 11+)
     */
    private fun isStoragePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            // On older versions, check if WRITE_EXTERNAL_STORAGE is granted
            val permissions = getRequiredPermissions()
            if (Manifest.permission.WRITE_EXTERNAL_STORAGE in permissions) {
                ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        }
    }

    /**
     * Check if ALL permissions (including storage) are granted
     */
    fun arePermissionsGranted(): Boolean {
        val standardGranted = areStandardPermissionsGranted()
        val storageGranted = isStoragePermissionGranted()

        if (!standardGranted || !storageGranted) {
            logPermissionStatus()
        }

        return standardGranted && storageGranted
    }

    /**
     * Log detailed permission status for debugging
     */
    private fun logPermissionStatus() {
        Log.d(TAG, "=== Permission Status ===")
        getRequiredPermissions().forEach {
            val granted = ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
            Log.d(TAG, "🔐 Permission $it: ${if (granted) "ACCORDÉE" else "MANQUANTE"}")
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Log.d(TAG, "🔐 MANAGE_EXTERNAL_STORAGE: ${if (isStoragePermissionGranted()) "ACCORDÉE" else "MANQUANTE"}")
        }
        Log.d(TAG, "=======================")
    }

    /**
     * Save the permissions granted status
     */
    private fun savePermissionsGranted() {
        if (arePermissionsGranted()) {
            prefs.edit().putBoolean(PERMISSIONS_GRANTED_KEY, true).apply()
            Log.d(TAG, "✅ All permissions granted and saved")
        }
    }

    /**
     * Check final permission state and notify callback
     */
    private fun checkFinalPermissionState() {
        if (arePermissionsGranted()) {
            permissionCallback?.onPermissionsGranted()
            showDialog = false
            showStorageExplanationDialog = false
        } else if (!isStoragePermissionGranted() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Standard permissions granted but storage not yet
            Log.d(TAG, "Standard permissions OK, requesting storage management")
            requestManageExternalStorage()
        } else {
            permissionCallback?.onPermissionsDenied()
        }
    }

    /**
     * Request MANAGE_EXTERNAL_STORAGE permission (Android 11+)
     */
    private fun requestManageExternalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            showStorageExplanationDialog = true
        }
    }

    /**
     * Open storage settings for MANAGE_EXTERNAL_STORAGE
     */
    fun openStorageSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                    data = Uri.parse("package:${activity.packageName}")
                }
                activity.startActivity(intent)
                Log.d(TAG, "Opened storage settings")
            } catch (e: Exception) {
                Log.e(TAG, "Error opening storage settings", e)
                openAppSettings()
            }
        }
    }

    /**
     * Main method to check and request all permissions
     */
    fun checkAndRequestPermissions(callback: PermissionCallback) {
        this.permissionCallback = callback

        // Quick check if everything is already granted
        if (arePermissionsGranted()) {
            Log.d(TAG, "✅ All permissions already granted")
            callback.onPermissionsGranted()
            return
        }

        // Strategy for Android 11+:
        // 1. Request standard runtime permissions first
        // 2. Then request MANAGE_EXTERNAL_STORAGE if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!areStandardPermissionsGranted()) {
                // Request standard permissions first
                requestStandardPermissions()
            } else if (!isStoragePermissionGranted()) {
                // Standard permissions OK, request storage
                requestManageExternalStorage()
            }
        } else {
            // For older Android versions, just request all at once
            requestStandardPermissions()
        }
    }

    /**
     * Request standard runtime permissions
     */
    private fun requestStandardPermissions() {
        val permissionsToRequest = getRequiredPermissions().filter {
            ContextCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (permissionsToRequest.isEmpty()) {
            Log.d(TAG, "No standard permissions to request")
            checkFinalPermissionState()
        } else {
            Log.d(TAG, "Requesting ${permissionsToRequest.size} permissions: ${permissionsToRequest.joinToString()}")
            requestPermissionLauncher.launch(permissionsToRequest)
        }
    }

    /**
     * Open app settings
     */
    fun openAppSettings() {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", activity.packageName, null)
            activity.startActivity(this)
        }
    }
}
