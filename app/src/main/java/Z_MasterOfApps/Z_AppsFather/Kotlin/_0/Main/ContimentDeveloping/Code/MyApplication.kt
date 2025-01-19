package Z_MasterOfApps.Z_AppsFather.Kotlin._0.Main.ContimentDeveloping.Code

import Z_MasterOfApps.Z_AppsFather.Kotlin._3.Init.A_LoadFireBase.FirebaseOfflineHandler
import android.app.Application
import com.google.firebase.FirebaseApp

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)?.let { app ->
            FirebaseOfflineHandler.initializeFirebase(app)
        }
    }
}
