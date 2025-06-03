package A.AtelierMobile.Test.ID1.Test.Shared.DataBase.Fonctions.Main


fun getKeyFireBase(dataId: Long? = null, dataNom: String? = null): String {
    val cleanedNom = (dataNom ?: "")
        .replace(Regex("[.#\$\\[\\]/®™©{}\"'`~!@%^&*()+=|\\\\:;<>?-]"), "")
        .replace(Regex("\\s+"), "_")
        .replace(Regex("_+"), "_")
        .trim('_')
        .take(40)

    val id = dataId ?: 0

    return when {
        id > 0 && cleanedNom.isNotEmpty() -> "${id}_${cleanedNom}"
        id > 0 -> "${id}_category"
        cleanedNom.isNotEmpty() -> "temp_${cleanedNom}"
        else -> throw IllegalArgumentException("Invalid ID or name")
    }
}
