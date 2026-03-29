package Application4.App.Fragment.ID1.Fragment

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import Application4.App.Fragment.ID1.Fragment.ViewModel.Z.Archive.UiState_NewProtoPatterns
import EntreApps.Shared.Models.M01Produit
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.Dialogs.Dialog_Fast_Affiche_Panie.Dialogs.Dialog_Fast_Affiche_Panie
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

private const val TAG_CONTENT = "Content_FragID4"

/**
 * Content
 *
 * groupe_Par_Catalogue removed — Etager_LazyColumn now builds the
 * catalogue→category→product→colour tree on-demand from active_Datas directly,
 * sorted by classement_By_FilterKeys_M3.
 */
@Composable
fun Content(
    modifier: Modifier = Modifier,
    on_pour_send_data: (String, String) -> Unit,
    onClickImageToShowControles: () -> Unit,
    onProductCategoryClick: (M01Produit) -> Unit,
    justMovedProductKeyID: String?,
    viewModel: A_ViewModel_NewProtoPatterns,
    uiStateNewProtoPatterns: UiState_NewProtoPatterns
) {
    val affiche_Dialog_Fast_Affiche_Panie =
        viewModel.active_Datas.affiche_Dialog_Fast_Affiche_Panie

    Etager_LazyColumn(
        modifier = modifier,
        on_pour_send_data = on_pour_send_data,
        onProductCategoryClick = onProductCategoryClick,
        justMovedProductKeyID = justMovedProductKeyID,
        uiState_NewProtoPatterns_viewModel = Pair(uiStateNewProtoPatterns, viewModel),
    )

    affiche_Dialog_Fast_Affiche_Panie?.ifTrue {
        Dialog_Fast_Affiche_Panie()
    }
}
