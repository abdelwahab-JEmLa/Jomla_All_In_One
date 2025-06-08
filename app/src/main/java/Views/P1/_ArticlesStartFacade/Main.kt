package Views.P1._ArticlesStartFacade

import V.DiviseParSections.App._0.Navigation.LoadingOverlay
import Views.P1.Ui.ArticlesGrid.A.List.ArticleGridWithScrollbar
import Views.P1.Ui.Objects.SearchFilterPB
import Views.P1._ArticlesStartFacade.FloatingActionButtonGroup.FloatingActionButtonGroup
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.A.Model.Juin3.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.Model.A_Produit.Z.Repository.A_ProduitRepository
import Z_CodePartageEntreApps.Model.B_ClientsDataBase
import Z_CodePartageEntreApps.Modules.WifiUpdateClientDisplayerStats
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.clientjetpack.Repositorys.UiState
import com.example.clientjetpack.ViewModel.HeadViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun FragmentStartupScreen(
    viewModel: HeadViewModel,
    onToggleNavBar: () -> Unit,
    reloadTrigger: Int,
    onClickToOpenWindos: (ArticlesBasesStatsTable, Int) -> Unit,
    onClickToOpenClientsW: () -> Unit,
    isFabVisibleInit: Boolean,
    onClickDonne: () -> Unit,
    onClickToDisplayeConexionWifi: () -> Unit,
    scrollTiger: Int,
    onToggleLockHost: () -> Unit,
    onToggleLockExpandedPricex: () -> Unit,
    currentClient: B_ClientsDataBase?,
    viewModelInitApp: ViewModelInitApp,
    targetCategoryId: MutableState<Long?> = mutableStateOf(null),
    lockHost: Boolean, onClickImageToShowControles: () -> Unit
) {
    val DevMode = false
    val isFabVisible = if (DevMode) DevMode else isFabVisibleInit
    val filterTextInit = if (DevMode) "dyd" else ""
    var showFilter by remember { mutableStateOf(false) }
    var gridColumns by remember { mutableStateOf(2) }
    var filterText by remember { mutableStateOf(filterTextInit) }
    val gridState = rememberLazyStaggeredGridState()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        if (viewModelInitApp.savedGridScrollPosition > 0) {
            gridState.scrollToItem(viewModelInitApp.savedGridScrollPosition)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModelInitApp.savedGridScrollPosition = gridState.firstVisibleItemIndex
        }
    }
    MainUi(
        uiState = uiState,
        gridColumns = gridColumns,
        filterText = filterText,
        gridState = gridState,
        onFilterTextChange = { filterText = it },
        onToggleFilter = { showFilter = !showFilter },
        onChangeGridColumns = { gridColumns = it },
        onToggleNavBar = onToggleNavBar,
        viewModel = viewModel,
        reloadTrigger = reloadTrigger,
        onClickToOpenWindos = onClickToOpenWindos,
        onClickToOpenClientsW = onClickToOpenClientsW,
        isFabVisible = isFabVisible,
        onClickDonne = {
            filterText = ""
            onClickDonne()
        },
        onClickToDisplayeConexionWifi = onClickToDisplayeConexionWifi,
        scrollTiger = scrollTiger,
        onToggleLockHost = onToggleLockHost,
        onToggleLockExpandedPricex = onToggleLockExpandedPricex,
        currentClient = currentClient,
        viewModelInitApp = viewModelInitApp,
        targetCategoryId = targetCategoryId,
        lockHost = lockHost, onClickImageToShowControles = onClickImageToShowControles
    )
}

