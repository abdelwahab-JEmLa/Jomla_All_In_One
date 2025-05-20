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
            timestamps = System.currentTimeMillis(),
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
            timestamps = System.currentTimeMillis() - 86400000, // Yesterday
            heurDebutInString = "14:30",
            heurFinInString = "Non Defini",
            cActive = true,
            cJustPourVoirPanie = true,
            ouvert = false
        )
    )
}

fun testD_TarificationInfosT2(): List<D_TarificationInfosT2> {
    val idProduit: Long = 4

    return listOf(
        D_TarificationInfosT2(
            vidTimestamp = createTimestamp(
                day = 1,
                hour = 12,
                minute = 30
            ),
            idParentProduit = idProduit,
            idParentBonAchat = testBonAchatT2()[0].vid,
            typeTarificationEnumT2Correspond = TypeTarificationEnumT2.ParBenifice,
            prixCurrency = 20.99
        ),
        D_TarificationInfosT2(
            vidTimestamp = createTimestamp(
                day = 5,
                hour = 13,
                minute = 30
            ),
            idParentProduit = idProduit,
            idParentBonAchat = testBonAchatT2()[0].vid,
            typeTarificationEnumT2Correspond = TypeTarificationEnumT2.PRIX_BASE,
            prixCurrency = 200.50
        ),
        D_TarificationInfosT2(
            vidTimestamp = createTimestamp(
                day = 5,
                hour = 14,
                minute = 30
            ),
            idParentProduit = idProduit,
            idParentBonAchat = testBonAchatT2()[0].vid,
            typeTarificationEnumT2Correspond = TypeTarificationEnumT2.Historique,
            prixCurrency = 250.75
        ),
        D_TarificationInfosT2(
            vidTimestamp = createTimestamp(
                day = 6,
                hour = 3,
                minute = 30
            ),
            idParentProduit = 2,
            idParentBonAchat = testBonAchatT2()[0].vid,
            typeTarificationEnumT2Correspond = TypeTarificationEnumT2.ParBenifice,
            prixCurrency = 15.25
        ),
        D_TarificationInfosT2(
            vidTimestamp = createTimestamp(
                day = 6,
                hour = 4,
                minute = 30
            ),
            idParentProduit = 3L,
            idParentBonAchat = testBonAchatT2()[0].vid,
            typeTarificationEnumT2Correspond = TypeTarificationEnumT2.LeMaxPrixArrive,
            prixCurrency = 14.80
        )
    )
}
