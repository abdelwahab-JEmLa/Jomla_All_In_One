package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.C.Update

import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.A.Main.B_ClientInfosProtoJuin3
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.A.Main.B_ClientInfosProtoJuin3Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun B_ClientInfosProtoJuin3Repository.addOrUpdateDatas(datas: List<B_ClientInfosProtoJuin3>) {
    CoroutineScope(Dispatchers.IO).launch {
        val preparedDatas = datas.map { it.withProperKeyFireBaseAndTimeTamp() }

        dao.upsertAllDatas(preparedDatas)

        preparedDatas.forEach { data ->
            repoRef.child(data.keyFireBase).setValue(data)
        }

        updateRepoState(preparedDatas)
    }
}
