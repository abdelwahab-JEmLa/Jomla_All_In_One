package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.C.Update

import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.A.Main.dataBaseCreationFactoryMID2ClientRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun dataBaseCreationFactoryMID2ClientRepository.addOrUpdateDatas(datas: List<M2Client>) {
    CoroutineScope(Dispatchers.IO).launch {
        val preparedDatas = datas.map { it.withProperKeyFireBaseAndTimeTamp() }

        dao.upsertAllDatas(preparedDatas)

        preparedDatas.forEach { data ->
            repoRef.child(data.keyFireBase).setValue(data)
        }

        updateRepoState(preparedDatas)
    }
}
