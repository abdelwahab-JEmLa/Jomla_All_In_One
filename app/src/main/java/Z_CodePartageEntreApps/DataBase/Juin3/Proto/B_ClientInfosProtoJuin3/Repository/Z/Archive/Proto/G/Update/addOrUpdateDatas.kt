package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.G.Update

import EntreApps.Shared.Models.M2Client
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.G.dataBaseCreationFactoryMID2ClientRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun dataBaseCreationFactoryMID2ClientRepository.addOrUpdateDatas(datas: List<M2Client>) {
    CoroutineScope(Dispatchers.IO).launch {
        val preparedDatas = datas.map { it.with_Trigger_RealTime() }

        dao.upsertAllDatas(preparedDatas)

        preparedDatas.forEach { data ->
            repoRef.child(data.keyID).setValue(data)
        }

        updateRepoState(preparedDatas)
    }
}
