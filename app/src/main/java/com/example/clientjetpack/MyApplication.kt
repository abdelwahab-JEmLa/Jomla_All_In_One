package com.example.clientjetpack

import Z_CodePartageEntreApps.DataBase.Koin.appManagerModules
import Z_CodePartageEntreApps.DataBase.Koin.appModule
import Z_CodePartageEntreApps.DataBase.Koin.isManagerApp
import Z_MasterOfApps.A.MainActivity.Start.Module.A.Koin.appClientModules
import android.app.Application
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Configure Firebase FIRST before any other Firebase operations
        try {
            // Get reference to database ONCE and configure it
            FirebaseDatabase.getInstance().apply {
                setPersistenceEnabled(true)
                setPersistenceCacheSizeBytes(100L * 1024L * 1024L) // 100MB
            }
        } catch (e: Exception) {
            Log.e("Firebase", "Initialization error: ${e.message}")
        }

        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(appModule) // Add viewModelModule here

            // Conditionally load app-specific modules
            if (isManagerApp(this@MyApplication)) {
                modules(appManagerModules)
            } else {
                modules(appClientModules)
            }
        }
    }
}
