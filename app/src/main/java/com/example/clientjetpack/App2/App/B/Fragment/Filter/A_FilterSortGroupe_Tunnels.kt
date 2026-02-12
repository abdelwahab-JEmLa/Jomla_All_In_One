package com.example.clientjetpack.App2.App.B.Fragment.Filter

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.Dialogs.Dialog_Fast_Affiche_Panie.Dialogs.Dialog_Fast_Affiche_Panie
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.CategoriesTabelle
import V.DiviseParSections.App.Shared.Repository.Repo21.Repository.CataloguesCaegorie
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import com.example.clientjetpack.App2.App.A.Main.Base.Modules.WifiConexiontLuncher
import com.example.clientjetpack.App2.App.A.Main.Base.Repository.FocusedValuesGetter_app2
import com.example.clientjetpack.App2.App.A.Main.Base.Repository.RepositorysMainGetter_app2
import com.example.clientjetpack.App2.App.A.Main.Base.Repository.RepositorysMainGetter_app2.Companion.ifTrue
import com.example.clientjetpack.App2.App.B.Fragment.Etager_LazyColumn_App2
import org.koin.compose.koinInject

/**
 * FilterSortGroupe_Tunnels - Compact filtering and sorting orchestration with Catalogue support
 * UPDATED: Now handles Catalogue -> Category -> Product -> Colors hierarchy
 * FIXED: Extracted filter logic to FilterTunnel (now a regular function, not @Composable)
 */
@Composable
fun FilterSortGroupe_Tunnels_app2(
    modifier: Modifier = Modifier,
    FocusedValuesGetter_app2: FocusedValuesGetter_app2 = koinInject(),
    RepositorysMainGetter_app2: RepositorysMainGetter_app2 = koinInject(),
    groupe_Par_Catalogue: List<Pair<CataloguesCaegorie, List<Pair<CategoriesTabelle, List<Pair<ArticlesBasesStatsTable, List<M3CouleurProduitInfos>>>>>>>,
    viewModelWifiConexiontLuncher: WifiConexiontLuncher,
    on_pour_send_data: (String, String) -> Unit,
    onClickImageToShowControles: () -> Unit,
    onProductCategoryClick: (ArticlesBasesStatsTable) -> Unit,
    justMovedProductKeyID: String?,
    isWifiClientConnected_1: Boolean
) {
    val currentAppCompt = FocusedValuesGetter_app2.currentActive_M9AppCompt
    val filterState = FocusedValuesGetter_app2.active_Central_Values.FilterState_Facad_Boutique_app2
        ?: FilterState_Facad_Boutique_app2()
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
        RepositorysMainGetter_app2 = RepositorysMainGetter_app2
    )

    // Render
    Etager_LazyColumn_App2(
        modifier = modifier.semantics(mergeDescendants = true) {
            set(value = catalogueFilter, key = SemanticsPropertyKey("catalogueFilter"))
        },
        cataloguesWithCategoriesAndProducts = sortedProducts,
        viewModelHeadViewModel_App2 = viewModelWifiConexiontLuncher,
        on_pour_send_data = on_pour_send_data,
        onClickImageToShowControles = onClickImageToShowControles,
        onProductCategoryClick = onProductCategoryClick,
        justMovedProductKeyID = justMovedProductKeyID,
        RepositorysMainGetter_app2 = RepositorysMainGetter_app2,
        isWifiClientConnected_1=isWifiClientConnected_1,
    )

    // Dialogs
    FocusedValuesGetter_app2.active_Central_Values.affiche_Dialog_Fast_Affiche_Panie.ifTrue {
        Dialog_Fast_Affiche_Panie()
    }

    FocusedValuesGetter_app2.active_Central_Values.FilterState_Facad_Boutique_app2?.affiche_dialog_editeur?.ifTrue {
        FilterDropdownMenu_Its_FacadeElectroBoutique(
            onDismiss = {
                FocusedValuesGetter_app2.update_ActiveCentralValues_app2(
                    FocusedValuesGetter_app2.active_Central_Values.copy(
                        FilterState_Facad_Boutique_app2 = FocusedValuesGetter_app2.active_Central_Values.FilterState_Facad_Boutique_app2
                            ?.copy(affiche_dialog_editeur = false)
                    )
                )
            }
        )
    }
}

