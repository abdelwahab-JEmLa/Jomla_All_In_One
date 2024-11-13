package com.example.clientjetpack

import A1_MainActivityCompnent.Main.MainScreen
import A1_MainActivityCompnent.Main.StartUpNewArticlesViewModels
import a_RoomDB.AppDatabase
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import e_AiGroupeForSupplier.GenerativeAiViewModel
import f_Wifi.Main.ClientPresentationViewModel

class MyApplication : Application() {
    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.DatabaseModule.getDatabase(this)
        FirebaseApp.initializeApp(this)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}

data class AppViewModels(
    val startUpNewArticlesViewModels: StartUpNewArticlesViewModels,
    val generativeAiViewModel: GenerativeAiViewModel,
    val clientPresentationViewModel: ClientPresentationViewModel,
)


// ViewModelFactory.kt
class ViewModelFactory(
    private val context: Context,
    private val database: AppDatabase,
) : ViewModelProvider.Factory {
    private var clientPresentationViewModelInstance: ClientPresentationViewModel? = null

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ClientPresentationViewModel::class.java) -> {
                clientPresentationViewModelInstance = ClientPresentationViewModel( context.applicationContext,)
                clientPresentationViewModelInstance as T
            }
            modelClass.isAssignableFrom(StartUpNewArticlesViewModels::class.java) ->
                StartUpNewArticlesViewModels(
                    context.applicationContext,
                    database,
                    clientPresentationViewModelInstance
                ) as T
            modelClass.isAssignableFrom(GenerativeAiViewModel::class.java) ->
                GenerativeAiViewModel() as T
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}

class MainActivity : ComponentActivity() {
    private val database by lazy { (application as MyApplication).database }
    private val permissionHandler by lazy { PermissionHandler(this) }
    private val viewModelFactory by lazy { ViewModelFactory(applicationContext, database) }
    private val startUpNewArticlesViewModels: StartUpNewArticlesViewModels by viewModels { viewModelFactory }
    private val generativeAiViewModel: GenerativeAiViewModel by viewModels { viewModelFactory }
    private val clientPresentationViewModel: ClientPresentationViewModel by viewModels { viewModelFactory }

    private val appViewModels by lazy {
        AppViewModels(
            startUpNewArticlesViewModels = startUpNewArticlesViewModels,
            generativeAiViewModel = generativeAiViewModel,
            clientPresentationViewModel=clientPresentationViewModel
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        permissionHandler.checkAndRequestPermissions(object : PermissionHandler.PermissionCallback {
            @RequiresApi(Build.VERSION_CODES.Q)
            override fun onPermissionsGranted() {
                setContent {
                    MainScreen(appViewModels)
                }
            }

            @RequiresApi(Build.VERSION_CODES.Q)
            override fun onPermissionsDenied() {}

            override fun onPermissionRationale(permissions: Array<String>) {}
        })
    }
}
