package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.Z.Option

import P0_MainScreen.Main.Main.Settings.UnderAll.Dialogs.Dialog_MainFastSearchProduitPourVent
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import EntreApps.Shared.Models.Home.ActiveCentralValues
import androidx.compose.runtime.Composable

@Composable
fun DialogsSearchProduit(aCentralFacade: ACentralFacade) {
    val dialogAboveAll_OutlinedSearchListProduits =
        aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.currentActive_M9AppCompt
            ?.dialogAboveAll_OutlinedSearchListProduits

    if (dialogAboveAll_OutlinedSearchListProduits == true) {
        Dialog_MainFastSearchProduitPourVent(
            sourceLenceurDeCetteFragment =
                ActiveCentralValues.RoleDefinieParSourceACetteFragment.AfficheSearchAllProduits,
            focusedVarsHandlerFacade = aCentralFacade.focusedActiveValuesFacade,
        )
    }

    val produitName = (aCentralFacade
        .focusedActiveValuesFacade
        .focusedValuesGetter.currentActive_M9AppCompt
        ?.startTextSearchM1Produit
        ?: ""
            )

    if (aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.activeDialogSearchM1Produit) {
        // FIXED: Safe handling of the find operation
        val foundProduct = aCentralFacade.repositorysMainGetter.repo1ProduitInfos.datasValue
            .find { it.nom == produitName }

        // Only show dialog if product is found
        foundProduct?.let { product ->
            Dialog_MainFastSearchProduitPourVent(
                sourceLenceurDeCetteFragment =
                    ActiveCentralValues.RoleDefinieParSourceACetteFragment.SearchProduit(product),
                focusedVarsHandlerFacade = aCentralFacade.focusedActiveValuesFacade,
            )
        }
    }
}
