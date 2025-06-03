package Views.P1.Ui.ArticlesGrid.A.List

import Views.P1.Ui.ArticlesGrid.C.Ui.ArticleItem
import Views.P1.Ui.ArticlesGrid.B.Main.Filter.filterArticles
import Views.P1.Ui.ArticlesGrid.Res.Scrollbar
import Views.P1.Ui.Objects.CategoryHeader
import Views.P1.Ui.Objects.ScrolleAdBanner
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models.CategoriesTabelle
import Z_CodePartageEntreApps.Model.B_ClientsDataBase
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.example.clientjetpack.Repositorys.UiState
import com.example.clientjetpack.ViewModel.HeadViewModel
import kotlinx.coroutines.delay

@Composable
fun ArticleGridWithScrollbar(
    uiState: UiState,
    gridColumns: Int,
    filterText: String,
    showFilter: Boolean,
    gridState: LazyStaggeredGridState,
    viewModel: HeadViewModel,
    reloadTrigger: Int,
    modifier: Modifier = Modifier,
    onClickToOpenWindos: (ArticlesBasesStatsTable, Int) -> Unit,
    currentClient: B_ClientsDataBase?,
    viewModelInitApp: ViewModelInitApp,
    targetCategoryId: MutableState<Long?> = mutableStateOf(null),
    lockHost: Boolean,
    onClickImageToShowControles: () -> Unit
) {
    Box(modifier = modifier) {
        Scrollbar(
            state = gridState,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 2.dp)
                .alpha(0.8f)
        )

        MainList(
            uiState = uiState,
            filterText = filterText,
            showFilter = showFilter,
            gridState = gridState,
            viewModel = viewModel,
            reloadTrigger = reloadTrigger,
            modifier = Modifier.fillMaxSize(),
            onClickToOpenWindos = onClickToOpenWindos,
            currentClient = currentClient,
            lockHost = lockHost,
            viewModelInitApp = viewModelInitApp,
            onClickImageToShowControles = onClickImageToShowControles
        )
    }
}

@Composable
fun MainList(
    uiState: UiState,
    filterText: String,
    showFilter: Boolean,
    gridState: LazyStaggeredGridState,
    viewModel: HeadViewModel,
    reloadTrigger: Int,
    modifier: Modifier = Modifier,
    onClickToOpenWindos: (ArticlesBasesStatsTable, Int) -> Unit,
    currentClient: B_ClientsDataBase?,
    lockHost: Boolean,
    viewModelInitApp: ViewModelInitApp,
    onClickImageToShowControles: () -> Unit
) {
    var lastSettledFirstVisible by remember { mutableStateOf(-1) }
    var isSettled by remember { mutableStateOf(true) }
    var currentCategory by remember { mutableStateOf<String?>(null) }

    val filteredArticles = remember(uiState.articlesBasesStatTables, filterText, currentClient) {
        filterArticles(uiState.articlesBasesStatTables, filterText, )
    }

    val articlesByCategory = remember(filteredArticles, uiState.categories) {
        val sortedCategories = uiState.categories.sortedWith(
            compareBy<CategoriesTabelle> { category ->
                when {
                    category.position <= 0 -> Int.MAX_VALUE
                    else -> category.position
                }
            }.thenBy { it.nom }
        )

        sortedCategories.associateWith { category ->
            val articlesForCategory = when {
                category.nom == "NewArrivale" ->
                    filteredArticles.filter { it.itsNewArrivale }
                else ->
                    filteredArticles.filter {
                        it.idParentCategorie == category.id && !it.itsNewArrivale
                    }
            }

            articlesForCategory
        }.filterValues { it.isNotEmpty() }
    }

    LaunchedEffect(gridState) {
        snapshotFlow {
            ScrollState(
                index = gridState.firstVisibleItemIndex,
                isScrolling = gridState.isScrollInProgress
            )
        }.collect { scrollState ->
            if (!scrollState.isScrolling) {
                delay(100)
                lastSettledFirstVisible = scrollState.index
                isSettled = true
            } else {
                isSettled = false
            }
        }
    }

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        state = gridState,
        contentPadding = PaddingValues(3.dp),
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalItemSpacing = 3.dp
    ) {
        if (!showFilter) {
            item(span = StaggeredGridItemSpan.FullLine) {
                ScrolleAdBanner(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    onBannerClick = {},
                    onClickImageToShowControles
                )
            }
        }

        articlesByCategory.forEach { (category, articles) ->
            if (category.displayedHeader) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    CategoryHeader(category)
                }
            }

            items(
                items = articles,
                key = { article -> "${category.id}_${article.id}" },
                span = { StaggeredGridItemSpan.SingleLane }
            ) { article ->
                val isFirstVisible = when {
                    !isSettled -> articles.indexOf(article) == lastSettledFirstVisible
                    else -> articles.indexOf(article) == gridState.firstVisibleItemIndex
                }

                if (isFirstVisible) {
                    currentCategory = category.nom
                }

                ArticleItem(
                    article = article,
                    viewModel = viewModel,
                    reloadTrigger = reloadTrigger,
                    onClickToOpenWindos = onClickToOpenWindos,
                    uiState = uiState,
                    isFirstVisible = isFirstVisible,
                    modifier = Modifier.animateItem(
                        fadeInSpec = null,
                        fadeOutSpec = null
                    ),
                    lockHost = lockHost,
                    viewModelInitApp = viewModelInitApp
                )
            }
        }
    }
}

private data class ScrollState(
    val index: Int,
    val isScrolling: Boolean
)
