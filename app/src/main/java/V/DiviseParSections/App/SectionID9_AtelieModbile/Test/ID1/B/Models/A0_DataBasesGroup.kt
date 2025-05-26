package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models

import java.util.UUID

data class A0_DataBasesGroup(
    val d_TarificationInfos: MutableList<D_TarificationInfos> = mutableListOf(),
    val refFireBaseD_TarificationInfos: String="D_TarificationInfos",

    val a_ProduitInfos: MutableList<A_ProduitInfos> = mutableListOf(),
    val refFireBaseA_ProduitInfos: String="A_ProduitInfos",

    )
fun getKeyFireBaseSafe(
    dataId: Long? = null,
    dataNom: String? = null
): String {
    val cleanedNom = dataNom?.replace(Regex("[.#$\\[\\]/®™©{}\"'`~!@%^&*()+=|\\\\:;<>?]"), "")
        ?.replace(" ", "_")?.replace("-", "_")?.take(50)?.trim()
        ?: ""

    return when {
        dataId != null && dataId != 0L && cleanedNom.isNotEmpty() -> {
            "ID_${dataId}_${cleanedNom}"
        }
        dataId != null && dataId != 0L -> {
            "ID_${dataId}_${System.currentTimeMillis()}"
        }
        cleanedNom.isNotEmpty() -> {
            "${cleanedNom}_${System.currentTimeMillis()}"
        }
        else -> {
            "ITEM_${UUID.randomUUID().toString().replace("-", "_")}"
        }
    }
}

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

