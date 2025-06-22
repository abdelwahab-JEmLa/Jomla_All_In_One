package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.ViewModel.Repository.BProduitDataBaseComposeRepositoryPJ17
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.ViewModel.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Filter.Models.FilterState
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.B_MainItem.ProductItem
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.ViewModel.Sec9FragId1ViewId2ViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Composable
fun EditeInfosMainList(
    modifier: Modifier = Modifier,
    filteredAndSortedProduitList: List<ArticlesBasesStatsTable>,
    aProduitdatabasecomposerepositorypj17: BProduitDataBaseComposeRepositoryPJ17,
    viewModel: Sec9FragId1ViewId2ViewModel = koinViewModel(),
    filterState: FilterState,
) {
    val uiState by viewModel.uiState.collectAsState()
    val shouldHideQuickInfoCards = filterState.hideQuiNeSontPas_cUnNeveauArrivage
    val lazyListState = rememberLazyListState()

    // Create focus requesters for each product item
    val focusRequesters = remember(filteredAndSortedProduitList.size) {
        List(filteredAndSortedProduitList.size) { FocusRequester() }
    }

    LazyColumn(
        modifier = modifier,
        state = lazyListState,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(
            items = filteredAndSortedProduitList,
            key = { it.id }
        ) { produit ->
            val currentIndex = filteredAndSortedProduitList.indexOf(produit)
            val nextIndex = currentIndex + 1

            // Determine which focus requester to use for next navigation
            val onNextField: (() -> Unit)? = if (shouldHideQuickInfoCards && nextIndex < focusRequesters.size) {
                { focusRequesters[nextIndex].requestFocus() }
            } else null

            ProductItem(
                viewModel=viewModel,
                shouldHideQuickInfoCards = shouldHideQuickInfoCards,
                uiState = uiState,
                mainComposRepository = aProduitdatabasecomposerepositorypj17,
                produit = produit,
                onNextField = onNextField,
                focusRequester = focusRequesters.getOrNull(currentIndex)
            )
        }
    }
}

