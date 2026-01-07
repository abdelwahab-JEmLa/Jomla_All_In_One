package V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.a

import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos


fun toggle_update_expanded_M3CouleurProduitInfos(
    focusedValuesGetter: FocusedValuesGetter,
    relative_M3CouleurProduitInfos: M3CouleurProduitInfos
) {
    val currentExpanded = focusedValuesGetter.active_Central_Values.expanded_M3CouleurProduitInfos
    
    val newValue = if (currentExpanded?.keyID == relative_M3CouleurProduitInfos.keyID) {
        null
    } else {
        relative_M3CouleurProduitInfos
    }
    
    focusedValuesGetter.update_activeCentralValues(
        focusedValuesGetter.active_Central_Values.copy(
            expanded_M3CouleurProduitInfos = newValue
        )
    )
}
