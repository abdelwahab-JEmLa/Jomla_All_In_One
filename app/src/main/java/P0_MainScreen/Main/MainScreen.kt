package P0_MainScreen.Main

import P0_MainScreen.Main.Windows.RecordAfficheurFAB
import P0_MainScreen.Modules.HandleFullscreenMode
import P0_MainScreen.Ui.Objects.ConnexionCard
import V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.A_APP4FragID1_MainScreen
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.clientjetpack.A_OptionsControlsButtons_A1FragID_3
import com.example.clientjetpack.ViewModel.HeadViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModelInitApp: ViewModelInitApp = koinViewModel(),
    xmlResources: List<Pair<String, Int>>? = null,
    panelsGroupeButtonHandler: PanelsGroupeButtonHandler = koinInject()
    ) {
    var showVendeursDialog by remember { mutableStateOf(false) }

    val a_ProduitModelRepository = koinInject<A_ProduitRepository>()
    // Use koinInject to get the FragmentNavigationHandler
    val navigationHandler = koinInject<FragmentNavigationHandler>()

    // Track the repository progress
    val repositoryProgress by a_ProduitModelRepository.progressRepo.collectAsState()

    // Additional state to track if we should show the UI
    var shouldShowContent by remember { mutableStateOf(false) }

    val context = LocalContext.current
    // Get the ViewModel with the context parameter
    val headViewModel: HeadViewModel = koinViewModel(parameters = { parametersOf(context) })

    val uiState by headViewModel.uiState.collectAsState()
    val productDisplayController = uiState.productDisplayController

    LaunchedEffect(repositoryProgress) {
        headViewModel.updateLoadingProgress((repositoryProgress * 100))

        val TAG = "id1"
        if (repositoryProgress >= 0.995f) {
            Log.d(TAG, "Repository considered loaded at: ${repositoryProgress * 100}%")
            shouldShowContent = true
        } else {
            Log.w(
                TAG,
                "UI waiting for repository to load. Current progress: ${repositoryProgress * 100}%"
            )
            shouldShowContent = false
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

    LaunchedEffect(productDisplayController.clientWindowsDisplayedProductId) {
        showProductDisplay = productDisplayController.clientWindowsDisplayedProductId != null

        // Only navigate if needed, and don't force back to start every time
        if (productDisplayController.clientWindowsDisplayedProductId == null
            && productDisplayController.isHostPhone
            && currentRoute != Screen.EditDatabaseWithCreateNewArticles.route
            && navController.currentDestination != null
        ) {

            // Navigate to FragmentStartupScreen instead of A_ClientsLocationGps
            navController.navigate(Screen.EditDatabaseWithCreateNewArticles.route) {

            }
        }
    }
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            val repositorysModel = viewModelInitApp.repo_0_0_HeadOfRepositorys_SQL_Repository
                .repositorys_Model
            val hideAppScreen = repositorysModel.repository_1_5_Vendeur.modelDatasSnapList
                .find { it.vid == repositorysModel.activeIdDe_1_5_Vendeur }?.hideAppScreen ?: false
            var isControleFabVisible by remember { mutableStateOf(false) }

            if (!shouldShowContent) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { repositoryProgress },
                        modifier = Modifier.size(64.dp),
                        trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
                        color = MaterialTheme.colorScheme.primary
                    )

                }
            } else {
                // Main content - only display when repository is loaded
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
                        // WiFi Connection Card
                        AnimatedVisibility(
                            visible = isDisplayedConnexionWifiVisible
                                    ||
                                    !productDisplayController.isConnected
                                    && !lockHost
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

                        // Set the NavController in the navigation handler
                        LaunchedEffect(Unit) {
                            navigationHandler.setNavController(navController)
                        }

                        // Main Content Area
                        Box(modifier = Modifier.weight(1f)) {

                            AppNavHost(
                                navController = navController,
                                onToggleNavBar = { isNavBarVisible = !isNavBarVisible },
                                modifier = Modifier.fillMaxSize(),
                                isFabVisible = isFabVisible,
                                onClickDonne = { isFabVisible = false },
                                onClickToDisplayeConexionWifi = {
                                    isDisplayedConnexionWifiVisible =
                                        !isDisplayedConnexionWifiVisible
                                },
                                onToggleLockHost = { lockHost = !lockHost },
                                viewModelInitApp = viewModelInitApp,
                                headViewModel = headViewModel,
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

                            val isControleFabVisibleFromClasse = panelsGroupeButtonHandler
                                .paneleGroupeButtonList
                                .find { it.key == PanelsGroupeButtonHandler.
                                PanelsGroupeButtonDeClasse.Keys
                                    .A_OptionsControlsButtons_A1FragID_3 }
                                ?.isVisible ?: false

                            if (isControleFabVisibleFromClasse || isControleFabVisible) {
                                A_OptionsControlsButtons_A1FragID_3() {
                                    showVendeursDialog = true
                                }
                            }
                        }

                        if (showVendeursDialog) {
                            AlertDialog(
                                onDismissRequest = {
                                    showVendeursDialog = false
                                },
                                title = { Text("Manage Vendeurs") },
                                text = {
                                    A_APP4FragID1_MainScreen(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(500.dp)
                                    )
                                },
                                confirmButton = {
                                    TextButton(onClick = {
                                        showVendeursDialog = false
                                    }) {
                                        Text("Close")
                                    }
                                }
                            )
                        }

                    }
                }

                // Navigation Bar with FAB - only show if app screen is not hidden
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
                        navController = navController,
                        modifier = Modifier.padding(bottom = 8.dp),
                        viewModelInitApp = viewModelInitApp
                    )
                }

                // Product Display Dialog - only if app screen is not hidden
                if (showProductDisplay && shouldShowContent && !hideAppScreen) {
                    val productId = productDisplayController.clientWindowsDisplayedProductId
                    val displayProductDataBase = productId?.let { id ->
                        uiState.articlesBasesStatTables.find { it.idArticle.toLong() == id }
                    }

                    if (displayProductDataBase != null) {
                        FragmentDisplayeInfoProductToClient7(
                            displayController = productDisplayController,
                            articleStatsDataBase = displayProductDataBase,
                            colorsArticlesList = uiState.colorsArticlesTabelleModel,
                            reloadTrigger = 0, // Use state if needed
                            modifier = Modifier.fillMaxSize(), viewModelInitApp = viewModelInitApp
                        )
                    }
                }

                // Search Display Dialog - only if app screen is not hidden
                if (productDisplayController.searchWindowsDisplaye.isNotEmpty() && shouldShowContent && !hideAppScreen) {
                    SearchArticle(
                        dsipayeText = productDisplayController.searchWindowsDisplaye
                    )
                }
                if (isHostPhone && !hideAppScreen) {
                    RecordAfficheurFAB()
                }

                // Show additional loading indicator if needed for other UI states
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
            }
        }
    }
}
