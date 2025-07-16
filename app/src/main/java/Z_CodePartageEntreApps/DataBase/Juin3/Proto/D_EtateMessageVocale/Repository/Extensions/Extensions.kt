package Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.Extensions

import V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository.Repo17MessageVocale

suspend fun Repo17MessageVocale.isRoomEmpty(): Boolean {
    return dao.getCount() == 0
}

fun Repo17MessageVocale.updateProgress(progress: Float) {
    val currentState = _repoState.value
    if (currentState != null) {
        val newState = currentState.copy(mainProgressRepo = progress)
        _repoState.value = newState
    }
}


