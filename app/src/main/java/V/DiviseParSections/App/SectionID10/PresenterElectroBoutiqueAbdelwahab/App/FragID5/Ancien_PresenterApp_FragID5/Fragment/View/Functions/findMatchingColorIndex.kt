package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.View.Functions
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos

fun findMatchingColorIndex(
    expandedColor: M3CouleurProduitInfos,
    availableColors: List<M3CouleurProduitInfos>
): Int {
   val exactMatch = availableColors.indexOfFirst { it.keyID == expandedColor.keyID }
   if (exactMatch != -1) return exactMatch

   val indexMatch = availableColors.indexOfFirst {
       it.parentBProduitOldID == expandedColor.parentBProduitOldID &&
               it.indexCouleurDansAncienProto == expandedColor.indexCouleurDansAncienProto
   }
   if (indexMatch != -1) return indexMatch

   if (expandedColor.nomCouleurStrSiSonImageDispo.isNotBlank()) {
       val colorNameMatch = availableColors.indexOfFirst {
           it.nomCouleurStrSiSonImageDispo.equals(
               expandedColor.nomCouleurStrSiSonImageDispo,
               ignoreCase = true
           )
       }
       if (colorNameMatch != -1) return colorNameMatch
   }

   return -1
}
