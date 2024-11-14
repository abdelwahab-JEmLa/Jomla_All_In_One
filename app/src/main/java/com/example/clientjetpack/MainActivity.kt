package com.example.clientjetpack

import P0_MainScreen.MainScreen
import P6_AiGroupeForSupplier.GenerativeAiViewModel
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
import com.example.clientjetpack.Modules.AppDatabase
import com.example.clientjetpack.Modules.PermissionHandler
import com.example.clientjetpack.ViewModel.HeadViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

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
    val headViewModel: HeadViewModel,
    val generativeAiViewModel: GenerativeAiViewModel,
)


// ViewModelFactory.kt
class ViewModelFactory(
    private val context: Context,
    private val database: AppDatabase,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HeadViewModel::class.java) ->
                HeadViewModel(
                    context.applicationContext,
                    database,
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
    private val headViewModel: HeadViewModel by viewModels { viewModelFactory }
    private val generativeAiViewModel: GenerativeAiViewModel by viewModels { viewModelFactory }

    private val appViewModels by lazy {
        AppViewModels(
            headViewModel = headViewModel,
            generativeAiViewModel = generativeAiViewModel,
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
