package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.Z.List.UI.ViewVentCouleur_T1.View

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos

/**
 * Data class to hold the result of depot update operation
 */
data class DepotUpdateResult(
    val success: Boolean,
    val newCount: Int,
    val deficit: Int = 0,
    val message: String = ""
)

/**
 * Updates the depot count for a color/product
 * @param aCentralFacade Central facade for accessing repositories
 * @param relative_M3CouleurInfos The color info to update
 * @param quantityChange The quantity change (negative for sales, positive for additions)
 * @param isAbsoluteValue If true, treat as absolute quantity difference
 * @param active Whether the operation is active (grossist mode)
 * @return DepotUpdateResult containing operation details
 */
fun update_countDepot(
    aCentralFacade: ACentralFacade,
    relative_M3CouleurInfos: M3CouleurProduitInfos,
    quantityChange: Int = 1,
    isAbsoluteValue: Boolean = false,
    active: Boolean
): DepotUpdateResult {

    // If in grossist mode, return success without updating
    if (active) {
        return DepotUpdateResult(
            success = true,
            newCount = relative_M3CouleurInfos.count_Don_Depot,
            message = "عملية في وضع الجملة - لا يوجد تحديث للمخزن"
        )
    }

    // Calculate new count
    val currentDepotCount = relative_M3CouleurInfos.count_Don_Depot

    val newCount = if (isAbsoluteValue) {
        currentDepotCount - quantityChange
    } else {
        currentDepotCount + quantityChange
    }

    // Calculate deficit if attempting to sell more than available
    val deficit = if (newCount < 0) -newCount else 0

    // FIXED: Allow negative depot count to display deficit visually
    // No longer forcing to 0, keep the actual negative value
    val finalDepotCount = newCount // Can be negative now!

    // Generate appropriate message
    val message = when {
        deficit > 0 -> {
            "تنبيه: الكمية المطلوبة ($deficit) غير متوفرة في المخزن. الكمية المتاحة: $currentDepotCount"
        }
        newCount == 0 -> {
            "تنبيه: المخزن أصبح فارغاً لهذا اللون"
        }
        else -> ""
    }

    // Update order from wholesaler if there's a deficit
    val newOrderFromWholesaler = if (deficit > 0) {
        relative_M3CouleurInfos.a_cammende_depuit_grossist + deficit
    } else {
        relative_M3CouleurInfos.a_cammende_depuit_grossist
    }

    // Update the database with actual count (can be negative)
    aCentralFacade.repositorysMainSetter.addOrUpdateData_M3CouleurProduitInfos(
        relative_M3CouleurInfos.copy(
            a_cammende_depuit_grossist = newOrderFromWholesaler,
            count_Don_Depot = finalDepotCount // Now allows negative values!
        )
    )

    return DepotUpdateResult(
        success = deficit == 0,
        newCount = finalDepotCount,
        deficit = deficit,
        message = message
    )
}
