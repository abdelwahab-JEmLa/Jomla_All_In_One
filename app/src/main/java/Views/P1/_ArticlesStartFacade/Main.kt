package Views.P1._ArticlesStartFacade

import P0_MainScreen.Ui.Objects.LoadingOverlay
import Views.P1.Ui.ArticlesGrid.ArticleGridWithScrollbar
import Views.P1.Ui.Objects.SearchFilterPB
import Views.P1._ArticlesStartFacade.FloatingActionButtonGroup.FloatingActionButtonGroup
import Z_CodePartageEntreApps.Model.A_ProduitModelRepository
import Z_CodePartageEntreApps.Model.B_ClientsDataBase
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Kotlin._WorkingON.WO_.WifiUpdateClientDisplayerStats
import Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Models.ArticlesBasesStatsTable
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
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.clientjetpack.Models.UiState
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
    isFabVisible: Boolean,
    onClickDonne: () -> Unit,
    onClickToDisplayeConexionWifi: () -> Unit, scrollTiger: Int, onToggleLockHost: () -> Unit,
    onToggleLockExpandedPricex: () -> Unit, currentClient: B_ClientsDataBase?,
    viewModelInitApp: ViewModelInitApp,
    targetCategoryId: MutableState<Long?> = mutableStateOf(null), lockHost: Boolean
) {
    var gridColumns by remember { mutableStateOf(2) }
    var showFilter by remember { mutableStateOf(false) }
    var filterText by remember { mutableStateOf("") }
    val gridState = rememberLazyStaggeredGridState()
    val uiState by viewModel.uiState.collectAsState()
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
            filterText=""
            onClickDonne() },
        onClickToDisplayeConexionWifi = onClickToDisplayeConexionWifi,
        scrollTiger, onToggleLockHost = onToggleLockHost,
        onToggleLockExpandedPricex = onToggleLockExpandedPricex, currentClient = currentClient,
        viewModelInitApp = viewModelInitApp,
        targetCategoryId = targetCategoryId, lockHost = lockHost

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
    targetCategoryId: MutableState<Long?> = mutableStateOf(null), lockHost: Boolean
) {
    val scope = rememberCoroutineScope()
    val tag = if (uiState.productDisplayController.isHostPhone) "📱 ServerScreen" else "📱 ClientScreen"
    var savedScrollPosition by rememberSaveable() { mutableStateOf(0) }
    var hostSavePosition by rememberSaveable() { mutableStateOf(0) }

    // Get the current scroll position from ProductDisplayController
    val currentScrollPosition = uiState.productDisplayController.mainGridScrollPosition

    // Handle initial scroll position and screen returns
    LaunchedEffect(currentScrollPosition) {

        if (currentScrollPosition > 0) {
            scope.launch {
                try {
                    // Animate scroll with custom duration and delay
                    delay(100) // Small initial delay for smoother transition
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
    LaunchedEffect(currentScrollPosition,) {//-->
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
        onScrollHostChange = {hostSavePosition=it}
    )

    HandleClientScroll(
        isHostPhone = uiState.productDisplayController.isHostPhone,
        scrollPosition = currentScrollPosition,
        gridState = gridState,
        tag = tag
    )

    Box(modifier = Modifier.fillMaxSize()) {
        val a_ProduitModelRepository = koinInject<A_ProduitModelRepository>()

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
                onClickDonne = onClickDonne
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
                        onClickToOpenWindos = onClickToOpenWindos, currentClient = currentClient
                        , viewModelInitApp =viewModelInitApp ,
                        targetCategoryId =targetCategoryId ,
                        a_ProduitModelRepository=a_ProduitModelRepository, lockHost = lockHost
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
            onClickToDisplayeConexionWifi = onClickToDisplayeConexionWifi, onToggleLockHost = onToggleLockHost,
            onToggleLockExpandedPricex = onToggleLockExpandedPricex, viewModelInitApp = viewModelInitApp
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
    onScrollHostChange : (Int) -> Unit,
) {
    var lastScrollPosition by remember { mutableStateOf(0) }
    var isScrollInProgress by remember { mutableStateOf(false) }

    LaunchedEffect(isHostPhone, isConnected) {
        if (!isHostPhone || !isConnected) {
            Log.d(TAG, "HandleScrollBroadcast: Not handling scroll - isHost: $isHostPhone, isConnected: $isConnected")
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
            """.trimIndent())

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



class ArticlePagingSource(
    private val articles: List<ArticlesBasesStatsTable>,
    private val filterText: String,
    private val currentClient: B_ClientsDataBase?,
    private val uiState: UiState,
) : PagingSource<Int, ArticlesBasesStatsTable>() {
    private val pageSize = 10
    private val cachedFilteredArticles = mutableMapOf<Int, List<ArticlesBasesStatsTable>>()

    override fun getRefreshKey(state: PagingState<Int, ArticlesBasesStatsTable>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.let { anchorPage ->
                anchorPage.prevKey?.plus(1) ?: anchorPage.nextKey?.minus(1)
            }
        }
    }

    private fun filterArticles(): List<ArticlesBasesStatsTable> {
        return if (filterText.isEmpty()) {
            articles.filter { article ->
                val currentProductByCurrentClient = uiState.diviseurDeDisplayProductForEachClient.find { divis1 ->
                    divis1.keyVid == "${currentClient?.id}->${article.idArticle}"
                }
                val currentProductByClientStandard = uiState.diviseurDeDisplayProductForEachClient.find {divis2 ->
                    divis2.keyVid == "100->${article.idArticle}"
                }
                val denied = currentProductByCurrentClient?.deniedFromDislplayToClient
                    ?: currentProductByClientStandard?.deniedFromDislplayToClient

                article.idForSearchArticles <= 0 &&
                        article.diponibilityState.isEmpty() &&
                        !article.nomArticleFinale.contains("New")
            }
        } else {
            articles.filter { article ->
                article.nomArticleFinale.contains(filterText, ignoreCase = true) ||
                        article.idForSearchArticles > 0
            }
        }
    }
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticlesBasesStatsTable> {
        val page = params.key ?: 0

        return try {
            val filteredArticles = cachedFilteredArticles.getOrPut(page) {
                filterArticles()
                    .drop(page * pageSize)
                    .take(pageSize)
            }

            LoadResult.Page(
                data = filteredArticles,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (filteredArticles.size < pageSize) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        } finally {
            // Clean up cache to prevent memory leaks
            cleanupCache(page)
        }
    }

    private fun cleanupCache(currentPage: Int) {
        cachedFilteredArticles.keys
            .filter { it < currentPage - 1 || it > currentPage + 1 }
            .forEach { cachedFilteredArticles.remove(it) }
    }
}
