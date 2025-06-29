package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Function

import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.A.Main.DataBaseFactoryFClient

fun DataBaseFactoryFClient.getMaxIdPlus1(): Long {
    val currentData = _repoState.value?.modelListFlow ?: emptyList()
    return if (currentData.isEmpty()) {
        1L
    } else {
        (currentData.maxOfOrNull { it.id } ?: 0L) + 1L
    }
}
