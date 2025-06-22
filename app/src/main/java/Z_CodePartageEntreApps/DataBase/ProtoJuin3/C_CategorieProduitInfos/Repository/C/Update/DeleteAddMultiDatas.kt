package Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.C.Update

import V.DiviseParSections.App.A.AchatsManager.App.FragID3.CommandeProduits.Package.Juin22.Proto.Repository.CategoriesTabelle
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.A.Main.C_CategorieProduitInfosRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun C_CategorieProduitInfosRepository.deleteAddMultiDatas(
    datas: List<CategoriesTabelle>,
) {
    CoroutineScope(Dispatchers.IO).launch {
        val preparedDatas = datas.map { it.withDernierTimeTampsSynchronisationAvecFireBase() }
        dao.deleteAll()
        dao.insertAll(preparedDatas)

        CategoriesTabelle.safeRemoveRef()
        true.batchFireBaseUpdate(preparedDatas)

        updateRepoState(preparedDatas)
    }
}
