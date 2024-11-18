package P1_StartupScreen.Ui.ArticlesGrid

import P1_StartupScreen.Main.ArticlePagingSource
import P1_StartupScreen.Ui.ArticlesGrid.ArticleItem.ArticleItem
import P1_StartupScreen.Ui.ArticlesGrid.Res.Scrollbar
import P1_StartupScreen.Ui.Objects.CategoryHeader
import P1_StartupScreen.Ui.Objects.ScrolleAdBanner
import a_RoomDB.ArticlesBasesStatsTable
import a_RoomDB.CategoriesTabelle
import android.util.Log
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
import androidx.compose.runtime.remember
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
private const val TAG = "ArticleGridDebug"


// Update the ArticleGrid to track first visible item
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
    val pagingConfig = PagingConfig(
        pageSize = 3,
        enablePlaceholders = true,
        prefetchDistance = 2
    )

    // Create separate pagers for each category
    val categoryPagers = remember(uiState.categories, filterText) {
        uiState.categories.associateWith { category ->
            Pager(pagingConfig) {
                ArticlePagingSource(
                    articles = if (category.nomCategorieInCategoriesTabele == "NewArrivale") {
                        uiState.articlesBasesStatTables.filter { it.itsNewArrivale }
                    } else {
                        uiState.articlesBasesStatTables.filter {
                            it.nomCategorie == category.nomCategorieInCategoriesTabele && !it.itsNewArrivale
                        }
                    },
                    filterText = filterText
                )
            }
        }
    }

    // Create a map of category to their respective LazyPagingItems
    val categoryPagingItems = remember(categoryPagers) {
        mutableMapOf<CategoriesTabelle, LazyPagingItems<ArticlesBasesStatsTable>>()
    }

    // Collect paging items for each category
    categoryPagers.forEach { (category, pager) ->
        val pagingItems = pager.flow.collectAsLazyPagingItems()
        categoryPagingItems[category] = pagingItems
    }

    // Log grid state changes
    LaunchedEffect(gridState) {
        snapshotFlow { gridState.firstVisibleItemIndex }
            .collect { firstVisibleIndex ->
                Log.d(TAG, """
                    Grid State Update:
                    - First Visible Index: $firstVisibleIndex
                    - Is Host: ${uiState.productDisplayController.isHostPhone}
                    - Total Categories: ${uiState.categories.size}
                """.trimIndent())
            }
    }

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(if (uiState.categories.any {
                it.nomCategorieInCategoriesTabele == "NewArrivale"
            }) gridColumns else 2),
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
                        .padding(bottom = 8.dp)
                )
            }
        }

        // Display categories in order, with NewArrivale first
        uiState.categories.sortedBy {
            if (it.nomCategorieInCategoriesTabele == "NewArrivale") 0 else 1
        }.forEach { category ->
            val lazyPagingItems = categoryPagingItems[category]

            if (lazyPagingItems != null && lazyPagingItems.itemCount > 0) {
                Log.d(TAG, """
                    Category Layout:
                    - Category: ${category.nomCategorieInCategoriesTabele}
                    - Items Count: ${lazyPagingItems.itemCount}
                """.trimIndent())

                // Show category header if needed
                if (category.displayedHeader) {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        CategoryHeader(category)
                    }
                }

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
                        val isFirstVisible = index == gridState.firstVisibleItemIndex

                        if (isFirstVisible) {
                            Log.d(TAG, """
                                First Visible Article:
                                - Article ID: ${it.idArticle}
                                - Index: $index
                                - Category: ${category.nomCategorieInCategoriesTabele}
                            """.trimIndent())
                        }

                        ArticleItem(
                            article = it,
                            viewModel = viewModel,
                            reloadTrigger = reloadTrigger,
                            onClickToOpenWindos = onClickToOpenWindos,
                            uiState = uiState,
                            isFirstVisible = isFirstVisible
                        )
                    }
                }
            }
        }
    }
}
