package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler.Functions

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.GTransactionVent
import Z_CodePartageEntreApps.Modules.DatesHandler
import androidx.compose.runtime.snapshots.SnapshotStateList

data class LastPurchaseInfo(
    val dayName: String = "",
    val timeStr: String = ""
)

fun findLastPurchaseInfoForClient(
    historicalData: SnapshotStateList<GTransactionVent>,
    clientId: Long
): LastPurchaseInfo {
    // Find the most recent transaction for this client (highest timestamp)
    val lastTransaction = historicalData
        .filter { it.parentHClientOldID == clientId }
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
