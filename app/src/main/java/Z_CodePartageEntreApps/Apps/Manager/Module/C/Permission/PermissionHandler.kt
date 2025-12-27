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
import java.io.File

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
        Log.d(TAG, "═══════════════════════════════════════════════════════")
        Log.d(TAG, "Permissions result:")
        permissions.forEach { (permission, granted) ->
            Log.d(TAG, "  ${if (granted) "✓" else "✗"} $permission")
        }
        Log.d(TAG, "All granted: $allGranted")
        Log.d(TAG, "═══════════════════════════════════════════════════════")

        if (allGranted) {
            savePermissionsGranted()
            // Test file access immediately after permissions granted
            testFileAccess()
            checkFinalPermissionState()
        } else {
            val denied = permissions.filter { !it.value }.keys.toTypedArray()
            Log.e(TAG, "⚠ Denied permissions: ${denied.joinToString()}")
            permissionCallback?.onPermissionsDenied()
        }
    }

    /**
     * Test if we can actually read files after permissions are granted
     */
    private fun testFileAccess() {
        val testPath = "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne/"
        val testDir = File(testPath)

        Log.d(TAG, "─────────────────────────────────────────────────────")
        Log.d(TAG, "Testing file access:")
        Log.d(TAG, "  Path: $testPath")
        Log.d(TAG, "  Dir exists: ${testDir.exists()}")
        Log.d(TAG, "  Dir canRead: ${testDir.canRead()}")

        if (testDir.exists() && testDir.canRead()) {
            val files = testDir.listFiles()?.take(3) // Test first 3 files
            Log.d(TAG, "  Files found: ${files?.size ?: 0}")
            files?.forEach { file ->
                Log.d(TAG, "    ${if (file.canRead()) "✓" else "✗"} ${file.name} (${file.length()} bytes)")
            }
        } else {
            Log.e(TAG, "  ✗ Cannot access directory!")
        }
        Log.d(TAG, "─────────────────────────────────────────────────────")
    }

    /**
     * Get required permissions based on the device's API level
     * PRIORITY: Storage permissions for image access
     */
    fun getRequiredPermissions(): Array<String> {
        return when {
            // Android 13+ (API 33+)
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES, // CRITICAL for images
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.NEARBY_WIFI_DEVICES
            )
            // Android 12 (API 31-32)
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE, // CRITICAL for images
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE
            )
            // Android 11 and below (API 30-)
            else -> arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE, // CRITICAL for images
                Manifest.permission.WRITE_EXTERNAL_STORAGE, // CRITICAL for full access
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE
            )
        }
    }

    /**
     * Check if standard runtime permissions are granted
     */
    private fun areStandardPermissionsGranted(): Boolean {
        val requiredPermissions = getRequiredPermissions()
        val allGranted = requiredPermissions.all {
            ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
        }

        if (!allGranted) {
            Log.d(TAG, "Standard permissions check:")
            requiredPermissions.forEach { permission ->
                val granted = ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
                Log.d(TAG, "  ${if (granted) "✓" else "✗"} $permission")
            }
        }

        return allGranted
    }

    /**
     * Check if storage permission is granted (Android 11+)
     * For Android 11+, MANAGE_EXTERNAL_STORAGE is needed for full /storage/emulated/0 access
     */
    private fun isStoragePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val hasManageStorage = Environment.isExternalStorageManager()
            Log.d(TAG, "Android 11+ storage check: MANAGE_EXTERNAL_STORAGE = $hasManageStorage")
            hasManageStorage
        } else {
            // On older versions, check if WRITE_EXTERNAL_STORAGE is granted
            val hasWritePermission = ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED

            val hasReadPermission = ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED

            Log.d(TAG, "Pre-Android 11 storage check: READ=$hasReadPermission, WRITE=$hasWritePermission")
            hasReadPermission && hasWritePermission
        }
    }

    /**
     * Check if ALL permissions (including storage) are granted
     */
    fun arePermissionsGranted(): Boolean {
        val standardGranted = areStandardPermissionsGranted()
        val storageGranted = isStoragePermissionGranted()

        Log.d(TAG, "Permission status: Standard=$standardGranted, Storage=$storageGranted")

        if (!standardGranted || !storageGranted) {
            logPermissionStatus()
        }

        return standardGranted && storageGranted
    }

    /**
     * Log detailed permission status for debugging
     */
    private fun logPermissionStatus() {
        Log.d(TAG, "═══════════════════════════════════════════════════════")
        Log.d(TAG, "DETAILED Permission Status (API ${Build.VERSION.SDK_INT}):")
        Log.d(TAG, "───────────────────────────────────────────────────────")

        // Standard permissions
        Log.d(TAG, "Standard Permissions:")
        getRequiredPermissions().forEach {
            val granted = ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
            Log.d(TAG, "  ${if (granted) "✓" else "✗"} $it")
        }

        // Storage permission
        Log.d(TAG, "───────────────────────────────────────────────────────")
        Log.d(TAG, "Storage Access:")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val hasManage = Environment.isExternalStorageManager()
            Log.d(TAG, "  ${if (hasManage) "✓" else "✗"} MANAGE_EXTERNAL_STORAGE")
        } else {
            val hasRead = ContextCompat.checkSelfPermission(
                activity, Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
            val hasWrite = ContextCompat.checkSelfPermission(
                activity, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
            Log.d(TAG, "  ${if (hasRead) "✓" else "✗"} READ_EXTERNAL_STORAGE")
            Log.d(TAG, "  ${if (hasWrite) "✓" else "✗"} WRITE_EXTERNAL_STORAGE")
        }

        Log.d(TAG, "═══════════════════════════════════════════════════════")
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
            Log.d(TAG, "✅ All permissions confirmed - app ready")
            permissionCallback?.onPermissionsGranted()
            showDialog = false
            showStorageExplanationDialog = false
        } else if (!isStoragePermissionGranted() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Standard permissions granted but storage not yet
            Log.d(TAG, "⚠ Standard permissions OK, but need MANAGE_EXTERNAL_STORAGE")
            requestManageExternalStorage()
        } else {
            Log.e(TAG, "❌ Permissions incomplete")
            permissionCallback?.onPermissionsDenied()
        }
    }

    /**
     * Request MANAGE_EXTERNAL_STORAGE permission (Android 11+)
     */
    private fun requestManageExternalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Log.d(TAG, "Showing storage explanation dialog for MANAGE_EXTERNAL_STORAGE")
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
                Log.d(TAG, "✓ Opened storage settings")
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

        Log.d(TAG, "═══════════════════════════════════════════════════════")
        Log.d(TAG, "STARTING Permission Check (API ${Build.VERSION.SDK_INT})")
        Log.d(TAG, "═══════════════════════════════════════════════════════")

        // Quick check if everything is already granted
        if (arePermissionsGranted()) {
            Log.d(TAG, "✅ All permissions already granted - no action needed")
            callback.onPermissionsGranted()
            return
        }

        // Strategy for Android 11+:
        // 1. Request standard runtime permissions first
        // 2. Then request MANAGE_EXTERNAL_STORAGE if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!areStandardPermissionsGranted()) {
                Log.d(TAG, "→ Requesting standard permissions first...")
                requestStandardPermissions()
            } else if (!isStoragePermissionGranted()) {
                Log.d(TAG, "→ Standard OK, requesting MANAGE_EXTERNAL_STORAGE...")
                requestManageExternalStorage()
            }
        } else {
            // For older Android versions, just request all at once
            Log.d(TAG, "→ Requesting all permissions (pre-Android 11)...")
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
            Log.d(TAG, "Requesting ${permissionsToRequest.size} permissions:")
            permissionsToRequest.forEach {
                Log.d(TAG, "  → $it")
            }
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
