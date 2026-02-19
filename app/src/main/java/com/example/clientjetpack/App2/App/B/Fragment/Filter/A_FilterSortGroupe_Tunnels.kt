package com.example.clientjetpack.App2.App.B.Fragment.Filter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import com.example.clientjetpack.App2.App.A.Main.Base.Repository.FocusedValuesGetter_app2
import com.example.clientjetpack.App2.App.B.Fragment.Etager_LazyColumn_App2
import com.example.clientjetpack.App2.App.A.Main.App.ViewModel.UiState
import com.example.clientjetpack.App2.App.A.Main.App.ViewModel.ViewModel_MainFragment
import org.koin.compose.koinInject

/**
 * FilterSortGroupe_Tunnels - Compact filtering and sorting orchestration with Catalogue support
 * UPDATED: Now handles Catalogue -> Category -> Product -> Colors hierarchy
 * FIXED: Extracted filter logic to FilterTunnel (now a regular function, not @Composable)
 */
@Composable
fun FilterSortGroupe_Tunnels_app2(
    modifier: Modifier = Modifier,
    focusedValuesGetter_app2: FocusedValuesGetter_app2 = koinInject(),
    uiState: UiState,
    viewModel: ViewModel_MainFragment
) {
    val filterState = focusedValuesGetter_app2.active_Central_Values.filterState_Facad_Boutique_app2
        ?: FilterState_Facad_Boutique_app2()

    val filteredProducts = remember(
        uiState.list_grouped_datas,
        filterState.hide_non_couleurAuDepot,
        filterState.hideQuiNeSontPas_cUnNeveauArrivage,
        filterState.hidePetiteProbability,
        filterState.hidePrixAchatZero,
        filterState.hidePrixAchatPositif,
        filterState.hidePrixVenteZero,
        filterState.hidePrixVentePositif,
        filterState.hideHeldPrioriteDemandAuGrossist,
        filterState.hideNonHeldPrioriteDemandAuGrossist,
        filterState.searchText,
        filterState.prixAchatTimeFilterDays,
        filterState.enablePrixAchatTimeFilter,
        filterState.produit_a_Une_Couleur_Ac_Image
    ) {
        FilterTunnel(
            groupe_Par_Catalogue = uiState.list_grouped_datas,
            filterState = filterState
        )
    }

    // Apply sorting
    val sortedProducts = SortTunnel(
        filteredProducts = filteredProducts,
        sortOrder = filterState.sortOrderFacadeBoutique,
        enableCategoryGrouping = filterState.enableCategoryGrouping,
    )

    // Render
    Etager_LazyColumn_App2(
        modifier = modifier.semantics(mergeDescendants = true) {
        },
        cataloguesWithCategoriesAndProducts = sortedProducts,
    )
}

