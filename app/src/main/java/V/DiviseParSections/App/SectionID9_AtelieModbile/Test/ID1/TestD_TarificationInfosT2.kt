package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1

fun testD_TarificationInfosT2(): List<D_TarificationInfos> {
    val idProduit1: Long = 849
    val idProduit2: Long = 1
    val idParentBonAchat : Long= 1

    return listOf(
        D_TarificationInfos(
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
        D_TarificationInfos(
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
        D_TarificationInfos(
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
        D_TarificationInfos(
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
        D_TarificationInfos(
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

        D_TarificationInfos(
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
        D_TarificationInfos(
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
