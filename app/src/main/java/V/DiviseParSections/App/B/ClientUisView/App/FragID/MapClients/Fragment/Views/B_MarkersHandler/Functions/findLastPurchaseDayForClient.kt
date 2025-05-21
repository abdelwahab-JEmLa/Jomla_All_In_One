package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler.Functions

import V.DiviseParSections.App.SectionID9_AtelieModbile.Models.C3_BonAchate
import Z_CodePartageEntreApps.Modules.DatesHandler
import androidx.compose.runtime.snapshots.SnapshotStateList

data class LastPurchaseInfo(
    val dayName: String = "",
    val timeStr: String = ""
)

fun findLastPurchaseInfoForClient(
    historicalData: SnapshotStateList<C3_BonAchate>,
    clientId: Long
): LastPurchaseInfo {
    // Find the most recent transaction for this client (highest timestamp)
    val lastTransaction = historicalData
        .filter { it.clientAcheteurID == clientId }
        .maxByOrNull { it.timestamps }

    return if (lastTransaction != null) {
        val dateHandler = DatesHandler()

        LastPurchaseInfo(
            dayName = dateHandler.getDateAndTimString(lastTransaction.timestamps).date,
            timeStr = dateHandler.getDateAndTimString(lastTransaction.timestamps).time
        )
    } else {
        LastPurchaseInfo()
    }
}
