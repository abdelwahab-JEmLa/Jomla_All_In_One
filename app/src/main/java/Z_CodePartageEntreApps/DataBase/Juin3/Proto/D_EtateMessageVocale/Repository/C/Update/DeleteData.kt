package Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.C.Update

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.Models.D_EtateMessageVocale
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.A.Main.D_EtateMessageVocaleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun D_EtateMessageVocaleRepository.deleteData(data: D_EtateMessageVocale) {
    CoroutineScope(Dispatchers.IO).launch {
        val preparedData = data.withProperKeyFireBaseAndTimeTamp()

        dao.deleteData(preparedData)

        D_EtateMessageVocale.removeRef(preparedData)

        val allData = dao.getAll()
        updateRepoState(allData)
    }
}
