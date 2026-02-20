package Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.C.Update

import EntreApps.Shared.Models.M16CategorieProduit
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.A.Main.C_CategorieProduitInfosRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun C_CategorieProduitInfosRepository.deleteAddMultiDatas(
    datas: List<M16CategorieProduit>,
) {
    CoroutineScope(Dispatchers.IO).launch {
        val preparedDatas = datas.map { it.withDernierTimeTampsSynchronisationAvecFireBase() }
        dao.deleteAll()
        dao.insertAll(preparedDatas)

        M16CategorieProduit.safeRemoveRef()
        true.batchFireBaseUpdate(preparedDatas)

        updateRepoState(preparedDatas)
    }
}
