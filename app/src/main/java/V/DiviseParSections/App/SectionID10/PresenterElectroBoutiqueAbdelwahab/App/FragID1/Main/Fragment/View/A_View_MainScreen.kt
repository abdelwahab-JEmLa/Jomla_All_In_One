package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View

import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.PressistatntMainActivityButtons_Sec8FWinID1
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.PresenterElectroBoutiqueAbdelwahabSec10Frag1ViewModel
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.B.List.Components.SearchFilterPB
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.MainFastSearchProduitPourVent
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.HClientInfos
import V.DiviseParSections.App._0.Navigation.LoadingOverlay
import Views.FragId3_DialogVendeurAfficheurInfosProduit.ModernToastMessage
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import com.example.clientjetpack.ViewModel.HeadViewModel
import com.example.clientjetpack.ViewModel.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MainUi(
    produits: List<ArticlesBasesStatsTable>,
    viewModel: PresenterElectroBoutiqueAbdelwahabSec10Frag1ViewModel,
    uiState: UiState,
    filterText: String,
    gridState: LazyStaggeredGridState,
    onFilterTextChange: (String) -> Unit,
    onToggleFilter: () -> Unit,
    onChangeGridColumns: (Int) -> Unit,
    onToggleNavBar: () -> Unit,
    viewModelHeadViewModel: HeadViewModel,
    reloadTrigger: Int,
    onClickToOpenWindos: (ArticlesBasesStatsTable, Int) -> Unit,
    onClickToOpenClientsW: () -> Unit,
    isFabVisible: Boolean,
    onClickToDisplayeConexionWifi: () -> Unit,
    onToggleLockHost: () -> Unit,
    onToggleLockExpandedPricex: () -> Unit,
    currentClient: HClientInfos?,
    viewModelInitApp: ViewModelInitApp,
    lockHost: Boolean,
    onClickImageToShowControles: () -> Unit
) {

    val scope = rememberCoroutineScope()
    val tag =
        if (uiState.productDisplayController.isHostPhone) "📱 ServerScreen" else "📱 ClientScreen"
    var savedScrollPosition by rememberSaveable() { mutableStateOf(0) }
    var hostSavePosition by rememberSaveable() { mutableStateOf(0) }

    val currentScrollPosition = uiState.productDisplayController.mainGridScrollPosition

    LaunchedEffect(currentScrollPosition) {

        if (currentScrollPosition > 0) {
            scope.launch {
                try {
                    delay(100)
                    gridState.animateScrollToItem(
                        index = currentScrollPosition,
                        scrollOffset = 0
                    )
                } catch (e: Exception) {
                    gridState.scrollToItem(currentScrollPosition)
                }
            }
        }
    }
    LaunchedEffect(currentScrollPosition) {
        if (uiState.productDisplayController.isHostPhone) {
            scope.launch {
                try {
                    delay(1500)
                    gridState.animateScrollToItem(
                        index = hostSavePosition,
                        scrollOffset = 0
                    )
                } catch (e: Exception) {
                    gridState.scrollToItem(hostSavePosition)
                }
            }
        }

    }
    // Handle FAB visibility changes
    LaunchedEffect(isFabVisible) {
        if (isFabVisible) {
            savedScrollPosition = gridState.firstVisibleItemIndex
        } else {
            scope.launch {
                try {
                    gridState.animateScrollToItem(savedScrollPosition)
                } catch (e: Exception) {
                    gridState.scrollToItem(savedScrollPosition)
                }
            }
        }
    }

    HandleScrollBroadcast(
        isHostPhone = uiState.productDisplayController.isHostPhone,
        isConnected = uiState.productDisplayController.isConnected,
        gridState = gridState,
        viewModel = viewModelHeadViewModel,
        onScrollHostChange = { hostSavePosition = it }
    )

    HandleClientScroll(
        isHostPhone = uiState.productDisplayController.isHostPhone,
        scrollPosition = currentScrollPosition,
        gridState = gridState,
        tag = tag
    )

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (isFabVisible) 80.dp else 0.dp)
        ) {
            SearchFilterPB(
                showFilter = isFabVisible,
                filterText = filterText,
                onFilterTextChange = onFilterTextChange,
                onAddNotInBaseArticle = onClickToOpenWindos,
                viewModel = viewModelHeadViewModel,
                uiState = uiState,
            )
            if (uiState.productDisplayController.isHostPhone || uiState.productDisplayController.isConnected) {

                Box(modifier = Modifier.weight(1f)) {
                    ArticleGridWithScrollbar(
                        viewModel = viewModel,
                        produits = produits,
                        uiState = uiState,
                        filterText = filterText,
                        showFilter = isFabVisible,
                        gridState = gridState,
                        viewModelHeadViewModel = viewModelHeadViewModel,
                        reloadTrigger = reloadTrigger,
                        onClickToOpenWindos = onClickToOpenWindos,
                        currentClient = currentClient,
                        viewModelInitApp = viewModelInitApp, lockHost = lockHost,
                        onClickImageToShowControles = onClickImageToShowControles
                    )
                }
            }
        }

        AnimatedFabGroup(
            isFabVisible = isFabVisible,
            isConnected = uiState.productDisplayController.isConnected,
            isHostPhone = uiState.productDisplayController.isHostPhone,
            viewModel = viewModelHeadViewModel,
            onToggleNavBar = onToggleNavBar,
            onToggleOutlineFilter = onToggleFilter,
            onChangeGridColumns = onChangeGridColumns,
            onClickToOpenClientsListW = onClickToOpenClientsW,
            onClickToDisplayeConexionWifi = onClickToDisplayeConexionWifi,
            onToggleLockHost = onToggleLockHost,
            onToggleLockExpandedPricex = onToggleLockExpandedPricex,
            viewModelInitApp = viewModelInitApp
        )

        if (uiState.isLoading) {
            LoadingOverlay(
                progress = uiState.loadingProgress,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        if (viewModel.aCentral.focusedVarsHandlerFacade.get.activeDialogSearchM1Produit) {
            Dialog_MainFastSearchProduitPourVent(
                viewModel=viewModel,
            )
        }
    }
}

