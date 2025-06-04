package Z_CodePartageEntreApps.Proto.Par.Type.Data

import Z_CodePartageEntreApps.Proto.Par.Type.Models.D_TarificationInfos
import Z_CodePartageEntreApps.Proto.Par.Type.Models.TypeTarificationEnumT2
import Z_CodePartageEntreApps.Proto.Par.Type.Function.createTimestamp

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
