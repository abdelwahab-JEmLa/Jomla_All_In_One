package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.C.Update

import V.DiviseParSections.App.Shared.Repository.HClientInfos
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.A.Main.DataBaseFactoryFClient
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Function.getMaxIdPlus1
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun DataBaseFactoryFClient.addOrUpdateData(data: HClientInfos) {
    CoroutineScope(Dispatchers.IO).launch {
        val dataWhithId = if (data.id == 0L) data.copy(id = getMaxIdPlus1()) else data

        val preparedData = dataWhithId.withProperKeyFireBaseAndTimeTamp()

        dao.upsert(preparedData)

        val allData = dao.getAll()
        updateRepoState(allData)
        repoRef.child(preparedData.keyFireBase).setValue(preparedData)

    }
}

