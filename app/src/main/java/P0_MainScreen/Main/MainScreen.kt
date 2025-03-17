package P0_MainScreen.Main

import P0_MainScreen.Modules.HandleFullscreenMode
import P0_MainScreen.Ui.Main.AppNavHost.AppNavHost
import P0_MainScreen.Ui.Main.AppNavHost.NavigationBarWithFab
import P0_MainScreen.Ui.Main.AppNavHost.NavigationItems
import P0_MainScreen.Ui.Main.AppNavHost.Screen
import P0_MainScreen.Ui.Objects.ConnexionCard
import Views.FragId4_EStorePresentationToClient.FragmentDisplayeInfoProductToClient7
import Views.FragId4_EStorePresentationToClient.Modules.SearchArticle
import Z_CodePartageEntreApps.Model.A_ProduitModelRepository
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import P0_MainScreen.Main.Windows.RecordAfficheurFAB
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.clientjetpack.ViewModel.HeadViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModelInitApp: ViewModelInitApp = koinViewModel(),
    xmlResources: List<Pair<String, Int>>?=null,
) {
    val a_ProduitModelRepository = koinInject<A_ProduitModelRepository>()

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
            Log.w(TAG, "UI waiting for repository to load. Current progress: ${repositoryProgress * 100}%")
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

// In MainScreen.kt, replace the LaunchedEffect block with:
    LaunchedEffect(productDisplayController.clientWindowsDisplayedProductId) {
        showProductDisplay = productDisplayController.clientWindowsDisplayedProductId != null

        // Only navigate if needed, and don't force back to start every time
        if (productDisplayController.clientWindowsDisplayedProductId == null
            && productDisplayController.isHostPhone
            && currentRoute != Screen.A_ClientsLocationGps.route
            && navController.currentDestination != null) {

            // Navigate without popping the entire back stack
            navController.navigate(Screen.A_ClientsLocationGps.route) {
                // Only pop up to start if explicitly returning to home
                // launchSingleTop = true
            }
        }
    }
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Show loading indicator while repository is loading
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
                Column(modifier = Modifier.fillMaxSize()) {
                    // WiFi Connection Card
                    AnimatedVisibility(
                        visible = isDisplayedConnexionWifiVisible
                                ||
                                !productDisplayController.isConnected
                                && !lockHost
                    ) {
                        ConnexionCard(
                            headViewModel=headViewModel,
                            productDisplayController = productDisplayController,
                            onClickToStartAsClient = {
                                isNavBarVisible = false
                                isFabVisible = false
                            } ,
                            lockHost= lockHost
                        )
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
                                isDisplayedConnexionWifiVisible = !isDisplayedConnexionWifiVisible
                            },
                            onToggleLockHost = {lockHost=!lockHost}, viewModelInitApp = viewModelInitApp,
                            onClear = {},
                            headViewModel = headViewModel,
                            targetCategoryId=targetCategoryId,
                            lockHost = isHostPhone
                        )

                        // Disable interactions when not host phone
                        if (!isHostPhone && productDisplayController.isConnected) {
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable(enabled = false) { }
                            )
                        }
                    }
                }

                // Navigation Bar with FAB
                AnimatedVisibility(
                    visible = (isHostPhone || !productDisplayController.isConnected) && shouldShowContent,
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
                        navController=navController,
                        modifier = Modifier.padding(bottom = 8.dp),
                        viewModelInitApp=viewModelInitApp
                    )
                }

                // Product Display Dialog
                if (showProductDisplay && shouldShowContent) {
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

                // Search Display Dialog
                if (productDisplayController.searchWindowsDisplaye.isNotEmpty() && shouldShowContent) {
                    SearchArticle(
                        dsipayeText = productDisplayController.searchWindowsDisplaye
                    )
                }
                if (isHostPhone) {
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
