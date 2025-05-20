package V.DiviseParSections.App.SectionID9_AtelieModbile.FragID2.Test

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun testBonAchatT2(): List<BonAchatT2> {
    return listOf(
        BonAchatT2(
            vid = 1,
            clientAcheteurID = 4L,
            nomClientConcerned = "abdelhamid",
            timestamps =  createTimestamp(
                day = 4,
                hour = 13,
                minute = 30
            ),
            heurDebutInString = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
            heurFinInString = "Non Defini",
            cActive = true,
            cJustPourVoirPanie = false,
            ouvert = true
        ),
        BonAchatT2(
            vid = 2,
            clientAcheteurID = 5L,
            nomClientConcerned = "sara",
            timestamps =  createTimestamp(
                day = 5,
                hour = 13,
                minute = 30
            ),
            heurDebutInString = "14:30",
            heurFinInString = "Non Defini",
            cActive = true,
            cJustPourVoirPanie = true,
            ouvert = false
        )
    )
}
