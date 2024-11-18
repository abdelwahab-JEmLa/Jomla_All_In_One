package P1_StartupScreen.Ui.ArticlesGrid

import P1_StartupScreen.Main.ArticlePagingSource
import P1_StartupScreen.Ui.ArticlesGrid.ArticleItem.ArticleItem
import P1_StartupScreen.Ui.ArticlesGrid.Res.Scrollbar
import P1_StartupScreen.Ui.Objects.CategoryHeader
import P1_StartupScreen.Ui.Objects.ScrolleAdBanner
import a_RoomDB.ArticlesBasesStatsTable
import a_RoomDB.CategoriesTabelle
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
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
import kotlinx.coroutines.flow.distinctUntilChanged
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
) {
    Box(modifier = modifier) {
        ArticleGrid(
            uiState = uiState,
            gridColumns = gridColumns,
            filterText = filterText,
            showFilter = showFilter,
            gridState = gridState,
            viewModel = viewModel,
            reloadTrigger = reloadTrigger,
            onClickToOpenWindos = onClickToOpenWindos,
            modifier = Modifier.fillMaxSize()
        )

        Scrollbar(
            state = gridState,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 2.dp)
                .alpha(0.8f)
        )
    }
}

private data class VisibleItemInfo(
    val index: Int,
    val itemId: Int,
    val offset: Int
)

@OptIn(ExperimentalFoundationApi::class)
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
) {
    // État global pour suivre l'article visible
    var currentVisibleItem by remember { mutableStateOf<VisibleItemInfo?>(null) }
    var globalIndex by remember { mutableStateOf(0) }

    // Configuration du paging
    val pagingConfig = remember {
        PagingConfig(
            pageSize = 3,
            enablePlaceholders = true,
            prefetchDistance = 2
        )
    }

    // Création des pagers par catégorie
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
                    filterText = filterText
                )
            }
        }
    }

    // Collection des items paginés
    val categoryPagingItems = remember(categoryPagers) {
        mutableMapOf<CategoriesTabelle, LazyPagingItems<ArticlesBasesStatsTable>>()
    }.apply {
        categoryPagers.forEach { (category, pager) ->
            this[category] = pager.flow.collectAsLazyPagingItems()
        }
    }

    // Suivi des changements de scroll
    LaunchedEffect(gridState) {
        snapshotFlow {
            VisibleItemInfo(
                index = gridState.firstVisibleItemIndex,
                itemId = gridState.firstVisibleItemIndex,
                offset = gridState.firstVisibleItemScrollOffset
            )
        }
            .distinctUntilChanged()
            .collect { visibleInfo ->
                if (!gridState.isScrollInProgress) {
                    delay(100) // Petit délai pour la stabilité
                    currentVisibleItem = visibleInfo
                    globalIndex = visibleInfo.index

                    Log.d(TAG, """
                    Scroll Update:
                    - Global Index: $globalIndex
                    - First Visible Index: ${visibleInfo.index}
                    - Offset: ${visibleInfo.offset}
                    - Is Scrolling: false
                """.trimIndent())
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
        // Bannière si pas de filtre
        if (!showFilter) {
            item(span = StaggeredGridItemSpan.FullLine) {
                ScrolleAdBanner(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }
        }

        var currentIndex = 0

        // Affichage des catégories dans l'ordre
        uiState.categories
            .sortedBy { if (it.nomCategorieInCategoriesTabele == "NewArrivale") 0 else 1 }
            .forEach { category ->
                val lazyPagingItems = categoryPagingItems[category]

                if (lazyPagingItems != null && lazyPagingItems.itemCount > 0) {
                    // En-tête de catégorie si nécessaire
                    if (category.displayedHeader) {
                        item(span = StaggeredGridItemSpan.FullLine) {
                            CategoryHeader(category)
                            currentIndex++
                        }
                    }

                    // Affichage des articles
                    items(
                        count = lazyPagingItems.itemCount,
                        span = { index ->
                            val article = lazyPagingItems[index]
                            if (article?.imageDimention == "Demi") {
                                StaggeredGridItemSpan.FullLine
                            } else {
                                StaggeredGridItemSpan.SingleLane
                            }
                        }
                    ) { index ->
                        val article = lazyPagingItems[index]
                        article?.let {
                            val actualIndex = currentIndex + index
                            val isFirstVisible = actualIndex == globalIndex

                            if (isFirstVisible) {
                                Log.d(TAG, """
                                    First Visible Article:
                                    - ID: ${it.idArticle}
                                    - Global Index: $globalIndex
                                    - Actual Index: $actualIndex
                                    - Category: ${category.nomCategorieInCategoriesTabele}
                                """.trimIndent())
                            }

                            ArticleItem(
                                article = it,
                                viewModel = viewModel,
                                reloadTrigger = reloadTrigger,
                                onClickToOpenWindos = onClickToOpenWindos,
                                uiState = uiState,
                                isFirstVisible = isFirstVisible,
                                modifier = Modifier.animateItemPlacement()
                            )
                        }
                    }

                    currentIndex += lazyPagingItems.itemCount
                }
            }
    }
}
