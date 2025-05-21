package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID2

import V.DiviseParSections.App.SectionID9_AtelieModbile.Models.C3_BonAchate
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

fun testD_TarificationInfosT2(): List<D_TarificationInfosT2> {
    val idProduit1: Long = 4
    val idProduit2: Long = 1
    val idParentBonAchat : Long= 1

    return listOf(
        D_TarificationInfosT2(
            id = 1,
            timestamps = createTimestamp(
                day = 1,
                hour = 12,
                minute = 30
            ),
            idParentProduit = idProduit1,
            idParentBonAchat = idParentBonAchat,
            typeTarificationEnumT2Correspond = TypeTarificationEnumT2.PRIX_BASE,
            prixCurrency = 185.99
        ),
        D_TarificationInfosT2(
            id = 2,

            timestamps = createTimestamp(
                day = 5,
                hour = 13,
                minute = 30
            ),
            idParentProduit = idProduit1,
            idParentBonAchat = idParentBonAchat,
            typeTarificationEnumT2Correspond = TypeTarificationEnumT2.Historique,
            prixCurrency = 190.50
        ),
        D_TarificationInfosT2(
            id = 3,

            timestamps = createTimestamp(
                day = 5,
                hour = 14,
                minute = 30
            ),
            idParentProduit = idProduit1,
            idParentBonAchat = idParentBonAchat,
            typeTarificationEnumT2Correspond = TypeTarificationEnumT2.DEFINI,
            prixCurrency = 195.75
        ),
        D_TarificationInfosT2(
            id = 4,

            timestamps = createTimestamp(
                day = 6,
                hour = 3,
                minute = 30
            ),
            idParentProduit = idProduit1,
            idParentBonAchat = idParentBonAchat,
            typeTarificationEnumT2Correspond = TypeTarificationEnumT2.AU_GERANT,
            prixCurrency = 00.00
        ),
        D_TarificationInfosT2(
            id = 5,

            timestamps = createTimestamp(
                day = 6,
                hour = 4,
                minute = 30
            ),
            idParentProduit = idProduit1,
            idParentBonAchat = idParentBonAchat,
            typeTarificationEnumT2Correspond = TypeTarificationEnumT2.LeMaxPrixArrive,
            prixCurrency = 200.00
        ),

        D_TarificationInfosT2(
            id = 6,

            timestamps = createTimestamp(
                day = 6,
                hour = 4,
                minute = 30
            ),
            idParentProduit = idProduit2,
            idParentBonAchat = idParentBonAchat,
            typeTarificationEnumT2Correspond = TypeTarificationEnumT2.PRIX_BASE,
            prixCurrency = 14.80
        )
        ,
        D_TarificationInfosT2(
            id = 7,

            timestamps = createTimestamp(
                day = 6,
                hour = 4,
                minute = 30
            ),
            idParentProduit = idProduit2,
            idParentBonAchat = idParentBonAchat,
            typeTarificationEnumT2Correspond = TypeTarificationEnumT2.LeMaxPrixArrive,
            prixCurrency = 14.80
        )
    )
}
