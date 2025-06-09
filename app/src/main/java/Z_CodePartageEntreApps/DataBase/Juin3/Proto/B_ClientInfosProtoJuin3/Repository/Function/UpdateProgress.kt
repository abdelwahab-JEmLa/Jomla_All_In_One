package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Function

import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.A.Main.B_ClientInfosProtoJuin3Repository

fun B_ClientInfosProtoJuin3Repository.updateProgress(progress: Float) {
    val currentState = _repoState.value
    if (currentState != null) {
        val newState = currentState.copy(mainProgressRepo = progress)
        _repoState.value = newState
    }
}
