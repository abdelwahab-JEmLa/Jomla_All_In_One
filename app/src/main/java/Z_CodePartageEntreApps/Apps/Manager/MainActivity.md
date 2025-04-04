package com.example.abdelwahabjemlajetpack

import W_.Views.FragID1.b2_Edite_Base_Donne_With_Creat_New_Articls.Ancien.ArticleDetailWindow
import W_.Views.FragID1.b2_Edite_Base_Donne_With_Creat_New_Articls.Ancien.MainFragmentEditDatabaseWithCreateNewArticles
import Z_CodePartageEntreApps.Modules.A.Room.AppDatabase
import Z_CodePartageEntreApps.Modules.A.Room.ArticleDao
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_B_MainActivity.Start.MAin.Screen.MainScreen_NewComputerPatterns
import Z_B_MainActivity.Start.appClientRepositoryModule
import Z_B_MainActivity.Start.appManagerRepositoryModule
import Z_B_MainActivity.Start.appModule
import Z_B_MainActivity.Start.isManagerApp
import a_MainAppCompnents.CreatAndEditeInBaseDonnRepositeryModels
import a_MainAppCompnents.DataBaseArticles
import a_MainAppCompnents.HeadOfViewModels
import android.app.Application
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EditRoad
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.material.ContentAlpha
import b_Edite_Base_Donne.EditeBaseDonneViewModel
import c_ManageBonsClients.FragmentManageBonsClients
import com.example.abdelwahabjemlajetpack.ui.theme.AbdelwahabJeMLaJetPackTheme
import com.google.firebase.database.FirebaseDatabase
import d_EntreBonsGro.FragmentEntreBonsGro
import f_credits.CreditsViewModel
import f_credits.FragmentCredits
import g_BoardStatistiques.BoardStatistiquesStatViewModel
import g_BoardStatistiques.CardBoardStatistiques
import g_BoardStatistiques.f_2_CreditsClients.CreditsClientsViewModel
import g_BoardStatistiques.f_2_CreditsClients.FragmentCreditsClients
import h_FactoryClassemntsArticles.ClassementsArticlesViewModel
import h_FactoryClassemntsArticles.MainFactoryClassementsArticles
import i2_FragmentMapArticleInSupplierStore.FragmentMapArticleInSupplierStore
import i_SupplierArticlesRecivedManager.Fragment_SupplierArticlesRecivedManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.context.GlobalContext.startKoin
import java.io.File
import kotlin.random.Random

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

        // Initialize Koin with the appropriate modules
        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(appModule)

            // Conditionally load app-specific modules
            if (isManagerApp(this@MyApplication)) {
                modules(appManagerRepositoryModule)
            } else {
                modules(appClientRepositoryModule)
            }
        }
    }
}

