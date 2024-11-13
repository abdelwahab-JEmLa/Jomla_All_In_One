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

//import A1_MainActivityCompnent.Main.MainScreen
//import B2_StartupAppDisplayerOfNewArticles.Main.StartUpNewArticlesViewModels
//import B2_StartupAppDisplayerOfNewArticles.Main.StartupAppDisplayerOfNewArticles
//import B2_StartupAppDisplayerOfNewArticles.Main.UiState
//import a_RoomDB.AppDatabase
//import a_RoomDB.ArticlesBasesStatsTable
//import android.app.Application
//import android.content.Context
//import android.os.Build
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.viewModels
//import androidx.annotation.RequiresApi
//import androidx.compose.animation.AnimatedVisibility
//import androidx.compose.animation.core.LinearEasing
//import androidx.compose.animation.core.RepeatMode
//import androidx.compose.animation.core.animateFloat
//import androidx.compose.animation.core.infiniteRepeatable
//import androidx.compose.animation.core.rememberInfiniteTransition
//import androidx.compose.animation.core.tween
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.offset
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.CreditScore
//import androidx.compose.material.icons.filled.EditRoad
//import androidx.compose.material.icons.filled.ShoppingCart
//import androidx.compose.material.icons.filled.Visibility
//import androidx.compose.material.icons.filled.VisibilityOff
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.Card
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.Icon
//import androidx.compose.material3.LocalContentColor
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.NavigationBar
//import androidx.compose.material3.NavigationBarItem
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.ProgressIndicatorDefaults
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableIntStateOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.saveable.rememberSaveable
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.alpha
//import androidx.compose.ui.draw.rotate
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.currentBackStackEntryAsState
//import androidx.navigation.compose.rememberNavController
//import androidx.wear.compose.material.ContentAlpha
//import c_WindosBuyAndDesplayeArticleStats.SaleWindows
//import com.google.firebase.FirebaseApp
//import com.google.firebase.database.FirebaseDatabase
//import d_SoldCartScreen.SoldCartScreen
//import e_AiGroupeForSupplier.GenerativeAiScreen
//import e_AiGroupeForSupplier.GenerativeAiViewModel
//import g_DialogeClientsEditer.ClientSelectionDialog
// Application.kt
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
)

// ViewModelFactory.kt
class ViewModelFactory(
    private val context: Context,
    private val database: AppDatabase,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(StartUpNewArticlesViewModels::class.java) ->
                StartUpNewArticlesViewModels(context.applicationContext, database) as T
            modelClass.isAssignableFrom(GenerativeAiViewModel::class.java) ->
                GenerativeAiViewModel() as T
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}

class MainActivity : ComponentActivity() {
    private val database by lazy { (application as MyApplication).database }
    private val permissionHandler by lazy { PermissionHandler(this) }

    private val viewModelFactory by lazy {
        ViewModelFactory(applicationContext, database)
    }

    private val startUpNewArticlesViewModels: StartUpNewArticlesViewModels by viewModels {
        viewModelFactory
    }

    private val generativeAiViewModel: GenerativeAiViewModel by viewModels {
        viewModelFactory
    }

    private val appViewModels by lazy {
        AppViewModels(
            startUpNewArticlesViewModels = startUpNewArticlesViewModels,
            generativeAiViewModel = generativeAiViewModel
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
