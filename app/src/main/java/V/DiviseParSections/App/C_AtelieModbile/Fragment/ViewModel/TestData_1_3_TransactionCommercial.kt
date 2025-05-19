package V.DiviseParSections.App.C_AtelieModbile.Fragment.ViewModel

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.SQL._1_3_TransactionCommercial
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun testData_1_3_TransactionCommercial(): _1_3_TransactionCommercial {
    // Use values from the provided JSON data
    return _1_3_TransactionCommercial(
        vid = 1747588447974L, // Using timestamp from JSON
        clientAcheteurID = 4L, // Using client ID from JSON
        nomClientConcerned = "abdelhamid", // Using client name from JSON
        timestamps = System.currentTimeMillis(),
        heurDebutInString = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
        heurFinInString = "Non Defini",
        cLeDataOuvertDuParentList = true, // Set to true since we need an open transaction
        cActive = true,
        cJustPourVoirPanie = false,
        ouvert = true
        // Using default values for other fields
    )
}
