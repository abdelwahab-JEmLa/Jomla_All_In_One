package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.B.List

import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.CategoriesTabelle
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.PresenterElectroBoutiqueAbdelwahabSec10Frag1ViewModel
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.B.List.Components.CategoryHeader
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.B.List.Components.ScrolleAdBanner
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.ArticleItem
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.D.Filter.filterArticles
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
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

@Composable
fun MainList(
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
    onClickImageToShowControles: () -> Unit,
) {
    val categories = viewModel.getter.repoM16CategorieProduit.datasValue
    val filteredArticles = remember(produits, filterText, currentClient) { filterArticles(produits, filterText) }

    val articlesByCategory = remember(filteredArticles, categories) {
        val sortedCategories = categories.sortedWith(
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
                    viewModel=viewModel,
                    article = article,
                    viewModelheadViewModelViewModel = headViewModelViewModel,
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
