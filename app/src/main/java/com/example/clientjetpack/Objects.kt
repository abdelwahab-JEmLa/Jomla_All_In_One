package com.example.clientjetpack

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PermissionHandler(private val activity: ComponentActivity) {
    private val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private val wifiPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.NEARBY_WIFI_DEVICES
        )
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        arrayOf(
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            // Pour Android 11 (API 30), on a toujours besoin de ACCESS_FINE_LOCATION
            // même si NEARBY_WIFI_DEVICES n'est pas disponible
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    } else {
        arrayOf(
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            // Pour Android 10 et moins, on a besoin de la localisation pour le WiFi
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private val nearbyPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_CONNECT
        )
    } else {
        arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN
        )
    }

    private val storagePermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO
        )
    } else {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    // Combine toutes les permissions nécessaires
    private val allPermissions = locationPermissions + wifiPermissions + nearbyPermissions + storagePermissions

    interface PermissionCallback {
        fun onPermissionsGranted()
        fun onPermissionsDenied()
    }

    private var permissionCallback: PermissionCallback? = null

    private val requestPermissionLauncher = activity.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            permissionCallback?.onPermissionsGranted()
        } else {
            handlePermissionDenial()
            permissionCallback?.onPermissionsDenied()
        }
    }

    fun checkAndRequestPermissions(callback: PermissionCallback) {
        this.permissionCallback = callback

        val permissionsToRequest = allPermissions.filter { permission ->
            ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (permissionsToRequest.isEmpty()) {
            callback.onPermissionsGranted()
        } else {
            requestPermissionLauncher.launch(permissionsToRequest)
        }
    }

    fun areLocationPermissionsGranted(): Boolean {
        return locationPermissions.all { permission ->
            ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun areWifiPermissionsGranted(): Boolean {
        return wifiPermissions.all { permission ->
            ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun areNearbyPermissionsGranted(): Boolean {
        return nearbyPermissions.all { permission ->
            ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun handlePermissionDenial() {
        MaterialAlertDialogBuilder(activity)
            .setTitle("Permissions Requises")
            .setMessage("Cette application nécessite des permissions pour la localisation, le WiFi et les appareils à proximité pour fonctionner correctement. Veuillez les accorder dans les Paramètres.")
            .setPositiveButton("Paramètres") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun openAppSettings() {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", activity.packageName, null)
            activity.startActivity(this)
        }
    }
}
