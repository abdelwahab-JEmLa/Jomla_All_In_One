package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.Z.Option

import P0_MainScreen.Main.Main.Settings.UnderAll.Dialogs.Dialog_MainFastSearchProduitPourVent
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.ViewModel.ViewModelMainFastSearchProduitPourVent
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import androidx.compose.runtime.Composable

@Composable
fun DialogsSearchProduit(aCentralFacade: ACentralFacade) {
    val dialogAboveAll_OutlinedSearchListProduits= aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.active_Current_M9AppCompt
        ?.dialogAboveAll_OutlinedSearchListProduits

    if (dialogAboveAll_OutlinedSearchListProduits == true) {
        Dialog_MainFastSearchProduitPourVent(
            sourceLenceurDeCetteFragment =
                ViewModelMainFastSearchProduitPourVent.RoleDefinieParSourceACetteFragment.AfficheSearchAllProduits,
            focusedVarsHandlerFacade = aCentralFacade.focusedActiveValuesFacade,
        )
    }

    val produitName = (aCentralFacade
        .focusedActiveValuesFacade.focusedValuesGetter.active_Current_M9AppCompt?.startTextSearchM1Produit
        ?: "")

    if (aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.activeDialogSearchM1Produit) {
        Dialog_MainFastSearchProduitPourVent(
            sourceLenceurDeCetteFragment =
                ViewModelMainFastSearchProduitPourVent.RoleDefinieParSourceACetteFragment.SearchProduit(
                    aCentralFacade.repoMainGetter.repo1ProduitInfos.datasValue
                        .find { it.nom == produitName }!!
                ),
            focusedVarsHandlerFacade = aCentralFacade.focusedActiveValuesFacade,
        )
    }
}
