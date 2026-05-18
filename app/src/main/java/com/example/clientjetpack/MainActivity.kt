package com.example.clientjetpack

import A_Main.Shared.Init.A_LoadingApp4_Init_Screen
import Application2.App.App.appModule_App2_ac_app1
import Application2.App.Fragment.Compact_Presentoire_App_Produits_App2
import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import Application4.App.Main.A.Navigation.Component.FragmentNavigationHandler_NewProto
import Application4.App.Modules.Wi.Module.WifiTransferDatas_ControllerApp
import Application4.App.Screen.MainScreen_NewProtoPattern
import EntreApps.Shared.Models.AppType
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Modules.Base.AppDatabase
import EntreApps.Shared.Modules.Base.PermissionHandler
import EntreApps.Shared.Modules.Base.StoragePermissionDialog
import EntreApps.Shared.Modules.Base.modules_NewProtoPatterns
import P0_MainScreen.Main.MainScreen_All
import Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin.appModule
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.clientjetpack.ui.theme.ClientJetPackTheme
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.compose.koinInject
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    lateinit var content: () -> Unit
    private val permissionHandler by lazy { PermissionHandler(this) }
    private var permissionsChecked by mutableStateOf(false)
    private var showStorageDialog by mutableStateOf(false)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ startKoin appelé UNE SEULE FOIS ici, avant setContent
        startKoin {
            androidLogger()
            androidContext(this@MainActivity)
            modules(modules_NewProtoPatterns)

            when (M00CentralParametresOfAllApps.get_Default().its_AppType) {
                AppType.JomLaElectroLivreurGrossist_PresenterScreen ->
                    if (M00CentralParametresOfAllApps().load_All_modules)
                        modules(appModule) else
                        modules(appModule_App2_ac_app1)

                AppType.JomLaElectroLivreurGrossist_VendeurHost -> {}
                else ->
                    modules(appModule)
            }
        }

        setupActivityContent()
    }

    @SuppressLint("ObsoleteSdkInt")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onResume() {
        super.onResume()

        val hasStorageAccess = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
        }

        if (hasStorageAccess && showStorageDialog) {
            showStorageDialog = false
            permissionsChecked = true
        }

        if (!permissionsChecked && hasStorageAccess) {
            handlePermissions()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @OptIn(KoinExperimentalAPI::class)
    private fun setupActivityContent() {
        runCatching {
            setContent {
                ClientJetPackTheme {
                    KoinAndroidContext {
                        Box(modifier = Modifier.fillMaxSize()) {

                            if (permissionsChecked) {
                                var initDone by rememberSaveable { mutableStateOf(false) }

                                if (!initDone) {
                                    A_LoadingApp4_Init_Screen(
                                        innerPadding = PaddingValues(),
                                        onInitDone = { initDone = true },
                                        appDatabase = koinInject()
                                    )
                                } else {
                                    val context = LocalContext.current
                                    val appDatabase = koinInject<AppDatabase>()
                                    val fragmentNavigationHandler_passed =
                                        koinInject<FragmentNavigationHandler_NewProto>()

                                    val scope = rememberCoroutineScope()

                                    val list_M1Produit by appDatabase.dao_M1Produit().getAllFlow()
                                        .collectAsState(initial = emptyList())
                                    val list_M3CouleurProduit by appDatabase.dao_M03CouleurProduitInfos().getAllFlow()
                                        .collectAsState(initial = emptyList())

                                    val wifiTransferDatas_ControllerApp = remember {
                                        WifiTransferDatas_ControllerApp(
                                            context = context,
                                            coroutineScope = scope,
                                            list_M1Produit = list_M1Produit,
                                            list_M3CouleurProduit = list_M3CouleurProduit,
                                        )
                                    }

                                    SideEffect {
                                        wifiTransferDatas_ControllerApp.list_M1Produit = list_M1Produit
                                        wifiTransferDatas_ControllerApp.list_M3CouleurProduit = list_M3CouleurProduit
                                    }

                                    when (M00CentralParametresOfAllApps.get_Default().its_AppType) {
                                        AppType.JomLaElectroLivreurGrossist_PresenterScreen -> {
                                            Compact_Presentoire_App_Produits_App2()
                                        }

                                        AppType.JomLaElectroLivreurGrossist_VendeurHost -> {
                                            MainScreen_NewProtoPattern(
                                                appDatabase=appDatabase,
                                                wifiTransferDatas_ControllerApp=wifiTransferDatas_ControllerApp,
                                                fragmentNavigationHandler_passed=fragmentNavigationHandler_passed,
                                                on_update_M13TarificationInfos_par_ecriture = {},
                                                on_clear_wifi_classe_cache= {
                                                    wifiTransferDatas_ControllerApp.cancel()
                                                },
                                            )
                                        }

                                        else -> {
                                            val appDatabase = koinInject<AppDatabase>()
                                            val fragmentNavigationHandler =
                                                koinInject<FragmentNavigationHandler_NewProto>()

                                            val viewModelNewProtoPatterns: A_ViewModel_NewProtoPatterns =
                                                viewModel(
                                                    factory = viewModelFactory {
                                                        initializer {
                                                            A_ViewModel_NewProtoPatterns(
                                                                wifiTransferDatas_ControllerApp=wifiTransferDatas_ControllerApp,
                                                                context = context,
                                                                appDatabase = appDatabase,
                                                                fragmentNavigationHandler = fragmentNavigationHandler,
                                                            )
                                                        }
                                                    }
                                                )
                                            MainScreen_All(
                                                fragmentNavigationHandler_passed = fragmentNavigationHandler,
                                                viewModelNewProtoPatterns = viewModelNewProtoPatterns,
                                                wifiTransferDatas_ControllerApp=wifiTransferDatas_ControllerApp,
                                                appDatabase=appDatabase,
                                                on_update_M13TarificationInfos_par_ecriture={}
                                            )
                                        }
                                    }
                                }
                                if (showStorageDialog) {
                                    StoragePermissionDialog(
                                        onOpenSettings = { permissionHandler.openStorageSettings() },
                                        onDismiss = {
                                            showStorageDialog = false
                                            permissionsChecked = true
                                            showPermissionDeniedMessage()
                                        }
                                    )
                                }
                            }

                        }
                    }
                }
            }

            handlePermissions()
        }.onFailure {
            Toast.makeText(
                this,
                "Une erreur s'est produite. Veuillez redémarrer l'application.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun handlePermissions() {
        if (permissionHandler.arePermissionsGranted()) {
            permissionsChecked = true
            return
        }

        permissionHandler.checkAndRequestPermissions(object : PermissionHandler.PermissionCallback {
            override fun onPermissionsGranted() {
                permissionsChecked = true
                showStorageDialog = false
                Toast.makeText(
                    this@MainActivity,
                    "✓ جميع الأذونات ممنوحة - الصور ستظهر الآن",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onPermissionsDenied() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
                    showStorageDialog = true
                    return
                }
                showPermissionDeniedMessage()
                permissionsChecked = true
            }

            override fun onPermissionRationale(permissions: Array<String>) {}
        })
    }

    private fun showPermissionDeniedMessage() {
        val message =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
                "⚠ الصور لن تظهر بدون إذن الوصول للملفات. يمكنك منح الإذن من الإعدادات لاحقاً"
            } else {
                "⚠ بعض الوظائف محدودة بدون الأذونات الكاملة"
            }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
