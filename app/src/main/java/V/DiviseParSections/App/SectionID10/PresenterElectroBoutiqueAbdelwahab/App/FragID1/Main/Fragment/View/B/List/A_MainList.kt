package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.B.List

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.PresenterElectroBoutiqueAbdelwahabSec10Frag1ViewModel
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.B.List.Components.CategoryHeader
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.B.List.Components.ScrolleAdBanner
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.A.View.ArticleItem
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.D.Filter.filterArticles
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.CategoriesTabelle
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
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
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.clientjetpack.ViewModel.HeadViewModel
import com.example.clientjetpack.ViewModel.UiState
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

@Composable
fun MainList(
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    viewModel: PresenterElectroBoutiqueAbdelwahabSec10Frag1ViewModel,
    viewModelInitApp: ViewModelInitApp,
    headViewModelViewModel: HeadViewModel,
    produits: List<ArticlesBasesStatsTable>,
    uiState: UiState,
    filterText: String,
    showFilter: Boolean,
    gridState: LazyStaggeredGridState,
    reloadTrigger: Int,
    modifier: Modifier = Modifier,
    onClickToOpenWindos: (ArticlesBasesStatsTable, Int) -> Unit,
    currentClient: M2Client?,
    lockHost: Boolean,
    on_pour_send_data: (String, String) -> Unit,
    onClickImageToShowControles: () -> Unit,
) {
    // Get the expanded color info
    val expanded_M3CouleurProduitInfos = focusedValuesGetter.active_Central_Values.expanded_M3CouleurProduitInfos

    val categories = viewModel.getter.repoM16CategorieProduit.datasValue
    val filteredArticles = remember(produits, filterText, currentClient) {
        filterArticles(produits, filterText, aCentralFacade)
    }

    val articlesByCategory = remember(filteredArticles, categories) {
        val sortedCategories = categories.sortedWith(
            compareBy<CategoriesTabelle> { category ->
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
                compareBy<ArticlesBasesStatsTable> { article ->
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
                // FIXED: Dynamic span based on expanded state
                span = { article ->
                    val isExpanded = expanded_M3CouleurProduitInfos?.let { expandedColor ->
                        // Check if this article contains the expanded color
                        expandedColor.parentBProduitOldID == article.id
                    } ?: false

                    if (isExpanded) {
                        StaggeredGridItemSpan.FullLine // Full width when expanded
                    } else {
                        StaggeredGridItemSpan.SingleLane // Normal width
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

                // Check if this article is expanded
                val isExpanded = expanded_M3CouleurProduitInfos?.let { expandedColor ->
                    expandedColor.parentBProduitOldID == article.id
                } ?: false

                // Get the expanded color index for this article
                val expandedColorIndex = if (isExpanded) {
                    expanded_M3CouleurProduitInfos?.indexCouleurDansAncienProto
                } else null

                // Animate elevation when expanded
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
                    expandedColorIndex = expandedColorIndex
                , on_pour_send_data = on_pour_send_data
                )
            }
        }
    }
}

private data class ScrollState(
    val index: Int,
    val isScrolling: Boolean
)
