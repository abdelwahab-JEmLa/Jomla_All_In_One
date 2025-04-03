package Views.P1.Ui.ArticlesGrid

import Views.P1.Ui.ArticlesGrid.ArticleItem.ArticleItem
import Views.P1.Ui.ArticlesGrid.Res.Scrollbar
import Views.P1.Ui.Objects.CategoryHeader
import Views.P1.Ui.Objects.ScrolleAdBanner
import Views.P1._ArticlesStartFacade.ArticlePagingSource
import Z_CodePartageEntreApps.Model.A_Produit.Z.Repository.A_ProduitRepository
import Z_CodePartageEntreApps.Model.B_ClientsDataBase
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_CodePartageEntreApps.Model.Z.Archive.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.Model.Z.Archive.CategoriesTabelle
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
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.clientjetpack.Models.UiState
import com.example.clientjetpack.ViewModel.HeadViewModel
import kotlinx.coroutines.delay
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

private const val TAG = "ArticleGridDebug"
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
     a_ProduitModelRepository: A_ProduitRepository,
     targetCategoryId: MutableState<Long?> = mutableStateOf(null), lockHost: Boolean
) {
    Box(modifier = modifier) {
        // Scrollbar first (will be on the left)
        Scrollbar(
            state = gridState,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 2.dp)
                .alpha(0.8f)
        )

        // Grid second
        ArticleGrid(
            uiState = uiState,
            gridColumns = gridColumns,
            filterText = filterText,
            showFilter = showFilter,
            gridState = gridState,
            viewModel = viewModel,
            reloadTrigger = reloadTrigger,
            modifier = Modifier.fillMaxSize(),
            onClickToOpenWindos = onClickToOpenWindos,
            currentClient = currentClient,
            targetCategoryId = targetCategoryId , a_ProduitModelRepository =a_ProduitModelRepository,
            lockHost = lockHost ,
            viewModelInitApp =viewModelInitApp
        )
    }
}

@Composable
fun ArticleGrid(
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
    targetCategoryId: MutableState<Long?> = mutableStateOf(null),
    a_ProduitModelRepository: A_ProduitRepository,
    lockHost: Boolean,
    viewModelInitApp: ViewModelInitApp
) {
    // Track scroll state and first visible item
    var lastSettledFirstVisible by remember { mutableStateOf(-1) }
    var isSettled by remember { mutableStateOf(true) }
    var currentCategory by remember { mutableStateOf<String?>(null) }

    // Configure paging with assembly monitoring
    val pagingConfig = remember {
        PagingConfig(
            pageSize = 3,
            enablePlaceholders = true,
            prefetchDistance = 2
        )
    }

    // Create category pagers with assembly tracking
    val categoryPagers = remember(uiState.categories, filterText) {
        uiState.categories.associateWith { category ->
            Pager(pagingConfig) {
                ArticlePagingSource(
                    articles = when {
                        category.nomCategorieInCategoriesTabele == "NewArrivale" ->
                            uiState.articlesBasesStatTables.filter { it.itsNewArrivale }

                        else ->
                            uiState.articlesBasesStatTables.filter {
                                it.nomCategorie == category.nomCategorieInCategoriesTabele && !it.itsNewArrivale
                            }
                    },
                    filterText = filterText,
                    currentClient = currentClient,
                    uiState = uiState
                )
            }
        }
    }

// Collect paging items for each category
    val categoryPagingItems = remember(categoryPagers) {
        mutableMapOf<CategoriesTabelle, LazyPagingItems<ArticlesBasesStatsTable>>()
    }.apply {
        categoryPagers.forEach { (category, pager) ->
            this[category] = pager.flow.collectAsLazyPagingItems()
        }
    }

    // Track scroll state changes
    LaunchedEffect(gridState) {
        snapshotFlow {
            ScrollState(
                index = gridState.firstVisibleItemIndex,
                isScrolling = gridState.isScrollInProgress
            )
        }.collect { scrollState ->
            if (!scrollState.isScrolling) {
                delay(100) // Brief delay for scroll settlement
                lastSettledFirstVisible = scrollState.index
                isSettled = true
            } else {
                isSettled = false
            }
        }
    }


    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(
            if (uiState.categories.any { it.nomCategorieInCategoriesTabele == "NewArrivale" })
                gridColumns else 2
        ),
        state = gridState,
        contentPadding = PaddingValues(3.dp),
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalItemSpacing = 3.dp
    ) {
        // Show banner if not filtering
        if (!showFilter) {
            item(span = StaggeredGridItemSpan.FullLine) {
                ScrolleAdBanner(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp) ,
                    onBannerClick = {

                    }
                )
            }
        }

        // Display categories in order
        uiState.categories
            .sortedBy { when {
                it.nomCategorieInCategoriesTabele == "NewArrivale" -> 0
                else -> it.idClassementCategorieInCategoriesTabele + 1
            } }
            .forEach { category ->
                val lazyPagingItems = categoryPagingItems[category]

                if (lazyPagingItems != null && lazyPagingItems.itemCount > 0) {
                    // Show category header if needed
                    if (category.displayedHeader) {
                        item(span = StaggeredGridItemSpan.FullLine) {
                            CategoryHeader(category)
                        }
                    }

                    // Display articles without keys
                    items(
                        count = lazyPagingItems.itemCount,
                        span = { index ->
                            val article = lazyPagingItems[index]

                            if (article?.diponibilityState != "") {
                                StaggeredGridItemSpan.FullLine
                            } else {
                                StaggeredGridItemSpan.SingleLane
                            }
                        }
                    ) { index ->
                        val article = lazyPagingItems[index]
                        article?.let { ancienData ->
                            val isFirstVisible = when {
                                !isSettled -> index == lastSettledFirstVisible
                                else -> index == gridState.firstVisibleItemIndex
                            }

                            if (isFirstVisible) {
                                currentCategory = category.nomCategorieInCategoriesTabele
                            }
                            val produitDepuitNewDATABASE =  a_ProduitModelRepository
                                .modelDatas.find { it.id.toInt() == article.idArticle }
                            ArticleItem(
                                article = ancienData,
                                viewModel = viewModel,
                                reloadTrigger = reloadTrigger,
                                onClickToOpenWindos = onClickToOpenWindos,
                                uiState = uiState,
                                isFirstVisible = isFirstVisible,
                                modifier = Modifier.animateItem(
                                    fadeInSpec = null,
                                    fadeOutSpec = null
                                ),
                                currentClient = currentClient,
                                produitDepuitNewDATABASE =produitDepuitNewDATABASE, lockHost = lockHost,
                                viewModelInitApp =viewModelInitApp
                            )
                        }
                    }
                }
            }
    }
}

// Function to handle scrolling to a specific category
fun scrollToCategory(
    categoryId: Long,
    targetCategoryId: MutableState<Long?>
) {
    targetCategoryId.value = categoryId
}

private data class ScrollState(
    val index: Int,
    val isScrolling: Boolean
)