@Composable
fun MainUi(
    uiState: UiState,
    gridColumns: Int,
    filterText: String,
    gridState: LazyStaggeredGridState,
    onFilterTextChange: (String) -> Unit,
    onToggleFilter: () -> Unit,
    onChangeGridColumns: (Int) -> Unit,
    onToggleNavBar: () -> Unit,
    viewModel: HeadViewModel,
    reloadTrigger: Int,
    onClickToOpenWindos: (ArticlesBasesStatsTable, Int) -> Unit,
    onClickToOpenClientsW: () -> Unit,
    isFabVisible: Boolean,
    onClickDonne: () -> Unit,
    onClickToDisplayeConexionWifi: () -> Unit,
    scrollTiger: Int,
    onToggleLockHost: () -> Unit,
    onToggleLockExpandedPricex: () -> Unit,
    currentClient: B_ClientsDataBase?,
    viewModelInitApp: ViewModelInitApp,
    targetCategoryId: MutableState<Long?> = mutableStateOf(null),
    lockHost: Boolean, onClickImageToShowControles: () -> Unit
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
                    // Fallback to instant scroll if animation fails
                    gridState.scrollToItem(currentScrollPosition)
                }
            }
        }
    }
    LaunchedEffect(currentScrollPosition) {//-->
        if (uiState.productDisplayController.isHostPhone) {
            scope.launch {
                try {
                    // Animate scroll with custom duration and delay
                    delay(1500) // Small initial delay for smoother transition
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
        viewModel = viewModel,
        onScrollHostChange = { hostSavePosition = it }
    )

    HandleClientScroll(
        isHostPhone = uiState.productDisplayController.isHostPhone,
        scrollPosition = currentScrollPosition,
        gridState = gridState,
        tag = tag
    )

    Box(modifier = Modifier.fillMaxSize()) {
        val a_ProduitModelRepository = koinInject<A_ProduitRepository>()

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
                viewModel = viewModel,
                uiState = uiState,
            )
            if (uiState.productDisplayController.isHostPhone || uiState.productDisplayController.isConnected) {

                Box(modifier = Modifier.weight(1f)) {
                    ArticleGridWithScrollbar(
                        uiState = uiState,
                        gridColumns = gridColumns,
                        filterText = filterText,
                        showFilter = isFabVisible,
                        gridState = gridState,
                        viewModel = viewModel,
                        reloadTrigger = reloadTrigger,
                        onClickToOpenWindos = onClickToOpenWindos,
                        currentClient = currentClient, viewModelInitApp = viewModelInitApp,
                        targetCategoryId = targetCategoryId,
                        lockHost = lockHost,
                        onClickImageToShowControles = onClickImageToShowControles
                    )
                }
            }
        }

        AnimatedFabGroup(
            isFabVisible = isFabVisible,
            isConnected = uiState.productDisplayController.isConnected,
            isHostPhone = uiState.productDisplayController.isHostPhone,
            viewModel = viewModel,
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
    }
}

@Composable
private fun AnimatedFabGroup(
    isFabVisible: Boolean,
    isConnected: Boolean,
    isHostPhone: Boolean,
    viewModel: HeadViewModel,
    onToggleNavBar: () -> Unit,
    onToggleOutlineFilter: () -> Unit,
    onChangeGridColumns: (Int) -> Unit,
    onClickToOpenClientsListW: () -> Unit,
    onClickToDisplayeConexionWifi: () -> Unit, onToggleLockHost: () -> Unit,
    onToggleLockExpandedPricex: () -> Unit, viewModelInitApp: ViewModelInitApp,
) {
    Box(
        modifier = Modifier
            .padding(bottom = 16.dp, end = 16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        AnimatedVisibility(
            visible = isFabVisible || (isConnected && isHostPhone),
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 }
        ) {
            FloatingActionButtonGroup(
                modifier = Modifier.zIndex(1f),
                viewModel = viewModel,
                onToggleNavBar = onToggleNavBar,
                onToggleOutlineFilter = onToggleOutlineFilter,
                onChangeGridColumns = onChangeGridColumns,
                onClickToOpenClientsListW = onClickToOpenClientsListW,
                onClickToDisplayeConexionWifi = onClickToDisplayeConexionWifi,
                onToggleLockHost,
                onToggleLockExpandedPricex,
                viewModelInitApp
            )
        }
    }
}

private const val TAG = "ArticleGridDebug"

@Composable
private fun HandleScrollBroadcast(
    isHostPhone: Boolean,
    isConnected: Boolean,
    gridState: LazyStaggeredGridState,
    viewModel: HeadViewModel,
    onScrollHostChange: (Int) -> Unit,
) {
    var lastScrollPosition by remember { mutableStateOf(0) }
    var isScrollInProgress by remember { mutableStateOf(false) }

    LaunchedEffect(isHostPhone, isConnected) {
        if (!isHostPhone || !isConnected) {
            Log.d(
                TAG,
                "HandleScrollBroadcast: Not handling scroll - isHost: $isHostPhone, isConnected: $isConnected"
            )
            return@LaunchedEffect
        }

        snapshotFlow {
            gridState.firstVisibleItemIndex to gridState.firstVisibleItemScrollOffset
        }
            .distinctUntilChanged()
            .collect { (position, offset) ->
                Log.d(
                    TAG, """
                Scroll Update:
                - Position: $position
                - Offset: $offset
                - Last Position: $lastScrollPosition
                - Is Scrolling: $isScrollInProgress
            """.trimIndent()
                )

                val isDragging = when {
                    gridState.layoutInfo.visibleItemsInfo.isEmpty() -> false
                    offset > 0 -> true
                    position != lastScrollPosition -> true
                    else -> false
                }

                if (isDragging) {
                    isScrollInProgress = true
                    if (position != lastScrollPosition) {
                        lastScrollPosition = position
                        onScrollHostChange(position)
                        Log.d(TAG, "Sending scroll position to client: $position")
                        viewModel.sendOrderToClientDisplayer(
                            WifiUpdateClientDisplayerStats.ClientMainGridScrollPosition.prefix,
                            position
                        )
                    }
                } else if (isScrollInProgress) {
                    isScrollInProgress = false
                    Log.d(TAG, "Final scroll position sent to client: $position")
                    viewModel.sendOrderToClientDisplayer(
                        WifiUpdateClientDisplayerStats.ClientMainGridScrollPosition.prefix,
                        position
                    )
                }
            }
    }
}

@Composable
private fun HandleClientScroll(
    isHostPhone: Boolean,
    scrollPosition: Int,
    gridState: LazyStaggeredGridState,
    tag: String
) {
    val scope = rememberCoroutineScope()
    var isAnimating by remember { mutableStateOf(false) }

    LaunchedEffect(scrollPosition) {
        if (isHostPhone) return@LaunchedEffect

        try {
            if (!isAnimating) {
                isAnimating = true
                scope.launch {
                    // Smooth scroll to the received position
                    gridState.animateScrollToItem(
                        index = scrollPosition,
                        scrollOffset = 0
                    )
                    delay(100)
                    isAnimating = false
                }
            }
        } catch (e: Exception) {
            isAnimating = false
            // Fallback to instant scroll
            gridState.scrollToItem(scrollPosition)
        }
    }
}


private data class ScrollUpdate(
    val position: Int,
    val offset: Int
)

// ExtensionProduitModel functions for scroll handling
private fun Int.toScrollUpdate(): ScrollUpdate {
    return ScrollUpdate(
        position = this,
        offset = 0
    )
}


