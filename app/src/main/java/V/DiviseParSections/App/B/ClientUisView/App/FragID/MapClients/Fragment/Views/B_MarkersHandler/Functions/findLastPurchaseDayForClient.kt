package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler.Functions

import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import Z_CodePartageEntreApps.Modules.DatesHandler
import androidx.compose.runtime.snapshots.SnapshotStateList

data class LastPurchaseInfo(
    val dayName: String = "",
    val timeStr: String = ""
)

fun findLastPurchaseInfoForClient(
    historicalData: SnapshotStateList<M8BonVent>,
    clientId: Long
): LastPurchaseInfo {
    // Find the most recent transaction for this client (highest timestamp)
    val lastTransaction = historicalData
        .filter { it.parent_M2Client_OldLongID == clientId }
        .maxByOrNull { it.creationTimestamps }

    return if (lastTransaction != null) {
        val dateHandler = DatesHandler()

        LastPurchaseInfo(
            dayName = dateHandler.getDateAndTimString(lastTransaction.creationTimestamps).date,
            timeStr = dateHandler.getDateAndTimString(lastTransaction.creationTimestamps).time
        )
    } else {
        LastPurchaseInfo()
    }
}
