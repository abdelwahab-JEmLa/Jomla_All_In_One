package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.Models

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
