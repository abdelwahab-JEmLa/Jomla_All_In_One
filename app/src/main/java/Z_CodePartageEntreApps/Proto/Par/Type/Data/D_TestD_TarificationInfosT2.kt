package Z_CodePartageEntreApps.Proto.Par.Type.Data

import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import Z_CodePartageEntreApps.Proto.Par.Type.Function.createTimestamp

fun testD_TarificationInfosT2(): List<M13TarificationInfos> {
    val idProduit1: Long = 849
    val parentIdClient: Long = 4

    return listOf(
        M13TarificationInfos(
            creationTimestamps = createTimestamp(
                day = 5,
                hour = 14,
                minute = 30
            ),
            parent_M1Produit_KeyId = idProduit1,
            parent_M2Client_KeyId = parentIdClient,
            typeChoisi = M13TarificationInfos.TypeChoisi.DEFINI,
            prixCurrency = 65.75
        ),
    )
}
