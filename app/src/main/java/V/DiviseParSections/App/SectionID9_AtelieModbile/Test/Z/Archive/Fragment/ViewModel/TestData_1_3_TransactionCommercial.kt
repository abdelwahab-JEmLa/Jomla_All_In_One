package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.ViewModel

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Models.C3_BonAchate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun testData_1_3_TransactionCommercial(): C3_BonAchate {
    return C3_BonAchate(
        vid = 1747588447974L,
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