@Composable
private fun Dialog_MainFastSearchProduitPourVent(viewModel: PresenterElectroBoutiqueAbdelwahabSec10Frag1ViewModel) {
    var showToast by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = {
            showToast = true
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = true
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            shape = MaterialTheme.shapes.large,
            tonalElevation = 2.dp
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Toast Animation Component - Must be first to appear on top
                AnimatedVisibility(
                    visible = showToast,
                    enter = fadeIn(animationSpec = tween(durationMillis = 300)) +
                            scaleIn(
                                initialScale = 0.8f,
                                animationSpec = tween(durationMillis = 300)
                            ),
                    exit = fadeOut(animationSpec = tween(durationMillis = 300)) +
                            scaleOut(
                                targetScale = 0.8f,
                                animationSpec = tween(durationMillis = 300)
                            ),
                    modifier = Modifier.zIndex(999f)
                ) {
                    ModernToastMessage(
                        message = "يرجى استخدام الأزرار لتحديد السعر",
                        onDismiss = { showToast = false }
                    )
                }

                MainFastSearchProduitPourVent()
                PressistatntMainActivityButtons_Sec8FWinID1()

                fun dismisses_By_toggle_CurrentApp_activeDialogSearchM1Produit(): Unit {
                    viewModel.aCentral.focusedVarsHandlerFacade.set.toggle_CurrentApp_activeDialogSearchM1Produit()
                }

                FloatingActionButton(
                    onClick = { dismisses_By_toggle_CurrentApp_activeDialogSearchM1Produit() },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .zIndex(100f),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "إغلاق"
                    )
                }
            }
        }
    }
}
