package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1

fun testD_TarificationInfosT2(): List<D_TarificationInfos> {
    val idProduit1: Long = 849
    val idProduit2: Long = 859
    val parentIdClient: Long = 4
    val parentIdClient2: Long = 3

    return listOf(
        D_TarificationInfos(
            timestamps = createTimestamp(
                day = 5,
                hour = 14,
                minute = 30
            ),
            idParentProduit = idProduit1,
            parentIdClient = parentIdClient,
            typeTarificationEnumT2Correspond = TypeTarificationEnumT2.DEFINI,
            prixCurrency = 65.75
        ),
        D_TarificationInfos(
            timestamps = createTimestamp(
                day = 1,
                hour = 13,
                minute = 30
            ),
            idParentProduit = idProduit1,
            parentIdClient = parentIdClient,
            typeTarificationEnumT2Correspond = TypeTarificationEnumT2.Historique,
            prixCurrency = 70.50
        ),

        D_TarificationInfos(
            timestamps = createTimestamp(
                day = 5,
                hour = 13,
                minute = 30
            ),
            idParentProduit = idProduit1,
            parentIdClient = parentIdClient,
            typeTarificationEnumT2Correspond = TypeTarificationEnumT2.Historique,
            prixCurrency = 72.50
        ),

        D_TarificationInfos(
            timestamps = createTimestamp(
                day = 6,
                hour = 4,
                minute = 30
            ),
            idParentProduit = idProduit2,
            parentIdClient = parentIdClient,
            typeTarificationEnumT2Correspond = TypeTarificationEnumT2.Historique,
            prixCurrency = 14.80
        ),
        D_TarificationInfos(
            timestamps = createTimestamp(
                day = 7,
                hour = 13,
                minute = 30
            ),
            idParentProduit = idProduit1,
            parentIdClient = parentIdClient2,
            typeTarificationEnumT2Correspond = TypeTarificationEnumT2.Historique,
            prixCurrency = 77.50          //<--
            //TODO(1): pk le max prix ne s affiche pas pour 
        ),
        )
}
