package Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.Function

import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.A.Main.D_EtateMessageVocaleRepository

fun D_EtateMessageVocaleRepository.getMaxIdPlus1(): Long {
    val currentData = _repoState.value?.modelListFlow ?: emptyList()
    return if (currentData.isEmpty()) {
        1L
    } else {
        (currentData.maxOfOrNull { it.id } ?: 0L) + 1L
    }
}
