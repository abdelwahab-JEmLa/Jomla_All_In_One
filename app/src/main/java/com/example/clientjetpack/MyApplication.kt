package com.example.clientjetpack

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.PeriodeVente
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.PeriodeVenteRepository
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Produit
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Vendeur
import Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin.appManagerModules
import Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin.appModule
import Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin.isManagerApp
import Z_MasterOfApps.A.MainActivity.Start.Module.A.Koin.appClientModules
import android.app.Application
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MyApplication : Application() {

    // Create a module for the repository
    private val databaseModule = module {
        single {
            val config = RealmConfiguration.Builder(
                schema = setOf(
                    PeriodeVente::class, Vendeur::class, Produit::class
                )
            )
                .name("ventesDatabase.realm")
                .schemaVersion(1)
                .build()

            Realm.open(config)
        }

        // Provide the PeriodeVenteRepository
        single { PeriodeVenteRepository(get()) }
    }

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
            modules(appModule, databaseModule) // Add the database module here

            // Conditionally load app-specific modules
            if (isManagerApp(this@MyApplication)) {
                modules(appManagerModules)
            } else {
                modules(appClientModules)
            }
        }
    }
}
