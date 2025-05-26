package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models

data class A0_DataBasesGroup(
    val d_TarificationInfos: MutableList<D_TarificationInfos> = mutableListOf(),
    val refFireBaseD_TarificationInfos: String="D_TarificationInfos",

    val a_ProduitInfos: MutableList<A_ProduitInfos> = mutableListOf(),
    val refFireBaseA_ProduitInfos: String="A_ProduitInfos",

    )


fun getKeyFireBase(
    dataId: Long? = null,
    dataNom: String? = null
): String {
    return if (dataId != null) {
        "-<$dataId($dataNom)"
    } else {
        "-<$dataNom"
    }
}

fun getKeyFireBaseSafe(
    dataId: Long? = null,
    dataNom: String? = null
): String {
    val cleanedNom = dataNom?.let { nom ->
        nom.replace(Regex("[®™©\\[\\]{}\"'`~!@#$%^&*()+=|\\\\:;\"'<>?/]"), "")
            .replace(" ", "_")
            .take(50)
    } ?: ""

    return if (dataId != null && dataId != 0L) {
        "-<${dataId}(${cleanedNom})"
    } else {
        "-<${cleanedNom}_${System.currentTimeMillis()}"
    }
}
