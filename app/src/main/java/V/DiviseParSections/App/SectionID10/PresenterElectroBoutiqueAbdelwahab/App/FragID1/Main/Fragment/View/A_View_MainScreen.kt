package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View

import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.PressistatntMainActivityButtons_Sec8FWinID1
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.Z.Main.PanierFinaleDAchatSec1Frag3
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.PresenterElectroBoutiqueAbdelwahabSec10Frag1ViewModel
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.B.List.Components.SearchFilterPB
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.Z.Option.DialogsSearchProduit
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App._0.Navigation.LoadingOverlay
import Views.FragId3_DialogVendeurAfficheurInfosProduit.ModernToastMessageLo
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
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
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
    currentClient: M2Client?,
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

    var showToast by remember { mutableStateOf(false) }

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
        DialogsSearchProduit(
            aCentralFacade = viewModel.aCentralFacade
        )

        Dialog_Panie(focusedValuesGetter, showToast)
    }
}

@Composable
private fun Dialog_Panie(
    focusedValuesGetter: FocusedValuesGetter,
    showToast: Boolean
) {
    var showToast1 = showToast
    if (
        focusedValuesGetter.active_Central_Values.opnerDialog_Panier_M10OperationVentCouleur
        != null
    ) {
        Dialog(
            onDismissRequest = {
                showToast1 = true
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
                    AnimatedVisibility(
                        visible = showToast1,
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
                        ModernToastMessageLo(
                            message = "يرجى استخدام الأزرار لتحديد السعر",
                            onDismiss = { showToast1 = false }
                        )
                    }
                    Column {

                        PanierFinaleDAchatSec1Frag3()
                    }
                    PressistatntMainActivityButtons_Sec8FWinID1()
                    FloatingActionButton(
                        onClick = {
                            focusedValuesGetter.update_activeCentralValues(
                                focusedValuesGetter.active_Central_Values.copy(
                                    opnerDialog_Panier_M10OperationVentCouleur = null
                                )
                            )
                        },
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
}

