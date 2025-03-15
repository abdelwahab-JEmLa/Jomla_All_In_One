package Views.P1.Ui.ArticlesGrid

import Views.P1.Ui.ArticlesGrid.ArticleItem.ArticleItem
import Views.P1.Ui.ArticlesGrid.Res.Scrollbar
import Views.P1.Ui.Objects.CategoryHeader
import Views.P1.Ui.Objects.ScrolleAdBanner
import Views.P1._ArticlesStartFacade.ArticlePagingSource
import Z_CodePartageEntreApps.Model.A_ProduitModelRepository
import Z_CodePartageEntreApps.Model.B_ClientsDataBase
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Models.ArticlesBasesStatsTable
import Z_MasterOfApps.Z_AppsFather.Kotlin._1.Model.App.CategoriesTabelle
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

private const val TAG = "id2"

@Composable
fun ArticleGridScrollHandlerEffect(
    gridState: LazyStaggeredGridState,
    categoryPositions: Map<Long, Int>,
    scrollHandler: ScrollHandler = koinInject()
) {
    val coroutineScope = rememberCoroutineScope()
    val pendingScrolls = remember { mutableStateOf<Long?>(null) }

    // Monitor category positions for changes
    LaunchedEffect(categoryPositions) {
        if (categoryPositions.isNotEmpty()) {
            // Check if we have a pending scroll request
            val pendingId = pendingScrolls.value
            if (pendingId != null) {
                Log.d(TAG, "Positions now available, processing pending scroll to category $pendingId")

                // Check if the pending ID exists in the positions
                val position = categoryPositions[pendingId]
                if (position != null) {
                    Log.d(TAG, "Found position $position for pending category ID $pendingId")
                    coroutineScope.launch {
                        try {
                            gridState.scrollToItem(position)
                            Log.d(TAG, "Successfully scrolled to position $position from pending request")
                            // Clear the pending request
                            pendingScrolls.value = null
                            // Also reset the scrollHandler's value
                            delay(500)
                            scrollHandler.scrollToCategoryId.value = null
                        } catch (e: Exception) {
                            Log.e(TAG, "Error during pending scroll: ${e.message}", e)
                        }
                    }
                } else {
                    Log.w(TAG, "Pending category ID $pendingId still not found in updated positions map")
                }
            }
        }
    }

    LaunchedEffect(scrollHandler.scrollToCategoryId.value) {
        val categoryId = scrollHandler.scrollToCategoryId.value
        Log.d(TAG, "LaunchedEffect triggered with scrollToCategoryId: $categoryId")

        categoryId?.let { id ->
            // Special handling for categories with IDs 148, 149, and 150
            if (id == 148L || id == 149L || id == 150L) {
                Log.d(TAG, "Processing scroll request for category ID: $id")
                Log.d(TAG, "Available category positions: $categoryPositions")

                // Check if positions are available yet
                if (categoryPositions.isEmpty()) {
                    Log.d(TAG, "Category positions not yet available, storing request for later")
                    pendingScrolls.value = id
                    return@LaunchedEffect
                }

                // Find the position of the target category
                val position = categoryPositions[id]
                if (position != null) {
                    Log.d(TAG, "Found position $position for category ID $id, initiating scroll")
                    coroutineScope.launch {
                        try {
                            gridState.scrollToItem(position)
                            Log.d(TAG, "Successfully scrolled to position $position")
                            // Reset the target category ID after scrolling
                            delay(500) // Give time for scrolling to complete
                            Log.d(TAG, "About to reset scrollToCategoryId to null")
                            scrollHandler.scrollToCategoryId.value = null
                            Log.d(TAG, "Reset scrollToCategoryId to null")
                        } catch (e: Exception) {
                            Log.e(TAG, "Error during scrolling: ${e.message}", e)
                        }
                    }
                } else {
                    Log.w(TAG, "Category with ID $id not found in positions map")
                    // Save as pending instead of clearing
                    pendingScrolls.value = id
                }
            } else {
                Log.d(TAG, "Category ID $id is not one of the special categories (148, 149, 150)")
            }
        }
    }
}

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
    a_ProduitModelRepository: A_ProduitModelRepository,
    targetCategoryId: MutableState<Long?> = mutableStateOf(null),
    lockHost: Boolean,
    scrollHandler: ScrollHandler = koinInject()
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
            targetCategoryId = targetCategoryId,
            a_ProduitModelRepository = a_ProduitModelRepository,
            lockHost = lockHost,
            viewModelInitApp = viewModelInitApp,
            scrollHandler = scrollHandler
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
    a_ProduitModelRepository: A_ProduitModelRepository,
    lockHost: Boolean,
    viewModelInitApp: ViewModelInitApp,
    scrollHandler: ScrollHandler = koinInject()
) {
    // Track scroll state and first visible item
    var lastSettledFirstVisible by remember { mutableStateOf(-1) }
    var isSettled by remember { mutableStateOf(true) }
    var currentCategory by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Configure paging with assembly monitoring
    val pagingConfig = remember {
        Log.d(TAG, "Initializing paging configuration for grid assembly")
        PagingConfig(
            pageSize = 3,
            enablePlaceholders = true,
            prefetchDistance = 2
        )
    }

    // Create category pagers with assembly tracking
    val categoryPagers = remember(uiState.categories, filterText) {
        Log.d(
            TAG, """
            Starting Grid Assembly:
            - Total Categories: ${uiState.categories.size}
            - Filter: ${if (filterText.isEmpty()) "None" else filterText}
            - Articles Total: ${uiState.articlesBasesStatTables.size}
        """.trimIndent()
        )

        uiState.categories.associateWith { category ->
            Log.d(
                TAG, """
                Processing Category:
                - Name: ${category.nomCategorieInCategoriesTabele}
                - ID: ${category.idCategorieInCategoriesTabele}
                - Articles: ${
                    uiState.articlesBasesStatTables.count {
                        if (category.nomCategorieInCategoriesTabele == "NewArrivale")
                            it.itsNewArrivale
                        else
                            it.nomCategorie == category.nomCategorieInCategoriesTabele && !it.itsNewArrivale
                    }
                }
            """.trimIndent()
            )

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
                ).also { source ->
                    Log.d(
                        TAG,
                        "Created PagingSource for ${category.nomCategorieInCategoriesTabele}"
                    )
                }
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

                // Log settled position
                Log.d(
                    TAG, """
                    Scroll Settled:
                    - Index: ${scrollState.index}
                    - Previous Index: $lastSettledFirstVisible
                    - Category: $currentCategory
                """.trimIndent()
                )
            } else {
                isSettled = false
            }
        }
    }

    // Calculate item positions for categories
