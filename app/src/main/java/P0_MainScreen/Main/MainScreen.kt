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

    var isControleFabVisible by remember { mutableStateOf(M18CentralParametresOfAllApps().isControleFabVisible) }

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

    val targted_Period_doitEtreDon = repo14VentPeriode.datasValue.find {
        it.abdelmounen_Doit_Etre_Ici
    }

    val doit_warning_change_Period_Vent =
        targted_Period_doitEtreDon != null && (focusedValuesGetter.currentActive_M9AppCompt?.keyID
            ?: "") == M18CentralParametresOfAllApps().abdelmomen_Compt_KeyId

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            val currentAppCompt = viewModel.getter.repo9AppCompt.currentAppCompt
            val hideAppScreen = currentAppCompt?.hideAppScreen ?: false

            if (!shouldShowContent || doit_warning_change_Period_Vent) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (doit_warning_change_Period_Vent) {
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
                                    if (targted_Period_doitEtreDon != null) {
                                        focusedValuesGetter.currentActive_M9AppCompt?.let {
                                            aCentralFacade.repositorysMainSetter.update_M9AppCompt(
                                                it.copy(
                                                    current_OnVent_M14VentPeriode_KeyID = targted_Period_doitEtreDon.keyID,
                                                )
                                            )
                                        }
                                        repo14VentPeriode.datasValue.forEach {
                                            repositorysMainSetter.update_M14VentPeriode(
                                                it.copy(
                                                    abdelmounen_Doit_Etre_Ici = false
                                                )
                                            )
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            BlinkingWarningCard(
                                "التطبيق ليس في الفترة المحددة للبيع اضغط للتحديث ${
                                    targted_Period_doitEtreDon?.keyID?.takeLast(
                                        3
                                    )
                                }"
                            )
                        }
                    } else {
                        CircularProgressIndicator(
                            progress = { repositoryProgress },
                            modifier = Modifier.size(64.dp),
                            trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

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
                            reloadTrigger = 0, // Use state if needed
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
        }
    }
}

