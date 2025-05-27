package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Main.B.Models

data class A0_DataBasesGroup(
    val d_TarificationInfos: MutableList<D_TarificationInfos> = mutableListOf(),
    val refFireBaseD_TarificationInfos: String = "D_TarificationInfos",

    val a_ProduitInfos: MutableList<A_ProduitInfos> = mutableListOf(),
    val refFireBaseA_ProduitInfos: String = "A_ProduitInfos",

    )

fun getKeyFireBase(
    dataId: Long? = null,
    dataNom: String? = null
): String {
    val cleanedNom =
        (dataNom ?: "").replace(Regex("[.#$\\[\\]/®™©{}\"'`~!@%^&*()+=|\\\\:;<>?]"), "")
            .replace(" ", "_")
            .replace("-", "_")
            .take(50)
            .trim()

    val id = dataId ?: 0

    return when {
        id != 0L && cleanedNom.isNotEmpty() -> "${id}(${cleanedNom})"
        else -> null!!
    }
}
