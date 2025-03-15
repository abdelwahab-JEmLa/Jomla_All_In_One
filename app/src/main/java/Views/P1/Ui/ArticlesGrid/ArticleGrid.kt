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
    a_ProduitModelRepository: A_ProduitModelRepository,
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
            targetCategoryId = targetCategoryId,
            a_ProduitModelRepository = a_ProduitModelRepository,
            lockHost = lockHost,
            viewModelInitApp = viewModelInitApp
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
    viewModelInitApp: ViewModelInitApp
) {
    // Track scroll state and first visible item
    var lastSettledFirstVisible by remember { mutableStateOf(-1) }
    var isSettled by remember { mutableStateOf(true) }
    var currentCategory by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

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
    // Calculate item positions for categories
    // Handle scrolling to target category
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

    // Calculate item positions for categories
// Calculate item positions for categories
    val categoryPositions = remember(uiState.categories, categoryPagingItems) {
        val positions = mutableMapOf<Long, Int>()
        var currentPosition = 0

        Log.d("ScrollDebug", "Recalculating category positions")

        // Add position for banner if present
        if (!showFilter) {
            currentPosition += 1
            Log.d("ScrollDebug", "Added position for banner: $currentPosition")
        }

        // Log all categories to verify they're being processed
        Log.d("ScrollDebug", "Categories to process: ${uiState.categories.map { it.idCategorieInCategoriesTabele }}")

        uiState.categories
            .sortedBy { if (it.nomCategorieInCategoriesTabele == "NewArrivale") 0 else 1 }
            .forEach { category ->
                val itemCount = categoryPagingItems[category]?.itemCount ?: 0
                Log.d("ScrollDebug", "Category ${category.idCategorieInCategoriesTabele} has $itemCount items")

                if (itemCount > 0) {
                    // Add position for category header if displayed
                    if (category.displayedHeader) {
                        positions[category.idCategorieInCategoriesTabele] = currentPosition
                        Log.d("ScrollDebug", "Added position for category ${category.idCategorieInCategoriesTabele}: $currentPosition")
                        currentPosition += 1
                    }

                    // Add positions for items in the category
                    currentPosition += itemCount
                    Log.d("ScrollDebug", "Updated position after adding items: $currentPosition")
                }
            }

        Log.d("ScrollDebug", "Final category positions: $positions")
        positions
    }

    // Handle scrolling to target category
    LaunchedEffect(targetCategoryId.value) {
        targetCategoryId.value?.let { id ->
            // Special handling for categories with IDs 148, 149, and 150
            if (id == 148L || id == 149L || id == 150L) {
                // Find the position of the target category
                val position = categoryPositions[id]
                if (position != null) {
                    coroutineScope.launch {
                        gridState.scrollToItem(position)
                        // Reset the target category ID after scrolling
                        delay(500) // Give time for scrolling to complete
                        targetCategoryId.value = null
                    }
                }
            }
        }
    }

    LaunchedEffect(targetCategoryId.value) {
        val targetId = targetCategoryId.value
        Log.d("ScrollDebug", "LaunchedEffect triggered with targetId: $targetId")

        targetId?.let { id ->
            // Log the category positions to verify they're calculated correctly
            Log.d("ScrollDebug", "Current category positions: $categoryPositions")

            // Special handling for categories with IDs 148, 149, and 150
            if (id == 148L || id == 149L || id == 150L) {
                // Find the position of the target category
                val position = categoryPositions[id]
                Log.d("ScrollDebug", "Found position $position for category ID $id")

                if (position != null) {
                    try {
                        Log.d("ScrollDebug", "Attempting to scroll to position $position")
                        gridState.scrollToItem(position)
                        Log.d("ScrollDebug", "Scroll to position $position completed")

                        // Reset the target category ID after scrolling
                        delay(500) // Give time for scrolling to complete
                        Log.d("ScrollDebug", "Resetting targetCategoryId after delay")
                        targetCategoryId.value = null
                    } catch (e: Exception) {
                        Log.e("ScrollDebug", "Error during scroll operation", e)
                    }
                } else {
                    Log.e("ScrollDebug", "Position not found for category ID $id")
                }
            } else {
                Log.d("ScrollDebug", "Category ID $id is not 148, 149, or 150, not scrolling")
            }
        } ?: Log.d("ScrollDebug", "No target category ID set")
    }

    // Find the template categories with IDs 148, 149, and 150
    val categoryTemplate148 = uiState.categories.find { it.idCategorieInCategoriesTabele == 148L }
    val categoryTemplate149 = uiState.categories.find { it.idCategorieInCategoriesTabele == 149L }
    val categoryTemplate150 = uiState.categories.find { it.idCategorieInCategoriesTabele == 150L }

    // Create new categories based on templates if found, with fallback to defaults
    val categoryHeaderConsmitiques = categoryTemplate148?.let {
        CategoriesTabelle(
            idCategorieInCategoriesTabele = 1000L, // Use a unique ID that won't conflict
            nomCategorieInCategoriesTabele = it.nomCategorieInCategoriesTabele,
            idClassementCategorieInCategoriesTabele = it.idClassementCategorieInCategoriesTabele,
            displayedHeader = true
        )
    } ?: CategoriesTabelle(
        idCategorieInCategoriesTabele = 1000L,
        nomCategorieInCategoriesTabele = "Search Results",
        idClassementCategorieInCategoriesTabele = 1,
        displayedHeader = true
    )

    val categoryHeaderConfiseries = categoryTemplate149?.let {
        CategoriesTabelle(
            idCategorieInCategoriesTabele = 1001L,
            nomCategorieInCategoriesTabele = it.nomCategorieInCategoriesTabele,
            idClassementCategorieInCategoriesTabele = it.idClassementCategorieInCategoriesTabele,
            displayedHeader = true // Always make header visible
        )
    } ?: CategoriesTabelle(
        idCategorieInCategoriesTabele = 1001L,
        nomCategorieInCategoriesTabele = "Recommended Items",
        idClassementCategorieInCategoriesTabele = 2,
        displayedHeader = true
    )

    val categoryHeaderTeBnages = categoryTemplate150?.let {
        CategoriesTabelle(
            idCategorieInCategoriesTabele = 1002L,
            nomCategorieInCategoriesTabele = it.nomCategorieInCategoriesTabele,
            idClassementCategorieInCategoriesTabele = it.idClassementCategorieInCategoriesTabele,
            displayedHeader = true // Always make header visible
        )
    } ?: CategoriesTabelle(
        idCategorieInCategoriesTabele = 1002L,
        nomCategorieInCategoriesTabele = "Featured Items",
        idClassementCategorieInCategoriesTabele = 3,
        displayedHeader = true
    )

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
        // Display categories in order, including custom categories when filtering is not active
        val allCategories = if (!showFilter) {
            // Add custom categories to the regular categories
            val customCategories = listOf(
                categoryHeaderConsmitiques,
                categoryHeaderConfiseries,
                categoryHeaderTeBnages
            )
            (customCategories + uiState.categories)
                .sortedBy { category ->
                    // Sort by classification ID, with "NewArrivale" always first
                    when {
                        category.nomCategorieInCategoriesTabele == "NewArrivale" -> 0
                        else -> category.idClassementCategorieInCategoriesTabele
                    }
                }
        } else {
            // Just use regular categories when filtering
            uiState.categories
                .sortedBy { if (it.nomCategorieInCategoriesTabele == "NewArrivale") 0 else 1 }
        }

        // Show banner if not filtering
        if (!showFilter) {
            item(span = StaggeredGridItemSpan.FullLine) {
                ScrolleAdBanner(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    onBannerClick = { bannerIndex ->
                        // Find the category with the name "Confiseries" if banner index is 0
                        if (bannerIndex == 0) {
                            val confiseriesCategory = uiState.categories.find {
                                it.nomCategorieInCategoriesTabele == "Confiseries"
                            }
                            confiseriesCategory?.let { category ->
                                // Scroll to this category
                                targetCategoryId.value = category.idCategorieInCategoriesTabele
                            }
                        }
                    }
                )
            }
        }

        // Display categories in order
        allCategories.forEach { category ->
            // Skip custom categories if we're filtering
            if (showFilter && (category.idCategorieInCategoriesTabele in listOf(
                    1000L,
                    1001L,
                    1002L
                ))
            ) {
                return@forEach
            }

            // Get the paging items for this category
            val lazyPagingItems = categoryPagingItems[category]

            // For custom categories, we might not have paging items yet
            val shouldShowCategory =
                category.idCategorieInCategoriesTabele in listOf(1000L, 1001L, 1002L) ||
                        (lazyPagingItems != null && lazyPagingItems.itemCount > 0)

            // Only display category content if it has items or is a custom category
            if (shouldShowCategory) {
                // Show category header if needed
                if (category.displayedHeader) {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        CategoryHeader(category)
                    }
                }

                // Skip item display for custom categories without items
                if (category.idCategorieInCategoriesTabele !in listOf(1000L, 1001L, 1002L) &&
                    lazyPagingItems != null && lazyPagingItems.itemCount > 0
                ) {

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
                                produitDepuitNewDATABASE = produitDepuitNewDATABASE,
                                lockHost = lockHost,
                                viewModelInitApp = viewModelInitApp
                            )
                        }
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

data class ScrollState(
    val index: Int,
    val isScrolling: Boolean
)
