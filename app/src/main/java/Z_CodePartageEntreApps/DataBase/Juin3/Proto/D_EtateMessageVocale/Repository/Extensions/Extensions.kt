package Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.Extensions

import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.A.Main.D_EtateMessageVocaleRepository

suspend fun D_EtateMessageVocaleRepository.isRoomEmpty(): Boolean {
    return dao.getCount() == 0
}

fun D_EtateMessageVocaleRepository.updateProgress(progress: Float) {
    val currentState = _repoState.value
    if (currentState != null) {
        val newState = currentState.copy(mainProgressRepo = progress)
        _repoState.value = newState
    }
}


