package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.Test

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.Test.Functions.createTimestamp
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.C3_BonAchat.C3_BonAchate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun testBonAchatT2(): List<C3_BonAchate> {
    return listOf(
        C3_BonAchate(
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
        C3_BonAchate(
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

