package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.G.Function

import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.G.dataBaseCreationFactoryMID2ClientRepository

fun dataBaseCreationFactoryMID2ClientRepository.getMaxIdPlus1(): Long {
    val currentData = _repoState.value?.modelListFlow ?: emptyList()
    return if (currentData.isEmpty()) {
        1L
    } else {
        (currentData.maxOfOrNull { it.id } ?: 0L) + 1L
    }
}
