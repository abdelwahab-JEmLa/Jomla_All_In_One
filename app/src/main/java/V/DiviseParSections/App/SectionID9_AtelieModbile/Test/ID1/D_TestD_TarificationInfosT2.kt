package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1

fun testD_TarificationInfosT2(): List<D_TarificationInfos> {
    val idProduit1: Long = 849
    val parentIdClient: Long = 4

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
        )
}
