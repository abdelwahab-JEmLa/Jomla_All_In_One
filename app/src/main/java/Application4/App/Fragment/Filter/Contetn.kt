package Application4.App.Fragment.Filter

import Application4.App.Fragment.A.ViewModel.UiState_NewProtoPatterns
import Application4.App.Fragment.A.ViewModel.ViewModel_NewProtoPatterns
import Application4.App.Fragment.Etager_LazyColumn_FragID4
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M16CategorieProduit
import EntreApps.Shared.Models.M21CataloguesCategorie
import EntreApps.Shared.Models.M3CouleurProduitInfos
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.Dialogs.Dialog_Fast_Affiche_Panie.Dialogs.Dialog_Fast_Affiche_Panie
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics

/**
 * FilterSortGroupe_Tunnels - Compact filtering and sorting orchestration with Catalogue support
 * UPDATED: Now handles Catalogue -> Category -> Product -> Colors hierarchy
 * FIXED: Extracted filter logic to FilterTunnel (now a regular function, not @Composable)
 */
@Composable
fun Contetn(
    modifier: Modifier = Modifier,
    groupe_Par_Catalogue: List<Pair<M21CataloguesCategorie, List<Pair<M16CategorieProduit, List<Pair<M01Produit, List<M3CouleurProduitInfos>>>>>>>,
    on_pour_send_data: (String, String) -> Unit,
    onClickImageToShowControles: () -> Unit,
    onProductCategoryClick: (M01Produit) -> Unit,
    justMovedProductKeyID: String?,
    viewModel: ViewModel_NewProtoPatterns,
    uiStateNewProtoPatterns: UiState_NewProtoPatterns
) {

    val active_Central_Values = uiStateNewProtoPatterns.active_Central_Values
    val currentAppCompt = active_Central_Values.activeCompt
    val filterState = active_Central_Values.filterState_Facad_Boutique
        ?: FilterState_Facad_Boutique()
    val catalogueFilter = currentAppCompt?.presentoireEBoutiqueFilterProduitDuCatalogueAvecBsonObjectId

    // Apply all filters using the new FilterTunnel (regular function, not @Composable)
    val filteredProducts = remember(
        groupe_Par_Catalogue, catalogueFilter, filterState.hide_non_couleurAuDepot,
        filterState.hideQuiNeSontPas_cUnNeveauArrivage, filterState.hidePetiteProbability,
        filterState.hidePrixAchatZero, filterState.hidePrixAchatPositif,
        filterState.hidePrixVenteZero, filterState.hidePrixVentePositif,
        filterState.hideHeldPrioriteDemandAuGrossist, filterState.hideNonHeldPrioriteDemandAuGrossist,
        filterState.searchText, filterState.prixAchatTimeFilterDays, filterState.enablePrixAchatTimeFilter,
        filterState.produit_a_Une_Couleur_Ac_Image
    ) {
        FilterTunnel(
            groupe_Par_Catalogue = groupe_Par_Catalogue,
            catalogueFilter = catalogueFilter,
            filterState = filterState
        )
    }

    // Apply sorting
    val sortedProducts = SortTunnel(
        uiStateNewProtoPatterns=uiStateNewProtoPatterns,
        filteredProducts = filteredProducts,
        sortOrder = filterState.sortOrderFacadeBoutique,
        enableCategoryGrouping = filterState.enableCategoryGrouping,
    )

    // Render
    Etager_LazyColumn_FragID4(
        modifier = modifier.semantics(mergeDescendants = true) {
            set(value = catalogueFilter, key = SemanticsPropertyKey("catalogueFilter"))
        },
        cataloguesWithCategoriesAndProducts = sortedProducts,
        on_pour_send_data = on_pour_send_data,
        onProductCategoryClick = onProductCategoryClick,
        justMovedProductKeyID = justMovedProductKeyID,
        uiState_NewProtoPatterns_viewModel=Pair(uiStateNewProtoPatterns,viewModel),
    )

    // Dialogs
    active_Central_Values.affiche_Dialog_Fast_Affiche_Panie.ifTrue {
        Dialog_Fast_Affiche_Panie()
    }

    active_Central_Values.filterState_Facad_Boutique?.affiche_dialog_editeur?.ifTrue {
        FilterDropdownMenu_Its_FacadeElectroBoutique(
            onDismiss = {
                viewModel.update_activeCentralValues(
                    active_Central_Values.copy(
                        filterState_Facad_Boutique = active_Central_Values.filterState_Facad_Boutique
                            ?.copy(affiche_dialog_editeur = false)
                    )
                )
            }
        )
    }
}

