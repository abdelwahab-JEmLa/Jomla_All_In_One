package com.example.clientjetpack

import Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin.appModule
import android.app.Application
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        try {
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
            modules(appModule)
        }
    }
}
