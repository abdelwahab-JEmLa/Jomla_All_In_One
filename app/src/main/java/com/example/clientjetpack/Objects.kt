package com.example.abdelwahabjemlajetpack

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat


class PermissionHandler(private val activity: ComponentActivity) {



    init {
        initializeLaunchers()
    }

    private fun initializeLaunchers() {

}
