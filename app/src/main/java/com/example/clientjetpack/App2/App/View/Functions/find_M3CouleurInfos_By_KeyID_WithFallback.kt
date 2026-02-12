package com.example.clientjetpack.App2.App.View.Functions

import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import android.util.Log
import com.example.clientjetpack.App2.App.A.Main.Base.Repository.RepositorysMainGetter_app2

/**
 * Extension functions for RepositorysMainGetter_app2 to handle intelligent color search
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
fun RepositorysMainGetter_app2.find_M3CouleurInfos_By_KeyID_WithFallback(
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
fun findBestMatchingColor(
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
