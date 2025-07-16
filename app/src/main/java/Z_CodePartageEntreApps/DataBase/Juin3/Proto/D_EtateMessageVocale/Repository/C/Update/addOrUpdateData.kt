package Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.C.Update

import V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository.M17MessageVocale
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.A.Main.D_EtateMessageVocaleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun D_EtateMessageVocaleRepository.addOrUpdateData(data: M17MessageVocale) {
    CoroutineScope(Dispatchers.IO).launch {
        dao.upsert(data)
        val allData = dao.getAll()
        updateRepoState(allData)
        repoRef.child(data.keyID).setValue(data)
    }
}

