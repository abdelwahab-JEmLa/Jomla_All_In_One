package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.C.Update

import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.A.Main.B_ClientInfosProtoJuin3
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.A.Main.B_ClientInfosProtoJuin3Repository
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Function.getMaxIdPlus1
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun B_ClientInfosProtoJuin3Repository.addOrUpdateData(data: B_ClientInfosProtoJuin3) {
    CoroutineScope(Dispatchers.IO).launch {
        val dataWhithId = if (data.id == 0L) data.copy(id = getMaxIdPlus1()) else data

        val preparedData = dataWhithId.withProperKeyFireBaseAndTimeTamp()

        dao.upsert(preparedData)

        val allData = dao.getAll()
        updateRepoState(allData)
        repoRef.child(preparedData.keyFireBase).setValue(preparedData)

    }
}

