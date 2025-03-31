package Z_MasterOfApps.Z.Android.A_MainActivityApp.Start.Modules

/*
import com.example.c_serveur.MainActivity
import com.example.c_serveur.R

import com.example.clientjetpack.MainActivity
import com.example.clientjetpack.R

 */


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
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
            savePermissionsGranted()
            permissionCallback?.onPermissionsGranted()
            showDialog = false
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
                Manifest.permission.CHANGE_WIFI_STATE
            )
            else -> arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    /**
     * Check if all required permissions are granted
     */
    fun arePermissionsGranted(): Boolean {
        val requiredPermissions = getRequiredPermissions()
        val checkResult = requiredPermissions.all {
            ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
        }

        // Log the status of each permission for debugging
        if (!checkResult) {
            requiredPermissions.forEach {
                val granted = ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
                Log.d(TAG, "🔐 Permission $it: ${if (granted) "ACCORDÉE" else "MANQUANTE"}")
            }
        }

        return checkResult
    }

    /**
     * Save the permissions granted status in SharedPreferences
     */
    private fun savePermissionsGranted() {
        // Only save as granted if all permissions are actually granted
        if (arePermissionsGranted()) {
            prefs.edit().putBoolean(PERMISSIONS_GRANTED_KEY, true).apply()
            Log.d(TAG, "✅ Toutes les permissions sont accordées et sauvegardées")
        } else {
            Log.d(TAG, "❌ Certaines permissions manquent encore")
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

        val permissionsToRequest = getRequiredPermissions().filter {
            ContextCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        Log.d(TAG, "Requesting permissions: ${permissionsToRequest.joinToString()}")

        when {
            permissionsToRequest.isEmpty() -> {
                savePermissionsGranted()
                callback.onPermissionsGranted()
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



