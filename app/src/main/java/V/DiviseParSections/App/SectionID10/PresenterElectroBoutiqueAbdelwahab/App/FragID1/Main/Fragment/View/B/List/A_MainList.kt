package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.B.List

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.PresenterElectroBoutiqueAbdelwahabSec10Frag1ViewModel
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.B.List.Components.CategoryHeader
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.B.List.Components.ScrolleAdBanner
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.A.View.ArticleItem
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.D.Filter.filterArticles
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import EntreApps.Shared.Models.Relative_Produits.Models.M16CategorieProduit
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import V.DiviseParSections.App.Shared.ViewModel.HeadViewModel
import V.DiviseParSections.App.Shared.ViewModel.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun MainList(
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    viewModel: PresenterElectroBoutiqueAbdelwahabSec10Frag1ViewModel,
    viewModelInitApp: ViewModelInitApp,
    headViewModelViewModel: HeadViewModel,
    produits: List<M01Produit>,
    uiState: UiState,
    filterText: String,
    showFilter: Boolean,
    gridState: LazyStaggeredGridState,
    reloadTrigger: Int,
    modifier: Modifier = Modifier,
    onClickToOpenWindos: (M01Produit, Int) -> Unit,
    currentClient: M2Client?,
    lockHost: Boolean,
    
    onClickImageToShowControles: () -> Unit,
) {
    val expanded_M3CouleurProduitInfos = focusedValuesGetter.active_Central_Values.expanded_M3CouleurProduitInfos
    val coroutineScope = rememberCoroutineScope()

    val categories = viewModel.getter.repoM16CategorieProduit.datasValue
    val filteredArticles = remember(produits, filterText, currentClient) {
        filterArticles(produits, filterText, aCentralFacade)
    }

    val articlesByCategory = remember(filteredArticles, categories) {
        val sortedCategories = categories.sortedWith(
            compareBy<M16CategorieProduit> { category ->
                category.positionDouble
            }.thenByDescending {
                it.creationTimestamp
            }.thenBy {
                it.nom
            }
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

            articlesForCategory.sortedWith(
                compareBy<M01Produit> { article ->
                    article.positionDonSonCesFrereCategorieProduits
                }.thenByDescending { article ->
                    article.creationTimestamp
                }
            )
        }.filterValues { it.isNotEmpty() }
    }

    var lastSettledFirstVisible by remember { mutableStateOf(-1) }
    var isSettled by remember { mutableStateOf(true) }
    var currentCategory by remember { mutableStateOf<String?>(null) }

    // State variable to track when scroll lock should expire
    var scrollLockUntil by remember { mutableStateOf(0L) }

    LaunchedEffect(expanded_M3CouleurProduitInfos) {
        expanded_M3CouleurProduitInfos?.let { expandedColor ->
            var currentIndex = if (!showFilter) 1 else 0
            var foundIndex = -1

            for ((category, articles) in articlesByCategory) {
                if (category.displayedHeader) {
                    currentIndex++ // Header takes one slot
                }

                val articleIndex = articles.indexOfFirst {
                    it.id == expandedColor.parentBProduitOldID
                }

                if (articleIndex != -1) {
                    foundIndex = currentIndex + articleIndex
                    break
                }

                currentIndex += articles.size
            }

            if (foundIndex != -1) {
                delay(100) // Small delay to ensure layout is complete

                // Set lock time to 2 seconds from now
                scrollLockUntil = System.currentTimeMillis() + 2000

                coroutineScope.launch {
                    gridState.animateScrollToItem(foundIndex)
                }
            }
        }
    }

    // Enforce scroll lock: keep scroll position for 2 seconds
    LaunchedEffect(gridState, scrollLockUntil) {
        snapshotFlow { gridState.firstVisibleItemIndex }
            .collect { currentIndex ->
                val now = System.currentTimeMillis()

                // If we're still in lock period
                if (now < scrollLockUntil) {
                    expanded_M3CouleurProduitInfos?.let { expandedColor ->
                        var targetIndex = if (!showFilter) 1 else 0
                        var found = false

                        for ((category, articles) in articlesByCategory) {
                            if (category.displayedHeader) {
                                targetIndex++
                            }

                            val articleIndex = articles.indexOfFirst {
                                it.id == expandedColor.parentBProduitOldID
                            }

                            if (articleIndex != -1) {
                                targetIndex += articleIndex
                                found = true
                                break
                            }

                            targetIndex += articles.size
                        }

                        // If user scrolled away from target, scroll back
                        if (found && currentIndex != targetIndex) {
                            delay(50) // Small delay to avoid too rapid corrections
                            gridState.animateScrollToItem(targetIndex)
                        }
                    }
                }
            }
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
                span = { article ->
                    val isExpanded = expanded_M3CouleurProduitInfos?.let { expandedColor ->
                        expandedColor.parentBProduitOldID == article.id
                    } ?: false

                    if (isExpanded) {
                        StaggeredGridItemSpan.FullLine
                    } else {
                        StaggeredGridItemSpan.SingleLane
                    }
                }
            ) { article ->
                val isFirstVisible = when {
                    !isSettled -> articles.indexOf(article) == lastSettledFirstVisible
                    else -> articles.indexOf(article) == gridState.firstVisibleItemIndex
                }

                if (isFirstVisible) {
                    currentCategory = category.nom
                }

                val isExpanded = expanded_M3CouleurProduitInfos?.let { expandedColor ->
                    expandedColor.parentBProduitOldID == article.id
                } ?: false

                val expandedColorIndex = if (isExpanded) {
                    expanded_M3CouleurProduitInfos?.indexCouleurDansAncienProto
                } else null

                val elevation by animateDpAsState(
                    targetValue = if (isExpanded) 12.dp else 4.dp,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "elevation"
                )

                ArticleItem(
                    relative_M1produit = article,
                    viewModel = viewModel,
                    viewModelheadViewModelViewModel = headViewModelViewModel,
                    viewModelInitApp = viewModelInitApp,
                    reloadTrigger = reloadTrigger,
                    modifier = Modifier
                        .animateItem(
                            fadeInSpec = null,
                            fadeOutSpec = null
                        ),
                    uiState = uiState,
                    isFirstVisible = isFirstVisible,
                    lockHost = lockHost,
                    onClickToOpenWindos = onClickToOpenWindos,
                    isExpanded = isExpanded,
                    expandedElevation = elevation,
                    expandedColorIndex = expandedColorIndex,
                    
                )
            }
        }
    }
}

private data class ScrollState(
    val index: Int,
    val isScrolling: Boolean
)
