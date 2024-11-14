package B2_StartupAppDisplayerOfNewArticles.Main

import A0_MainActivity.Objects.LoadingOverlay
import A0_MainObjectsAPP.Models.UiState
import A0_MainObjectsAPP.ViewModel.HeadViewModel
import B2_StartupAppDisplayerOfNewArticles.FloatingActionButtonGroup
import B2_StartupAppDisplayerOfNewArticles.Ui.ArticleGridWithScrollbar
import B2_StartupAppDisplayerOfNewArticles.Ui.SearchFilterPB
import a_RoomDB.ArticlesBasesStatsTable
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@Composable
fun StartupAppDisplayerOfNewArticles(
    viewModel: HeadViewModel,
    onToggleNavBar: () -> Unit,
    reloadTrigger: Int,
    onClickToOpenWindos: (ArticlesBasesStatsTable, Int) -> Unit,
    onClickToOpenClientsW: () -> Unit,
    isFabVisible: Boolean,
    onClickDonne: () -> Unit,
    onClickToDisplayeConexionWifi: () -> Unit
) {
    var gridColumns by remember { mutableStateOf(2) }
    var showFilter by remember { mutableStateOf(false) }
    var filterText by remember { mutableStateOf("") }
    val gridState = rememberLazyStaggeredGridState()
    val uiState by viewModel.uiState.collectAsState()

    ArticleDisplayScreen(
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
    )
}

@Composable
fun ArticleDisplayScreen(
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
) {
    val scope = rememberCoroutineScope()
    val tag = if (uiState.isHostPhone) "📱 ServerScreen" else "📱 ClientScreen"

    // Sauvegarder la position quand le FAB est visible
    var savedScrollPosition by remember { mutableStateOf(0) }

    // Gérer le changement de visibilité du FAB
    LaunchedEffect(isFabVisible) {
        if (isFabVisible) {
            // Quand le FAB devient visible, sauvegarder la position actuelle
            savedScrollPosition = gridState.firstVisibleItemIndex
        } else {
            // Quand le FAB devient invisible, revenir à la position sauvegardée
            scope.launch {
                gridState.scrollToItem(savedScrollPosition)
            }
        }
    }

    HandleScrollBroadcast(
        isHostPhone = uiState.isHostPhone,
        isConnected = uiState.isConnected,
        gridState = gridState,
        viewModel = viewModel,
        tag = tag
    )

    HandleClientScroll(
        isHostPhone = uiState.isHostPhone,
        scrollPosition = uiState.scrollPosition,
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
                viewModel = viewModel,
                uiState = uiState,
                onClickDonne = onClickDonne
            )

            Box(modifier = Modifier.weight(1f)) {
                ArticleGridWithScrollbar(
                    uiState = uiState,
                    gridColumns = gridColumns,
                    filterText = filterText,
                    showFilter = isFabVisible,
                    gridState = gridState,
                    viewModel = viewModel,
                    reloadTrigger = reloadTrigger,
                    onClickToOpenWindos = onClickToOpenWindos
                )
            }
        }

        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(bottom = 16.dp, end = 16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            AnimatedVisibility(
                visible = isFabVisible,
                enter = fadeIn() + slideInVertically { it / 2 },
                exit = fadeOut() + slideOutVertically { it / 2 }
            ) {
                FloatingActionButtonGroup(
                    modifier = Modifier.zIndex(1f),
                    viewModel = viewModel,
                    onToggleNavBar = onToggleNavBar,
                    onToggleOutlineFilter = onToggleFilter,
                    onChangeGridColumns = onChangeGridColumns,
                    onClickToOpenClientsListW = onClickToOpenClientsW,
                    onClickToDisplayeConexionWifi = onClickToDisplayeConexionWifi
                )
            }
        }

        if (uiState.isLoading) {
            LoadingOverlay(
                progress = uiState.loadingProgress,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun HandleScrollBroadcast(
    isHostPhone: Boolean,
    isConnected: Boolean,
    gridState: LazyStaggeredGridState,
    viewModel: HeadViewModel,
    tag: String
) {
    LaunchedEffect(isHostPhone, isConnected) {
        if (!isHostPhone || !isConnected) {
            Log.d(tag, "⚠️ Not monitoring scroll (Server: $isHostPhone, Connected: $isConnected)")
            return@LaunchedEffect
        }

        Log.d(tag, "👀 Starting scroll monitoring")
        snapshotFlow { gridState.firstVisibleItemIndex }
            .distinctUntilChanged()
            .collect { position ->
                Log.d(tag, "📊 Grid position changed to: $position")
                try {
                    viewModel.sendScrollPositionToClient(position)
                    Log.d(tag, "✅ Scroll broadcast completed")
                } catch (e: Exception) {
                    Log.e(tag, "❌ Failed to broadcast scroll: ${e.message}")
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

    LaunchedEffect(scrollPosition) {
        if (isHostPhone) return@LaunchedEffect

        Log.d(tag, "🔄 Processing scroll position update")
        try {
            scope.launch {
                gridState.animateScrollToItem(scrollPosition)
                delay(300)
                Log.d(tag, "✅ Scroll animation completed")
            }
        } catch (e: Exception) {
            Log.e(tag, "❌ Scroll animation failed: ${e.message}")
        }
    }
}

class ArticlePagingSource(
    private val articles: List<ArticlesBasesStatsTable>,
    private val filterText: String,
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

    private fun filterArticles() = if (filterText.isEmpty()) {
        articles.filter { it.idForSearchArticles <= 0 && it.diponibilityState == "" }
    } else {
        articles.filter { article ->
            article.nomArticleFinale.contains(filterText, ignoreCase = true) ||
                    article.idForSearchArticles > 0
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticlesBasesStatsTable> {
        val page = params.key ?: 0

        return try {
            val filteredArticles = cachedFilteredArticles.getOrPut(page) {
                filterArticles().drop(page * pageSize).take(pageSize)
            }

            LoadResult.Page(
                data = filteredArticles,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (filteredArticles.size < pageSize) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        } finally {
            // Clear cache for pages that are no longer needed
            cachedFilteredArticles.keys
                .filter { it < page - 1 || it > page + 1 }
                .forEach { cachedFilteredArticles.remove(it) }
        }
    }
}
