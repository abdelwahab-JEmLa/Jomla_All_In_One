package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.G.Update

import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.G.dataBaseCreationFactoryMID2ClientRepository
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.G.Function.getMaxIdPlus1
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun dataBaseCreationFactoryMID2ClientRepository.addOrUpdateData(data: M2Client) {
    CoroutineScope(Dispatchers.IO).launch {
        val dataWhithId = if (data.id == 0L) data.copy(id = getMaxIdPlus1()) else data

        val preparedData = dataWhithId.withProperKeyFireBaseAndTimeTamp()

        dao.upsert(preparedData)

        val allData = dao.getAll()
        updateRepoState(allData)
        repoRef.child(preparedData.keyFireBase).setValue(preparedData)

    }
}

