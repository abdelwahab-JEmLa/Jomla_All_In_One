package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.G.Function

import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.G.dataBaseCreationFactoryMID2ClientRepository

fun dataBaseCreationFactoryMID2ClientRepository.updateProgress(progress: Float) {
    val currentState = _repoState.value
    if (currentState != null) {
        val newState = currentState.copy(mainProgressRepo = progress)
        _repoState.value = newState
    }
}
