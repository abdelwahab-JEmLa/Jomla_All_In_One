package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.Components.Expand_Produit_Couleur

import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.a.toggle_update_expanded_M3CouleurProduitInfos
import EntreApps.Shared.Models.M3CouleurProduitInfos
import Z_CodePartageEntreApps.Modules.ModuleID1.WifiTransferDatas.Module.WifiUpdateClientDisplayerStats

fun updateExpandedCouleur(
    focusedValuesGetter: FocusedValuesGetter,
    relative_M3CouleurProduitInfos: M3CouleurProduitInfos,
    on_pour_send_data: (String, String) -> Unit,
) {
    toggle_update_expanded_M3CouleurProduitInfos(
        focusedValuesGetter = focusedValuesGetter,
        relative_M3CouleurProduitInfos = relative_M3CouleurProduitInfos
    )

    on_pour_send_data(
        WifiUpdateClientDisplayerStats.Update_ActiveCompt_active_ProduitKeyID_Au_DroopDown_PresenterEcran.prefix,
        relative_M3CouleurProduitInfos.keyID
    )
}
