package V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.a

import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos

// Replace the toggle_update_expanded_M3CouleurProduitInfos function in Expanded_Multi_Couleurs.kt
// with this improved version that actually toggles:

fun toggle_update_expanded_M3CouleurProduitInfos(
    focusedValuesGetter: FocusedValuesGetter,
    relative_M3CouleurProduitInfos: M3CouleurProduitInfos
) {
    val currentExpanded = focusedValuesGetter.active_Central_Values.expanded_M3CouleurProduitInfos
    
    // Si la même couleur est déjà expanded, on la met à null (toggle off)
    // Sinon, on met la nouvelle couleur (toggle on)
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
