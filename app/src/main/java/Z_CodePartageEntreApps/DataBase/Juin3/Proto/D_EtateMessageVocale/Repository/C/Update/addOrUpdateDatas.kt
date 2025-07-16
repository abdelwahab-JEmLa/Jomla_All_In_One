package Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.C.Update

import V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository.M17MessageVocale
import V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository.Repo17MessageVocale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun Repo17MessageVocale.addOrUpdateDatas(datas: List<M17MessageVocale>) {
    CoroutineScope(Dispatchers.IO).launch {

        dao.upsertAllDatas(datas)

        datas.forEach { data ->
            repoRef.child(data.keyID).setValue(data)
        }

        updateRepoState(datas)
    }
}
