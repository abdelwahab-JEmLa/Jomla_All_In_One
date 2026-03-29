package Application4.App.Fragment.ID1.Fragment

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import Application4.App.Fragment.ID1.Fragment.ViewModel.Z.Archive.UiState_NewProtoPatterns
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M16CategorieProduit
import EntreApps.Shared.Models.M21CataloguesCategorie
import EntreApps.Shared.Models.M3CouleurProduitInfos
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.Dialogs.Dialog_Fast_Affiche_Panie.Dialogs.Dialog_Fast_Affiche_Panie
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
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
    viewModel: A_ViewModel_NewProtoPatterns,
    uiStateNewProtoPatterns: UiState_NewProtoPatterns
) {
    val affiche_Dialog_Fast_Affiche_Panie = viewModel.active_Datas.affiche_Dialog_Fast_Affiche_Panie
    val currentAppCompt = viewModel.active_Datas.active_M9Compt
    val catalogueFilter = currentAppCompt?.presentoireEBoutiqueFilterProduitDuCatalogueAvecBsonObjectId


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

    affiche_Dialog_Fast_Affiche_Panie?.ifTrue {
        Dialog_Fast_Affiche_Panie()
    }
}
