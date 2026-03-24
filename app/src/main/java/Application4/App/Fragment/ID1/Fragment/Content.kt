package Application4.App.Fragment.ID1.Fragment

import Application4.App.Fragment.ID1.Fragment.ViewModel.Model.UiState_NewProtoPatterns
import Application4.App.Fragment.ID1.Fragment.ViewModel.ViewModel_NewProtoPatterns
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M16CategorieProduit
import EntreApps.Shared.Models.M21CataloguesCategorie
import EntreApps.Shared.Models.M3CouleurProduitInfos
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.Dialogs.Dialog_Fast_Affiche_Panie.Dialogs.Dialog_Fast_Affiche_Panie
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics

private const val TAG_CONTENT = "Content_FragID4"

/**
 * FilterSortGroupe_Tunnels - Compact filtering and sorting orchestration with Catalogue support
 * UPDATED: Now handles Catalogue -> Category -> Product -> Colors hierarchy
 * FIXED: Extracted filter logic to FilterTunnel (now a regular function, not @Composable)
 */
@Composable
fun Content(
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
    val catalogueFilter = currentAppCompt?.presentoireEBoutiqueFilterProduitDuCatalogueAvecBsonObjectId

    // FIX TODO(1): diagnostic logs — trace why nothing displays in Etager_LazyColumn
    if (groupe_Par_Catalogue.isEmpty()) {
        Log.w(TAG_CONTENT, "⚠️ groupe_Par_Catalogue is EMPTY. Possible causes:" +
                "\n  1) list_filter_Priorite_M21Catalogues_To_M16Categories_To_M1Products_To_M03Couleur not yet populated (init still running?)" +
                "\n  2) flatten() in A_Compact_Presentoire returned empty (outer list is List<List<...>>)" +
                "\n  3) affiche_produits_Ou_On_TagPrioriter filter excludes all products" +
                "\n  4) No M16CategorieProduit rows match active_M21Catalogue.id=${viewModel.active_Datas.active_M21Catalogue.id}" +
                "\n  initDatasProgressEtate=${uiStateNewProtoPatterns.initDatasProgressEtate}" +
                "\n  mainInitDataBaseProgressEtate=${active_Central_Values.mainInitDataBaseProgressEtate}" +
                "\n  activeFilter=${viewModel.active_Datas.affiche_produits_Ou_On_TagPrioriter}"
        )
    } else {
        Log.d(TAG_CONTENT, "groupe_Par_Catalogue → ${groupe_Par_Catalogue.size} catalogue(s)")
        groupe_Par_Catalogue.forEach { (catalogue, cats) ->
            val totalProducts = cats.sumOf { (_, products) -> products.size }
            Log.d(TAG_CONTENT, "  📚 catalogue='${catalogue.nom}' id=${catalogue.id}" +
                    " → ${cats.size} catégorie(s), $totalProducts produit(s)")
            cats.forEach { (cat, products) ->
                Log.d(TAG_CONTENT, "    📂 category='${cat.nom}' id=${cat.id}" +
                        " displayedHeader=${cat.displayedHeader}" +
                        " → ${products.size} produit(s)")
            }
        }
    }

    Etager_LazyColumn(
        modifier = modifier.semantics(mergeDescendants = true) {
            set(value = catalogueFilter, key = SemanticsPropertyKey("catalogueFilter"))
        },
        cataloguesWithCategoriesAndProducts = groupe_Par_Catalogue,
        on_pour_send_data = on_pour_send_data,
        onProductCategoryClick = onProductCategoryClick,
        justMovedProductKeyID = justMovedProductKeyID,
        uiState_NewProtoPatterns_viewModel = Pair(uiStateNewProtoPatterns, viewModel),
    )

    active_Central_Values.affiche_Dialog_Fast_Affiche_Panie.ifTrue {
        Dialog_Fast_Affiche_Panie()
    }
}