// Calculate item positions for categories
    val categoryPositions = remember(uiState.categories, categoryPagingItems) {
        val positions = mutableMapOf<Long, Int>()
        var currentPosition = 0

        // Add position for banner if present
        if (!showFilter) {
            currentPosition += 1
        }

        uiState.categories
            .sortedBy { if (it.nomCategorieInCategoriesTabele == "NewArrivale") 0 else 1 }
            .forEach { category ->
                val itemCount = categoryPagingItems[category]?.itemCount ?: 0

                // Always register position for special categories
                if ((category.idCategorieInCategoriesTabele == 148L ||
                            category.idCategorieInCategoriesTabele == 149L ||
                            category.idCategorieInCategoriesTabele == 150L) ||
                    (itemCount > 0 && category.displayedHeader)) {

                    positions[category.idCategorieInCategoriesTabele] = currentPosition
                    currentPosition += 1
                }

                // Add positions for items in the category
                if (itemCount > 0) {
                    currentPosition += itemCount
                }
            }

        Log.d(TAG, "Category positions calculated: $positions")
        positions
    }

    // Handle scrolling to target category
    LaunchedEffect(targetCategoryId.value) {
        targetCategoryId.value?.let { id ->
            // Special handling for categories with IDs 148, 149, and 150
            if (id == 148L || id == 149L || id == 150L) {
                Log.d(TAG, "Scrolling to category with ID: $id")

                // Find the position of the target category
                val position = categoryPositions[id]
                if (position != null) {
                    coroutineScope.launch {
                        gridState.scrollToItem(position)
                        // Reset the target category ID after scrolling
                        delay(500) // Give time for scrolling to complete
                        targetCategoryId.value = null
                    }
                } else {
                    Log.d(TAG, "Category with ID $id not found in positions map")
                }
            }
        }
    }

    // Use the ArticleGridScrollHandlerEffect to handle scroll requests
    ArticleGridScrollHandlerEffect(gridState, categoryPositions, scrollHandler)

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
                        .padding(bottom = 8.dp)
                )
            }
        }

        // Display categories in order
// In the ArticleGrid function, modify the code that displays categories

// Display categories in order
        uiState.categories
            .sortedBy { if (it.nomCategorieInCategoriesTabele == "NewArrivale") 0 else 1 }
            .forEach { category ->
                val lazyPagingItems = categoryPagingItems[category]
                val isSpecialCategory = category.idCategorieInCategoriesTabele in 148L..150L

                // Show category header if it has articles or is a special category
                if ((category.displayedHeader || isSpecialCategory) &&
                    ((lazyPagingItems?.itemCount ?: 0) > 0 || isSpecialCategory)
                ) {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        CategoryHeader(category)
                    }
                }

                // Display articles only if they exist
                if (lazyPagingItems != null && lazyPagingItems.itemCount > 0) {
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
                            val produitDepuitNewDATABASE = a_ProduitModelRepository
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
                                produitDepuitNewDATABASE=produitDepuitNewDATABASE,
                                lockHost = lockHost,
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
