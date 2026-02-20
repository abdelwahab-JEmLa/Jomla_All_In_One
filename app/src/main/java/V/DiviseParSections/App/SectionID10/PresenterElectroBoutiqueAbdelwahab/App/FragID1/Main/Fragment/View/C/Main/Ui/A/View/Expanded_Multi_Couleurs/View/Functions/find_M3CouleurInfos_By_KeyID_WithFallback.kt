package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.A.View.Expanded_Multi_Couleurs.View.Functions

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import EntreApps.Shared.Models.M3CouleurProduitInfos
import android.util.Log

/**
 * Extension functions for RepositorysMainGetter to handle intelligent color search
 * Direct matching only - no file decrementing
 */

/**
 * Finds a color by keyID with fallback to similar colors.
 * Searches by:
 * 1. Exact keyID match
 * 2. Same product with same color index
 * 3. Same color name
 * 
 * @param keyID The keyID of the color to find
 * @return The found color, or null if not found
 */
fun RepositorysMainGetter.find_M3CouleurInfos_By_KeyID_WithFallback(
    keyID: String
): M3CouleurProduitInfos? {
    val targetColor = find_M3CouleurInfos_By_KeyID(keyID) ?: return null
    
    // Direct match found - return it
    Log.d("ColorSearch", "Direct match found for $keyID")
    return targetColor
}

/**
 * Finds the best matching color from available colors
 * Priority: keyID > color index > color name
 */
fun RepositorysMainGetter.findBestMatchingColor(
    targetColor: M3CouleurProduitInfos,
    availableColors: List<M3CouleurProduitInfos>
): M3CouleurProduitInfos? {
    // Priority 1: Exact keyID match
    val exactMatch = availableColors.find { it.keyID == targetColor.keyID }
    if (exactMatch != null) {
        Log.d("ColorSearch", "Found exact match: ${exactMatch.keyID}")
        return exactMatch
    }
    
    // Priority 2: Same color index
    val indexMatch = availableColors.find {
        it.indexCouleurDansAncienProto == targetColor.indexCouleurDansAncienProto
    }
    if (indexMatch != null) {
        Log.d("ColorSearch", "Found match via color index: ${indexMatch.keyID}")
        return indexMatch
    }
    
    // Priority 3: Same color name
    if (targetColor.nomCouleurStrSiSonImageDispo.isNotBlank()) {
        val nameMatch = availableColors.find {
            it.nomCouleurStrSiSonImageDispo.equals(
                targetColor.nomCouleurStrSiSonImageDispo,
                ignoreCase = true
            )
        }
        if (nameMatch != null) {
            Log.d("ColorSearch", "Found match via color name: ${nameMatch.keyID}")
            return nameMatch
        }
    }
    
    Log.w("ColorSearch", "No matching color found")
    return null
}

/**
 * Validates and returns a color, or finds the best alternative
 * This is useful when receiving a keyID from payload
 */
fun RepositorysMainGetter.validateAndGetColorOrFallback(
    keyID: String
): M3CouleurProduitInfos? {
    // Try direct lookup first
    val color = find_M3CouleurInfos_By_KeyID(keyID)
    
    if (color == null) {
        Log.e("ColorSearch", "Could not find color for keyID: $keyID")
        return null
    }
    
    Log.d("ColorSearch", "Found color: ${color.keyID}")
    return color
}

/**
 * Finds a color and returns the best match from a specific product's colors
 * Used when you want to ensure the color belongs to a specific product
 */
fun RepositorysMainGetter.findColorInProduct(
    keyID: String,
    productKeyID: String
): M3CouleurProduitInfos? {
    val targetColor = find_M3CouleurInfos_By_KeyID(keyID) ?: return null
    val productColors = find_ListM3CouleurInfos_By_Parent_Produit_KeyID(productKeyID)
    
    return findBestMatchingColor(targetColor, productColors)
}
