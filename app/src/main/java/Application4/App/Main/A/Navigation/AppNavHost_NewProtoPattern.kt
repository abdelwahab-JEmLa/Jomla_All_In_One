package Application4.App.Main.A.Navigation

import Application4.App.A.Start.Init.Proto.A_LoadingApp4_Init_Screen
import Application4.App.Fragment.ID1.Fragment.A_Compact_Presentoire_App_Produits_App4
import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import Application4.App.Fragment.ID2.Fragment.Screen_Panie_FragID2
import Application4.App.Main.A.Navigation.Component.FragmentNavigationHandler_NewProto
import Application4.App.Main.A.Navigation.Component.Screen_NewProtoPattern
import EntreApps.Shared.Models.Z_AppCompt
import EntreApps.Shared.Modules.Base.AppDatabase
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_MapClients_A2FragID_1
import Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin.centralDataBasesModule
import Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin.classesHandlersModule
import Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin.composRepositorysModule
import Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin.factoryDataBaseProtoAvantJuin3Module
import Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin.viewModelModule
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.core.context.GlobalContext
import java.util.concurrent.atomic.AtomicBoolean

private val heavyModules = listOf(
    viewModelModule,
    centralDataBasesModule,
    composRepositorysModule,
    factoryDataBaseProtoAvantJuin3Module,
    classesHandlersModule,
)

private val heavyModulesLoaded = AtomicBoolean(false)

@SuppressLint("RememberReturnType")
@Composable
fun AppNavHost_NewProtoPattern(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    innerPadding: PaddingValues = PaddingValues(),
    fragmentNavigationHandler: FragmentNavigationHandler_NewProto = koinInject(),
    viewModelNewProtoPatterns: A_ViewModel_NewProtoPatterns,
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    LaunchedEffect(currentRoute) {
        fragmentNavigationHandler.updateCurrentFragmentByRoute(currentRoute)
    }

    // State flag: true only after heavy modules are confirmed loaded in Koin
    val heavyReady = androidx.compose.runtime.remember { mutableStateOf(heavyModulesLoaded.get()) }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen_NewProtoPattern.Compact_Presentoire_App_Produits_FragID4.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(route = Screen_NewProtoPattern.Compact_Presentoire_App_Produits_FragID4.route) {
                LaunchedEffect(Unit) {
                    if (heavyModulesLoaded.get()) {
                        runCatching { GlobalContext.get().unloadModules(heavyModules) }
                            .onSuccess {
                                heavyModulesLoaded.set(false)
                                heavyReady.value = false
                            }
                    }
                }

                val appDb: AppDatabase = koinInject()
                val appComptList by appDb.dao_M9AppCompt()
                    .getBy_M00_Lence_Key_Flow()
                    .collectAsState(initial = emptyList())

                val lastSyncTimestamp = appComptList.firstOrNull()?.dernierTimeTampsSynchronisationAvecFireBase

                var initDone by rememberSaveable { mutableStateOf(false) }
                LaunchedEffect(lastSyncTimestamp) {
                    if (lastSyncTimestamp != null) {
                        initDone = false
                    }
                }

                // Firebase → Room alive listener: syncs Z_AppCompt ref changes into local DB
                DisposableEffect(Unit) {
                    val listener = object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            snapshot.children.forEach { child ->
                                val updated = child.getValue(Z_AppCompt::class.java) ?: return@forEach
                                CoroutineScope(Dispatchers.IO).launch {
                                    appDb.dao_M9AppCompt().upsert(updated)
                                }
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                            Log.w("Firebase", "Z_AppCompt listener cancelled", error.toException())
                        }
                    }
                    Z_AppCompt.ref.addValueEventListener(listener)
                    onDispose {
                        Z_AppCompt.ref.removeEventListener(listener)
                    }
                }

                if (!initDone) {
                    A_LoadingApp4_Init_Screen(
                        innerPadding = innerPadding,
                        onInitDone = { initDone = true },
                        appDatabase = appDb
                    )
                } else {
                    A_Compact_Presentoire_App_Produits_App4()
                }
            }

            composable(route = Screen_NewProtoPattern.Panier.route) {
                LaunchedEffect(Unit) {
                    if (!heavyModulesLoaded.get()) {
                        runCatching { GlobalContext.get().loadModules(heavyModules) }
                            .onSuccess {
                                heavyModulesLoaded.set(true)
                                heavyReady.value = true
                            }
                    } else {
                        heavyReady.value = true
                    }
                }
                if (heavyReady.value) {
                    Screen_Panie_FragID2()
                }
            }

            composable(route = Screen_NewProtoPattern.A_Clients_LocationGps.route) {
                LaunchedEffect(Unit) {
                    if (!heavyModulesLoaded.get()) {
                        runCatching { GlobalContext.get().loadModules(heavyModules) }
                            .onSuccess {
                                heavyModulesLoaded.set(true)
                                heavyReady.value = true
                            }
                    } else {
                        heavyReady.value = true
                    }
                }
                if (heavyReady.value) {
                    A_MapClients_A2FragID_1(
                        onUpdateLongAppSetting = {
                            fragmentNavigationHandler.navigateTo(
                                Screen_NewProtoPattern.Compact_Presentoire_App_Produits_FragID4.route
                            )
                        },
                        onClear = {}
                    )
                }
            }
        }
    }
}
