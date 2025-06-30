package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.C.Update

import V.DiviseParSections.App.Shared.Repository.HClientInfos
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.A.Main.DataBaseFactoryFClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun DataBaseFactoryFClient.addOrUpdateDatas(datas: List<HClientInfos>) {
    CoroutineScope(Dispatchers.IO).launch {
        val preparedDatas = datas.map { it.withProperKeyFireBaseAndTimeTamp() }

        dao.upsertAllDatas(preparedDatas)

        preparedDatas.forEach { data ->
            repoRef.child(data.keyFireBase).setValue(data)
        }

        updateRepoState(preparedDatas)
    }
}
