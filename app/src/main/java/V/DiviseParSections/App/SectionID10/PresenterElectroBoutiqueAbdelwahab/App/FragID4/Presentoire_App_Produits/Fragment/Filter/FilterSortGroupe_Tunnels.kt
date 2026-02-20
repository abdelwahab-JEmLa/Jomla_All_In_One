package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.Fragment.Filter

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.Dialogs.Dialog_Fast_Affiche_Panie.Dialogs.Dialog_Fast_Affiche_Panie
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.Fragment.Etager_LazyColumn_FragID4
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.M16CategorieProduit
import V.DiviseParSections.App.Shared.Repository.Repo21.Repository.M21CataloguesCategorie
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import V.DiviseParSections.App.Shared.ViewModel.HeadViewModel
import org.koin.compose.koinInject

/**
 * FilterSortGroupe_Tunnels - Compact filtering and sorting orchestration with Catalogue support
 * UPDATED: Now handles Catalogue -> Category -> Product -> Colors hierarchy
 * FIXED: Extracted filter logic to FilterTunnel (now a regular function, not @Composable)
 */
@Composable
fun FilterSortGroupe_Tunnels(
    modifier: Modifier = Modifier,
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    repositorysMainGetter: RepositorysMainGetter = koinInject(),
    groupe_Par_Catalogue: List<Pair<M21CataloguesCategorie, List<Pair<M16CategorieProduit, List<Pair<M01Produit, List<M3CouleurProduitInfos>>>>>>>,
    viewModelHeadViewModel: HeadViewModel,
    on_pour_send_data: (String, String) -> Unit,
    onClickImageToShowControles: () -> Unit,
    onProductCategoryClick: (M01Produit) -> Unit,
    justMovedProductKeyID: String?,
    isWifiClientConnected_1: Boolean
) {
    val currentAppCompt = focusedValuesGetter.currentActive_M9AppCompt
    val filterState = focusedValuesGetter.active_Central_Values.filterState_Facad_Boutique
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
        filteredProducts = filteredProducts,
        sortOrder = filterState.sortOrderFacadeBoutique,
        enableCategoryGrouping = filterState.enableCategoryGrouping,
        repositorysMainGetter = repositorysMainGetter
    )

    // Render
    Etager_LazyColumn_FragID4(
        modifier = modifier.semantics(mergeDescendants = true) {
            set(value = catalogueFilter, key = SemanticsPropertyKey("catalogueFilter"))
        },
        cataloguesWithCategoriesAndProducts = sortedProducts,
        viewModelHeadViewModel = viewModelHeadViewModel,
        on_pour_send_data = on_pour_send_data,
        onClickImageToShowControles = onClickImageToShowControles,
        onProductCategoryClick = onProductCategoryClick,
        justMovedProductKeyID = justMovedProductKeyID,
        repositorysMainGetter = repositorysMainGetter,
        isWifiClientConnected_1=isWifiClientConnected_1,
    )

    // Dialogs
    focusedValuesGetter.active_Central_Values.affiche_Dialog_Fast_Affiche_Panie.ifTrue {
        Dialog_Fast_Affiche_Panie()
    }

    focusedValuesGetter.active_Central_Values.filterState_Facad_Boutique?.affiche_dialog_editeur?.ifTrue {
        FilterDropdownMenu_Its_FacadeElectroBoutique(
            onDismiss = {
                focusedValuesGetter.update_activeCentralValues(
                    focusedValuesGetter.active_Central_Values.copy(
                        filterState_Facad_Boutique = focusedValuesGetter.active_Central_Values.filterState_Facad_Boutique
                            ?.copy(affiche_dialog_editeur = false)
                    )
                )
            }
        )
    }
}

