package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler.Functions

import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.Models._1_3_TransactionCommercial
import Z_CodePartageEntreApps.Modules.DatesHandler
import Z_CodePartageEntreApps.Modules.formatTimeToArabic
import androidx.compose.runtime.snapshots.SnapshotStateList

data class LastPurchaseInfo(
    val dayName: String = "",
    val timeStr: String = ""
)

fun findLastPurchaseInfoForClient(
    historicalData: SnapshotStateList<_1_3_TransactionCommercial>,
    clientId: Long
): LastPurchaseInfo {
    // Find the most recent transaction for this client (highest timestamp)
    val lastTransaction = historicalData
        .filter { it.clientAcheteurID == clientId }
        .maxByOrNull { it.timestamps }

    return if (lastTransaction != null) {
        val dateHandler = DatesHandler()
        val dayName = dateHandler.getArabicDayNameFromTimestamp(lastTransaction.timestamps)
        val timeStr = formatTimeToArabic(lastTransaction.heurDebutInString)

        LastPurchaseInfo(
            dayName = dayName,
            timeStr = timeStr
        )
    } else {
        LastPurchaseInfo()
    }
}
