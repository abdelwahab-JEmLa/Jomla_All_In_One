package Z_CodePartageEntreApps.Model

import EntreApps.Shared.Models.M13TarificationInfos

data class A0_DataBasesGroup(
    val d_TarificationInfos: MutableList<M13TarificationInfos> = mutableListOf(),
    val refFireBaseD_TarificationInfos: String = "M13TarificationInfos",

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
