package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Function

import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.A.Main.B_ClientInfosProtoJuin3Repository

fun B_ClientInfosProtoJuin3Repository.getMaxIdPlus1(): Long {
    val currentData = _repoState.value?.modelListFlow ?: emptyList()
    return if (currentData.isEmpty()) {
        1L
    } else {
        (currentData.maxOfOrNull { it.id } ?: 0L) + 1L
    }
}
