package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.Components.Expand_Produit_Couleur

import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.a.toggle_update_expanded_M3CouleurProduitInfos

fun updateExpandedCouleur(
    focusedValuesGetter: FocusedValuesGetter,
    relative_M3CouleurProduitInfos: M3CouleurProduitInfos,

) {
    toggle_update_expanded_M3CouleurProduitInfos(
        focusedValuesGetter = focusedValuesGetter,
        relative_M3CouleurProduitInfos = relative_M3CouleurProduitInfos
    )

}
