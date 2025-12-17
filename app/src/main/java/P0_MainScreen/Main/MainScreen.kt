package P0_MainScreen.Main

import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.P.Buttons.BlinkingWarningCard
import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.P.PressistatntMainActivityButtons_Sec8FWinID1
import P0_MainScreen.Modules.HandleFullscreenMode
import P0_MainScreen.Ui.Objects.ConnexionCard
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedActiveValuesFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.M14VentPeriode
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.Repo14VentPeriode
import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.M18CentralParametresOfAllApps
import V.DiviseParSections.App._0.Navigation.AppNavHost
import V.DiviseParSections.App._0.Navigation.NavigationBarWithFab
import V.DiviseParSections.App._0.Navigation.NavigationItems
import V.DiviseParSections.App._0.Navigation.Screen
import Views.FragId4_EStorePresentationToClient.FragmentDisplayeInfoProductToClient7
import Views.FragId4_EStorePresentationToClient.Modules.SearchArticle
import Z_CodePartageEntreApps.Model.A_Produit.Z.Repository.A_ProduitRepository
import Z_CodePartageEntreApps.Modules.FragmentNavigationHandler
import Z_CodePartageEntreApps.Modules.PanelsGroupeButtonHandler
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.clientjetpack.ViewModel.HeadViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: HeadViewModel = koinViewModel(),
    viewModelViewModelInitApp: ViewModelInitApp = koinViewModel(),
    aCentralFacade: ACentralFacade = koinInject(),
    focusedActiveValuesFacade: FocusedActiveValuesFacade = aCentralFacade.focusedActiveValuesFacade,
    repo14VentPeriode: Repo14VentPeriode = aCentralFacade.repositorysMainGetter.repo14VentPeriode,
    repositorysMainSetter: RepositorysMainSetter = aCentralFacade.repositorysMainSetter,
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    panelsGroupeButtonHandler: PanelsGroupeButtonHandler = koinInject()
) {
    val a_ProduitModelRepository = koinInject<A_ProduitRepository>()
    val navigationHandler = koinInject<FragmentNavigationHandler>()
    val repositoryProgress by a_ProduitModelRepository.progressRepo.collectAsState()
    var shouldShowContent by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val headViewModel: HeadViewModel = koinViewModel(parameters = { parametersOf(context) })
    val uiState by headViewModel.uiState.collectAsState()
    val productDisplayController = uiState.productDisplayController

    // State for confirmation dialog
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var isUpdating by remember { mutableStateOf(false) }
    var updateCountdown by remember { mutableStateOf(5) }
    var isCheckingFirebase by remember { mutableStateOf(false) }

// Add this enhanced logging section right after the targetedPeriodDoitEtreDon is defined:

    // Check for M14VentPeriode with abdelmounen_Doit_Etre_Ici = true
    val targetedPeriodDoitEtreDon = repo14VentPeriode.datasValue.find {
        it.abdelmounen_Doit_Etre_Ici
    }

    // Enhanced logging for warning dialog debugging
    LaunchedEffect(targetedPeriodDoitEtreDon, focusedValuesGetter.currentActive_M9AppCompt) {
        val TAG_WARNING = "WarningDialogDebug"

        Log.d(TAG_WARNING, "=== Warning Dialog State Check ===")
        Log.d(TAG_WARNING, "targetedPeriodDoitEtreDon: ${targetedPeriodDoitEtreDon?.keyID ?: "NULL"}")
        Log.d(TAG_WARNING, "targetedPeriodDoitEtreDon.abdelmounen_Doit_Etre_Ici: ${targetedPeriodDoitEtreDon?.abdelmounen_Doit_Etre_Ici}")

        Log.d(TAG_WARNING, "currentActive_M9AppCompt.keyID: ${focusedValuesGetter.currentActive_M9AppCompt?.keyID ?: "NULL"}")
        Log.d(TAG_WARNING, "abdelmomen_Compt_KeyId from M18: ${M18CentralParametresOfAllApps().abdelmomen_Compt_KeyId}")

        val isComptMatch = (focusedValuesGetter.currentActive_M9AppCompt?.keyID ?: "") == M18CentralParametresOfAllApps().abdelmomen_Compt_KeyId
        Log.d(TAG_WARNING, "Is Compt Match: $isComptMatch")

        val shouldShowWarning = targetedPeriodDoitEtreDon != null && isComptMatch
        Log.d(TAG_WARNING, "Should Show Warning: $shouldShowWarning")

        Log.d(TAG_WARNING, "shouldShowContent: $shouldShowContent")

        // Log all periods in repo
        Log.d(TAG_WARNING, "All periods in repo14VentPeriode:")
        repo14VentPeriode.datasValue.forEachIndexed { index, period ->
            Log.d(TAG_WARNING, "  Period $index: keyID=${period.keyID}, abdelmounen_Doit_Etre_Ici=${period.abdelmounen_Doit_Etre_Ici}")
        }

        Log.d(TAG_WARNING, "================================")
    }

    val shouldWarningChangePeriodVent = remember(targetedPeriodDoitEtreDon, focusedValuesGetter.currentActive_M9AppCompt) {
        val result = targetedPeriodDoitEtreDon != null &&
                (focusedValuesGetter.currentActive_M9AppCompt?.keyID ?: "") == M18CentralParametresOfAllApps().abdelmomen_Compt_KeyId

        Log.d("WarningDialogDebug", "shouldWarningChangePeriodVent computed: $result")
        result
    }

// Also update the initial load LaunchedEffect to include better logging:

    LaunchedEffect(repositoryProgress) {
        headViewModel.updateLoadingProgress((repositoryProgress * 100))

        val TAG = "id1"
        if (repositoryProgress >= 0.995f) {
            Log.d(TAG, "Repository considered loaded at: ${repositoryProgress * 100}%")

            // Check Firebase immediately when repository loads
            isCheckingFirebase = true
            try {
                val snapshot = M14VentPeriode.ref.get().await()
                var foundFlaggedPeriod = false

                Log.d(TAG, "Checking ${snapshot.childrenCount} periods from Firebase")

                snapshot.children.forEach { childSnapshot ->
                    val period = childSnapshot.getValue(M14VentPeriode::class.java)
                    Log.d(TAG, "Firebase Period: keyID=${period?.keyID}, flag=${period?.abdelmounen_Doit_Etre_Ici}")

                    if (period?.abdelmounen_Doit_Etre_Ici == true) {
                        Log.d(TAG, "✓ Found period with flag during initial load: ${period.keyID}")
                        foundFlaggedPeriod = true

                        // Update local data if Firebase has the flag
                        val localPeriod = repo14VentPeriode.datasValue.find {
                            it.keyID == period.keyID
                        }

                        if (localPeriod == null) {
                            Log.d(TAG, "⚠ Period ${period.keyID} NOT found in local repo, refreshing...")
                            repo14VentPeriode.refresh_Datas()
                        } else if (!localPeriod.abdelmounen_Doit_Etre_Ici) {
                            Log.d(TAG, "⚠ Local period ${period.keyID} has flag=false, refreshing...")
                            repo14VentPeriode.refresh_Datas()
                        } else {
                            Log.d(TAG, "✓ Local period ${period.keyID} already has flag=true")
                        }
                    }
                }

                if (!foundFlaggedPeriod) {
                    Log.d(TAG, "ℹ No flagged periods found during initial load")
                }

            } catch (e: Exception) {
                Log.e(TAG, "❌ Error checking Firebase during initial load: ${e.message}", e)
            } finally {
                isCheckingFirebase = false
            }

            shouldShowContent = true
            Log.d(TAG, "✓ Content display enabled")
        } else {
            Log.w(
                TAG,
                "⏳ UI waiting for repository to load. Current progress: ${repositoryProgress * 100}%"
            )
            shouldShowContent = false
        }
    }

    // Verify from Firebase periodically
    LaunchedEffect(shouldShowContent) {
        if (shouldShowContent) {
            while (true) {
                isCheckingFirebase = true
                try {
                    // Check Firebase for abdelmounen_Doit_Etre_Ici = true
                    val snapshot = M14VentPeriode.ref.get().await()

                    snapshot.children.forEach { childSnapshot ->
                        val period = childSnapshot.getValue(M14VentPeriode::class.java)
                        if (period?.abdelmounen_Doit_Etre_Ici == true) {
                            Log.d("FirebaseCheck", "Found period with flag: ${period.keyID}")

                            // Update local data if Firebase has the flag
                            val localPeriod = repo14VentPeriode.datasValue.find {
                                it.keyID == period.keyID
                            }

                            if (localPeriod == null || !localPeriod.abdelmounen_Doit_Etre_Ici) {
                                Log.d("FirebaseCheck", "Local data outdated, refreshing...")
                                repo14VentPeriode.refresh_Datas()
                            }
                        }
                    }

                    isCheckingFirebase = false
                    Log.d("FirebaseCheck", "Firebase verification completed successfully")

                } catch (e: Exception) {
                    Log.e("FirebaseCheck", "Error checking Firebase: ${e.message}")
                    isCheckingFirebase = false
                }

                // Check every 30 seconds
                kotlinx.coroutines.delay(30000)
            }
        }
    }

    // Handle countdown and refresh
    LaunchedEffect(isUpdating) {
        if (isUpdating) {
            for (i in 5 downTo 1) {
                updateCountdown = i
                kotlinx.coroutines.delay(1000)
            }
            // Refresh data after countdown
            repo14VentPeriode.refresh_Datas()
            isUpdating = false
            updateCountdown = 5
        }
    }

    // Handle fullscreen mode
    HandleFullscreenMode(productDisplayController)

    // Navigation setup
    val navController = rememberNavController()
    val items = NavigationItems.getItems()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    // State management
    var isNavBarVisible by remember { mutableStateOf(true) }
    var isFabVisible by remember { mutableStateOf(false) }
    var isDisplayedConnexionWifiVisible by remember { mutableStateOf(false) }
    var showProductDisplay by remember { mutableStateOf(false) }
    var lockHost by remember { mutableStateOf(false) }
    val targetCategoryId = remember { mutableStateOf<Long?>(null) }

    var isControleFabVisible by remember { mutableStateOf(M18CentralParametresOfAllApps().isControleFabVisible) }
    var isProcessingUpdate by remember { mutableStateOf(false) }

    LaunchedEffect(productDisplayController.clientWindowsDisplayedProductId) {
        showProductDisplay = productDisplayController.clientWindowsDisplayedProductId != null

        if (productDisplayController.clientWindowsDisplayedProductId == null
            && productDisplayController.isHostPhone
            && currentRoute != Screen.FacadePresentoireProduits.route
            && navController.currentDestination != null
        ) {
            navController.navigate(Screen.FacadePresentoireProduits.route) {

            }
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            val currentAppCompt = viewModel.getter.repo9AppCompt.currentAppCompt
            val hideAppScreen = currentAppCompt?.hideAppScreen ?: false

            // Show loading or warning screen
            if (!shouldShowContent || shouldWarningChangePeriodVent) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (shouldWarningChangePeriodVent) {
                        // Warning screen for period change
                        Box(
                            modifier = Modifier
                                .semantics(mergeDescendants = true) {
                                    set(
                                        value = focusedValuesGetter.currentActive_M9AppCompt,
                                        key = SemanticsPropertyKey("currentActive_M9AppCompt")
                                    )
                                }
                                .fillMaxSize()
                                .clickable {
                                    if (!isUpdating) {
                                        showConfirmationDialog = true
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isUpdating) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(64.dp),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "جاري التحديث... ($updateCountdown)",
                                        style = MaterialTheme.typography.headlineSmall,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(top = 16.dp)
                                    )
                                    Text(
                                        text = "Updating... ($updateCountdown)",
                                        style = MaterialTheme.typography.bodyLarge,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            } else {

                                BlinkingWarningCard(
                                    "التطبيق ليس في الفترة المحددة للبيع اضغط للتحديث ${
                                        targetedPeriodDoitEtreDon?.keyID?.takeLast(3)
                                    }"
                                )
                            }
                        }
                    } else {
                        // Loading screen
                        CircularProgressIndicator(
                            progress = { repositoryProgress },
                            modifier = Modifier.size(64.dp),
                            trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            } else {
                // Main content - only display when repository is loaded and no warning
                val isHostPhone = productDisplayController.isHostPhone

                if (hideAppScreen) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Build,
                                contentDescription = "Maintenance",
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "التطبيق في طور الاصلاحات",
                                style = MaterialTheme.typography.headlineMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                            Text(
                                text = "The application is under maintenance",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                } else {
                    Column(modifier = Modifier.fillMaxSize()) {

                        AnimatedVisibility(
                            visible = isDisplayedConnexionWifiVisible || (!productDisplayController.isConnected && !lockHost
                                    && !focusedActiveValuesFacade.focusedValuesGetter.currentApp_ItsWorkChezGrossisst)
                        ) {
                            ConnexionCard(
                                headViewModel = headViewModel,
                                productDisplayController = productDisplayController,
                                onClickToStartAsClient = {
                                    isNavBarVisible = false
                                    isFabVisible = false
                                },
                                lockHost = lockHost
                            )
                        }

                        LaunchedEffect(Unit) {
                            navigationHandler.setNavController(navController)
                        }

                        Box(modifier = Modifier.weight(1f)) {

                            AppNavHost(
                                modifier = Modifier.fillMaxSize(),
                                viewModel = headViewModel,
                                viewModelInitApp = viewModelViewModelInitApp,
                                navController = navController,
                                onToggleNavBar = { isNavBarVisible = !isNavBarVisible },
                                isFabVisible = isFabVisible,
                                onClickToDisplayeConexionWifi = {
                                    isDisplayedConnexionWifiVisible =
                                        !isDisplayedConnexionWifiVisible
                                },
                                onToggleLockHost = { lockHost = !lockHost },
                                targetCategoryId = targetCategoryId,
                                lockHost = isHostPhone,
                                onClickImageToShowControles = {
                                    isControleFabVisible = !isControleFabVisible
                                }
                            )

                            if (!isHostPhone && productDisplayController.isConnected) {
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clickable(enabled = false) { }
                                )
                            }
                        }
                    }
                }

                AnimatedVisibility(
                    visible = (isHostPhone || !productDisplayController.isConnected) && shouldShowContent && !hideAppScreen,
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    NavigationBarWithFab(
                        items = items.filter { it != Screen.ToggleFab },
                        currentRoute = currentRoute,
                        onNavigate = { route ->
                            navController.navigate(route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        isFabVisible = isFabVisible,
                        onToggleFabVisibility = {
                            isFabVisible = !isFabVisible
                            isDisplayedConnexionWifiVisible = false
                        },
                        onCatalogSelected = {
                            targetCategoryId.value = it
                        },
                        modifier = Modifier.padding(bottom = 2.dp),
                        viewModelInitApp = viewModelViewModelInitApp
                    )
                }

                if (showProductDisplay && shouldShowContent && !hideAppScreen) {
                    val productId = productDisplayController.clientWindowsDisplayedProductId
                    val displayProductDataBase = productId?.let { id ->
                        uiState.articlesBasesStatTables.find { it.id.toLong() == id }
                    }

                    if (displayProductDataBase != null) {
                        FragmentDisplayeInfoProductToClient7(
                            displayController = productDisplayController,
                            articleStatsDataBase = displayProductDataBase,
                            colorsArticlesList = uiState.colorsArticlesTabelleModel,
                            reloadTrigger = 0,
                            modifier = Modifier.fillMaxSize(),
                            viewModelInitApp = viewModelViewModelInitApp
                        )
                    }
                }

                if (productDisplayController.searchWindowsDisplaye.isNotEmpty()
                    && shouldShowContent && !hideAppScreen
                ) {
                    SearchArticle(
                        dsipayeText = productDisplayController.searchWindowsDisplaye
                    )
                }

                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { uiState.loadingProgress / 100f },
                            modifier = Modifier.size(48.dp),
                            trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                if (isControleFabVisible) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .zIndex(10f)
                    ) {
                        panelsGroupeButtonHandler.GroupeButtonsActivePanelsWindows()
                        panelsGroupeButtonHandler.AfficheDialogesHeadApps()
                    }
                }

                if (isHostPhone && shouldShowContent && !hideAppScreen) {
                    PressistatntMainActivityButtons_Sec8FWinID1()
                }

                focusedActiveValuesFacade.focusedValuesGetter.currentActive_M9AppCompt?.let {
                    (!productDisplayController.isHostPhone && productDisplayController.isConnected).ifTrue {
                        App_PresenterEcran_Au_Client()
                    }
                }
            }

            // Confirmation Dialog
            if (showConfirmationDialog) {
                AlertDialog(
                    onDismissRequest = { showConfirmationDialog = false },
                    title = {
                        Text(
                            text = "تأكيد التحديث - Confirm Update",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    text = {
                        Column {
                            Text(
                                text = "هل قمت بمحو وتحديث التطبيق؟",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "Did you clear and update the application?",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            Text(
                                text = "سيتم تحديث البيانات وإعادة تحميلها بعد 5 ثواني",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = "Data will be updated and reloaded after 5 seconds",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    },
                    confirmButton = {
                        androidx.compose.material3.Button(
                            onClick = {
                                targetedPeriodDoitEtreDon?.let { period ->
                                    focusedValuesGetter.currentActive_M9AppCompt?.let { appCompt ->
                                        isProcessingUpdate = true

                                        // Launch coroutine to handle update with delay
                                        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                                            try {
                                                // Update current AppCompt with new period
                                                aCentralFacade.repositorysMainSetter.update_M9AppCompt(
                                                    appCompt.copy(
                                                        current_OnVent_M14VentPeriode_KeyID = period.keyID,
                                                    )
                                                )

                                                // Reset all periods' abdelmounen_Doit_Etre_Ici flag
                                                repo14VentPeriode.datasValue.forEach { ventPeriode ->
                                                    repositorysMainSetter.update_M14VentPeriode(
                                                        ventPeriode.copy(
                                                            abdelmounen_Doit_Etre_Ici = false
                                                        )
                                                    )
                                                }

                                                // Wait 5 seconds for task to complete
                                                kotlinx.coroutines.delay(5000)

                                            } finally {
                                                // Close dialog and start refresh countdown
                                                isProcessingUpdate = false
                                                showConfirmationDialog = false
                                                isUpdating = true
                                            }
                                        }
                                    }
                                }
                            },
                            enabled = !isProcessingUpdate
                        ) {
                            if (isProcessingUpdate) {
                                androidx.compose.material3.CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("نعم - Yes")
                            }
                        }
                    },
                    dismissButton = {
                        androidx.compose.material3.TextButton(
                            onClick = { showConfirmationDialog = false },
                            enabled = !isProcessingUpdate
                        ) {
                            Text("لا - No")
                        }
                    }
                )
            }
        }
    }
}
